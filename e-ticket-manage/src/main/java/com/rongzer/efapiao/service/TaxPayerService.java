package com.rongzer.efapiao.service;
import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.dao.TaxPayerMapper;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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
public class TaxPayerService extends BaseBusinessService {
    @Autowired
    private TaxPayerMapper taxPayerMapper;

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
                Map<String, Object> taxpayer = null;
                //新的纳税人信息
                List<Map<String, Object>> insertTaxpayerList = null;
                //旧的纳税人信息
                List<Map<String, Object>> updateTaxpayerList = null;

                //查询t_taxpayer_info 表中的所有纳税人信息税号
                List<String> nsrsbhList = taxPayerMapper.listNsrsbh();

                //遍历EXCEL中记录，如果数据库中包含记录则做更新，如果不包含则做新增（根据税号）
                String  nsrsbh = null;
                for (int i = 1; i <= rowNum; i++) {
                    row = sheet.getRow(i);

                    //必填项：纳税人识别号，纳税人名称，纳税人电话，纳税人开户行，纳税人开户帐号，纳税人类型，单张发票限额
                    Cell cell0 = row.getCell(0);
                    Cell cell1 = row.getCell(1);
                    //纳税人识别号
                    Cell cell2 = row.getCell(2);
                    //纳税人名称
                    Cell cell3 = row.getCell(3);

                    Cell cell4 = row.getCell(4);
                    //纳税人电话
                    Cell cell5 = row.getCell(5);
                    //纳税人开户行
                    Cell cell6 = row.getCell(6);
                    //纳税人开户帐号
                    Cell cell7 = row.getCell(7);
                    //纳税人类型
                    Cell cell8 = row.getCell(8);
                    //单张发票限额
                    Cell cell9 = row.getCell(9);
                    Cell cell10 = row.getCell(10);
                    Cell cell11 = row.getCell(11);
                    Cell cell12 = row.getCell(12);
                    Cell cell13 = row.getCell(13);
                    Cell cell14 = row.getCell(14);
                    if (StringUtil.isEmpty(cell2)
                            || StringUtil.isEmpty(cell3)
                            || StringUtil.isEmpty(cell5)
                            || StringUtil.isEmpty(cell6)
                            || StringUtil.isEmpty(cell7)
                            || StringUtil.isEmpty(cell8)
                            || StringUtil.isEmpty(cell9)) {
                        resultBuffer.append("第" + i + "行信息不完全；\r\n");
                        continue;
                    }
                    //如果发现excel中有不完整信息，都查找出来给予用户提示
                    if(StringUtil.isNotEmpty(resultBuffer.toString())){
                        continue;
                    }

                    if (cell0 != null) {
                        cell0.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if (cell1 != null) {
                        cell1.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    cell2.setCellType(Cell.CELL_TYPE_STRING);
                    cell3.setCellType(Cell.CELL_TYPE_STRING);
                    if (cell4 != null) {
                        cell4.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    cell5.setCellType(Cell.CELL_TYPE_STRING);
                    cell6.setCellType(Cell.CELL_TYPE_STRING);
                    cell7.setCellType(Cell.CELL_TYPE_STRING);
                    cell8.setCellType(Cell.CELL_TYPE_STRING);
                    cell9.setCellType(Cell.CELL_TYPE_STRING);
                    if (cell10 != null) {
                        cell10.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if (cell11 != null) {
                        cell11.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if (cell12 != null) {
                        cell12.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if (cell13 != null) {
                        cell13.setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if (cell14 != null) {
                        cell14.setCellType(Cell.CELL_TYPE_STRING);
                    }

                    taxpayer = new HashMap<String,Object>();
                    taxpayer.put("TAXPAYER_PROVINCE",cell0==null?"":cell0.getStringCellValue());
                    taxpayer.put("TAXPAYER_CITY",cell1==null?"":cell1.getStringCellValue());
                    taxpayer.put("TAXPAYER_IDENTIFY_NO", cell2.getStringCellValue());
                    taxpayer.put("TAXPAYER_NAME_CN",cell3.getStringCellValue());
                    taxpayer.put("TAXPAYER_ADDRESS",cell4==null?"":cell4.getStringCellValue());
                    taxpayer.put("TAXPAYER_PHONE",cell5.getStringCellValue());
                    taxpayer.put("TAXPAYER_BANK",cell6.getStringCellValue());
                    taxpayer.put("TAXPAYER_ACCOUNT",cell7.getStringCellValue());
                    taxpayer.put("TAXPAYER_TYPE",cell8.getStringCellValue());
                    taxpayer.put("INVOICE_LIMIT_AMOUNT",cell9.getStringCellValue());
                    taxpayer.put("IS_EFAPIAO",cell10==null?"":cell10.getStringCellValue());
                    taxpayer.put("PLATFORM_TYPE",cell11==null?"":cell11.getStringCellValue());
                    taxpayer.put("PLATFORM_CODE",cell12==null?"":cell12.getStringCellValue());
                    taxpayer.put("REGISTRATION_CODE",cell13==null?"":cell13.getStringCellValue());
                    taxpayer.put("AUTHORIZATION_CODE",cell14==null?"":cell14.getStringCellValue());

                    //纳税人识别号
                    nsrsbh = cell2.getStringCellValue();
                    //如果表中有记录则更新
                    if(nsrsbhList.contains(nsrsbh)){
                        if(updateTaxpayerList == null){
                            updateTaxpayerList = new ArrayList<Map<String, Object>>();
                        }
                        taxpayer.put("UPDATE_USER",userId);
                        taxpayer.put("UPDATE_TIME",StringUtil.getNowTime());
                        updateTaxpayerList.add(taxpayer);
                    }else{
                        if(insertTaxpayerList == null){
                            insertTaxpayerList = new ArrayList<Map<String, Object>>();
                        }
                        taxpayer.put("TAXPAYER_ID",StringUtil.getUuid32());
                        taxpayer.put("ADD_USER",userId);
                        taxpayer.put("ADD_TIME",StringUtil.getNowTime());
                        taxpayer.put("IS_USED","D00002");
                        insertTaxpayerList.add(taxpayer);
                    }

                    //每100条批量插入一次
                    if(CollectionUtil.isNotEmpty(insertTaxpayerList) && (insertTaxpayerList.size()%100 == 0 || i == rowNum)){
                        taxPayerMapper.insertTaxPayer(insertTaxpayerList);
                    }
                    //每100条批量更新一次
                    if(CollectionUtil.isNotEmpty(updateTaxpayerList) && (updateTaxpayerList.size()%100 == 0 || i == rowNum)){
                        taxPayerMapper.updateTaxPayer(updateTaxpayerList);
                    }
                }

                if (CollectionUtil.isEmpty(updateTaxpayerList) && CollectionUtil.isEmpty(insertTaxpayerList) ) {
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

    public Map<String,Object> getTaxpayerById(Map<String,Object> paramMap){
        return taxPayerMapper.getTaxpayerById(paramMap);
    }
}
