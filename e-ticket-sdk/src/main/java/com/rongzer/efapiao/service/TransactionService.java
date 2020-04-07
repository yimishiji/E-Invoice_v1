package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.util.DeflaterZipUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("transactionService")
public class TransactionService extends BaseBusinessService {

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
     * 处理交易数据,保存入库，生成发票订单数据
     * @param paramMap
     * @return
     */
    public Map<String, Object> dealTransaction(Map<String, Object> paramMap) {

        String cookieValue = (String) paramMap.get("COOKIE_VALUE");//获取Cookie
        String json = (String) paramMap.get("JSON");
        Map<String,Object> transactionMap = new HashMap<String,Object>();
        try {
            //解压缩json数据
            byte[] base64ByteJson = Base64.decodeBase64(json);
            byte[] jsonDeflater = DeflaterZipUtil.uncompress(base64ByteJson);
            json = new String(jsonDeflater);
            //判断传递数据正确性
            transactionMap = JSONUtil.json2Map(json,transactionMap);
            transactionMap = convertOrderMap(transactionMap);
        }catch (Exception e){
        }
        return transactionMap;
    }

    public Map<String,Object> convertOrderMap(Map<String,Object> orderMap){
        Map<String,Object> returnMap = new HashMap<String,Object>();

        returnMap.put("TRANSACTION_NUMBER",orderMap.get("tn"));
        returnMap.put("TRANSACTION_DATETIME",orderMap.get("tt"));
        returnMap.put("STORE_NUMBER",orderMap.get("sn"));
        returnMap.put("POS_NAME",orderMap.get("pn"));
        returnMap.put("TRANSACTION_AMOUNT",orderMap.get("ta"));
        returnMap.put("EMPLOYEE_ID",orderMap.get("eid"));
        returnMap.put("EMPLOYEE_NAME",orderMap.get("en"));
        returnMap.put("SERVICE_TYPE",orderMap.get("st"));

        List<Map<String,Object>> tItemsListTmp = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> tItemsList = new ArrayList<Map<String,Object>>();
        tItemsListTmp = (List<Map<String, Object>>) orderMap.get("ti");

        for(Map<String,Object> tMap :tItemsListTmp){
            Map<String,Object> tItem = new HashMap<String,Object>();
            tItem.put("SALESITEM_CODE",tMap.get("ic"));
            tItem.put("SALESITEM_QUANTITY",tMap.get("iq"));
            tItem.put("SALESITEM_AMOUNT",tMap.get("ia"));
            tItem.put("SALESITEM_AMOUNT_AFTER_DISCOUNT",tMap.get("iad"));

            List<Map<String,Object>> dItemsListTmp = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> dItemsList = new ArrayList<Map<String,Object>>();
            dItemsListTmp = (List<Map<String, Object>>) tMap.get("di");
            for(Map<String,Object> dMap :dItemsListTmp){
                Map<String,Object> dItem = new HashMap<String,Object>();
                dItem.put("DISCOUNTITEM_CODE",dMap.get("ic"));
                dItem.put("DISCOUNTITEM_QUANTITY",dMap.get("iq"));
                dItem.put("DISCOUNTITEM_AMOUNT",dMap.get("ia"));
                dItemsList.add(dItem);
            }
            tItem.put("DISCOUNTITEMS",dItemsList);
            tItemsList.add(tItem);
        }
        returnMap.put("TRANSACTIONITEMS",tItemsList);

        List<Map<String,Object>> pItemsListTmp = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> pItemsList = new ArrayList<Map<String,Object>>();
        pItemsListTmp = (List<Map<String, Object>>) orderMap.get("pi");
        for(Map<String,Object> pMap :pItemsListTmp){
            Map<String,Object> pItem = new HashMap<String,Object>();
            pItem.put("PAYMENTITEM_CODE",pMap.get("ic"));
            pItem.put("PAYMENTITEM_QUANTITY",pMap.get("iq"));
            pItem.put("PAYMENTITEM_AMOUNT",pMap.get("ia"));
            pItemsList.add(pItem);
        }
        returnMap.put("PAYMENTITEMS",pItemsList);
        return returnMap;
    }



}
