package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.dao.InvoiceContentMapper;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ${DESCRIPTION}
 *
 * @author heps
 * @create 2017-06-01 11:08
 **/
@Service
public class InvoiceContentService extends BaseBusinessService {
    @Autowired
    private InvoiceContentMapper invoiceContentMapper;

    @Override
    protected Map<String, Object> process(Map<String, Object> paramMap) {
        if (!paramMap.containsKey("method")) {
        } else {
            String methodName = (String) paramMap.get("method");
            try {
                Method method = this.getClass().getMethod(methodName, Map.class);
                paramMap = (Map<String, Object>) method.invoke(this, paramMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paramMap;
    }

    /**
     * 导入纳税人信息
     * @param path
     * @param fileName
     * @param userId
     * @return
     */
    public String readExcel(String path, String fileName, String userId) throws IOException, ParseException {
        Workbook wb;
        Sheet sheet;
        Row row;
        InputStream is = null;
        StringBuffer resultBuffer = new StringBuffer();
        try {
            is = new FileInputStream(path);
            if (fileName.endsWith("xls")) {
                wb = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                wb = new XSSFWorkbook(is);
            } else {
                resultBuffer.append("读取的文件不是excel文件，请导入正确的文件格式");
                return resultBuffer.toString();
            }
            // 得到总行数,不大于1的时候无数据要导入
            sheet = wb.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            if (rowNum < 1) {
                resultBuffer.append("excel中没有要导入的数据");
            } else {
                Map<String, Object> invoiceContent = null;
                //新的纳税人信息
                List<Map<String, Object>> insertInvoiceContentList = null;
                //旧的纳税人信息
                List<Map<String, Object>> updateInvoiceContentList = null;

                //查询t_invoice_content 表中的所有商品编码
                List<String> contentCodeList = invoiceContentMapper.listContentCode();

                //遍历EXCEL中记录，如果数据库中包含记录则做更新，如果不包含则做新增（根据税号）
                String  contentCode = null;
                for (int i = 1; i <= rowNum; i++) {
                    row = sheet.getRow(i);

                    //content_code
                    Cell cell0 = row.getCell(0);
                    //content_name_cn
                    Cell cell1 = row.getCell(1);
                    //tax_rate 普通纳税人税率
                    Cell cell2 = row.getCell(2);
                    //小额纳税人税率
                    Cell cell3 = row.getCell(3);

                    if (StringUtil.isEmpty(cell0)
                            || StringUtil.isEmpty(cell1)
                            || StringUtil.isEmpty(cell2)
                            ) {
                        resultBuffer.append("第" + i + "行信息不完全；\r\n");
                        continue;
                    }
                    //如果发现excel中有不完整信息，都查找出来给予用户提示
                    if(StringUtil.isNotEmpty(resultBuffer.toString())){
                        continue;
                    }

                    cell0.setCellType(Cell.CELL_TYPE_STRING);
                    cell1.setCellType(Cell.CELL_TYPE_STRING);
                    cell2.setCellType(Cell.CELL_TYPE_STRING);
                    cell3.setCellType(Cell.CELL_TYPE_STRING);

                    invoiceContent = new HashMap<String,Object>();
                    invoiceContent.put("CONTENT_CODE",cell0.getStringCellValue());
                    invoiceContent.put("CONTENT_NAME_CN",cell1.getStringCellValue());
                    invoiceContent.put("TAXRATE", cell2.getStringCellValue());
                    invoiceContent.put("SMALL_TAXRATE",cell3.getStringCellValue());

                    contentCode = cell0.getStringCellValue();
                    //如果表中有记录则更新
                    if(contentCodeList.contains(contentCode)){
                        if(updateInvoiceContentList == null){
                            updateInvoiceContentList = new ArrayList<Map<String, Object>>();
                        }
                        invoiceContent.put("UPDATE_USER",userId);
                        invoiceContent.put("UPDATE_TIME",StringUtil.getNowTime());
                        updateInvoiceContentList.add(invoiceContent);
                    }else{
                        if(insertInvoiceContentList == null){
                            insertInvoiceContentList = new ArrayList<Map<String, Object>>();
                        }
                        invoiceContent.put("CONTENT_ID",StringUtil.getUuid32());
                        invoiceContent.put("ADD_USER",userId);
                        invoiceContent.put("ADD_TIME",StringUtil.getNowTime());
                        invoiceContent.put("IS_USED","D00002");
                        invoiceContent.put("IS_DELETE","0");
                        insertInvoiceContentList.add(invoiceContent);
                    }

                    //每100条批量插入一次
                    if(CollectionUtil.isNotEmpty(insertInvoiceContentList) && (insertInvoiceContentList.size()%100 == 0 || i == rowNum)){
                        invoiceContentMapper.insertInvoiceContent(insertInvoiceContentList);
                    }
                    //每100条批量更新一次
                    if(CollectionUtil.isNotEmpty(updateInvoiceContentList) && (updateInvoiceContentList.size()%100 == 0 || i == rowNum)){
                        invoiceContentMapper.updateInvoiceContent(updateInvoiceContentList);
                    }
                }

                if (CollectionUtil.isEmpty(updateInvoiceContentList) && CollectionUtil.isEmpty(insertInvoiceContentList) ) {
                    resultBuffer.append("没有需要导入的数据，请检查文件！");
                }
            }
        } catch (Exception e) {
            resultBuffer.append("文件读取异常，请联系管理员！");
            e.printStackTrace();
        } finally {
            if (is != null) is.close();
        }
        return resultBuffer.toString();
    }
}
