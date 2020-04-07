package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.dao.InvoiceCheckMapper;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("invoiceCheckService")
public class InvoiceCheckService extends BaseBusinessService {

    @Autowired
    private InvoiceCheckMapper invoiceCheckMapper;

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
     * @param paramMap
     * @return
     */
    public Map<String, Object> getInvoices(Map<String, Object> paramMap) {
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("RESULT_CODE","10000");
        List<Map<String, Object>> invoices = (List<Map<String, Object>>) paramMap.get("invoices");

        List<Map<String,Object>> invoiceRegisterList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> invoiceTmp : invoices) {
            Map<String,Object> invoiceRegister = invoiceCheckMapper.getInvoiceRegister(invoiceTmp);
            /**
             * 如果为空,说明发票之前没有查验过，分两种情况
             * 1.如果不是本平台开具的发票，给予提示。
             * 2.如果是本平台开具的发票，将查验发票数据保存进数据库。
             *
             * 如果不为空，说明发票之前查验过，将发票信息显示在界面上，并且给予提示。
             */
            Map<String,Object> invoice = invoiceCheckMapper.getInvoice(invoiceTmp);
            if(CollectionUtil.isEmpty(invoiceRegister)){
                if(CollectionUtil.isEmpty(invoice)){
                    //为空则此发票不是平台开具
                    returnMap.put("RESULT_CODE","10001");
                    returnMap.put("RESULT_MESSAGE","发票"+invoiceTmp.get("INVOICE_CODE")+","+invoiceTmp.get("INVOICE_NUMBER")+"不是平台开具");
                    return  returnMap;
                }else{
                    invoiceTmp.put("ID", StringUtil.getUuid32());
                    invoiceTmp.put("INVOICE_TYPE",invoice.get("INVOICE_TYPE"));
                    invoiceTmp.put("INVOICE_AMOUNT",invoice.get("TOTAL_AMOUNT"));
                    invoiceTmp.put("INVOICE_DATE",invoice.get("INVOICE_TIME"));
                    invoiceTmp.put("ADD_USER",paramMap.get("userId"));
                    invoiceTmp.put("ADD_TIME",StringUtil.getNowTime());
                    invoiceTmp.put("UPDATE_USER",paramMap.get("userId"));
                    invoiceTmp.put("UPDATE_TIME",StringUtil.getNowTime());
                    invoiceTmp.put("PURCHASER_NAME",invoice.get("PURCHASER_NAME"));
                    invoiceTmp.put("INVOICE_STATUS_CN",invoice.get("INVOICE_STATUS_CN"));
                    invoiceTmp.put("IS_DELETE","0");
                    //保存进invoiceRegister
                    invoiceRegisterList.add(invoiceTmp);
                }
            }else{
                returnMap.put("RESULT_CODE","10002");
                returnMap.put("RESULT_MESSAGE","发票"+invoiceTmp.get("INVOICE_CODE")+","+invoiceTmp.get("INVOICE_NUMBER")+"已经入账，请确认！");
                returnMap.put("registerinvoice",invoice);
                return returnMap;
            }
        }

        //如果校验没有问题，则将查验发表插入表
        if(CollectionUtil.isNotEmpty(invoiceRegisterList)){
            for(Map<String, Object> invoiceRegisterTemp : invoiceRegisterList) {
                invoiceCheckMapper.saveInvoiceRegister(invoiceRegisterTemp);
            }
            returnMap.put("registerinvoicelist",invoiceRegisterList);
        }
        return returnMap;
    }
}
