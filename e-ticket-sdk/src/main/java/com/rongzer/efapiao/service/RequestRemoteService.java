package com.rongzer.efapiao.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.dao.RequestRemoteMapper;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.service.RDPBaseService;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.HttpUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("requestRemoteService")
public class RequestRemoteService extends BaseBusinessService implements RDPBaseService{
	
	@Autowired
	private RequestRemoteMapper requestRemoteMapper;
	
    @SuppressWarnings("unchecked")
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
	 * BW交易数据抓取,入库
	 * @param pickupCode提取码
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public Map<String, Object> getTransDataFromBw(Map<String, Object> paramMap) {
		String pickUpCode = (String) paramMap.get("PICKUPCODE");		
		Map<String, Object> invoiceServiceParamMap = new HashMap<String, Object>();
		invoiceServiceParamMap.put("PICKUPCODE", pickUpCode);
		invoiceServiceParamMap.put("method", "getTransaction");
		Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
		if(tempResult.get("transactionData") == null){			
			getTransDataFromInterface(paramMap);
			tempResult = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
		}
		return null;
	}
	
	public Map<String, Object> getTransDataFromInterface(Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Transaction> returnList = new ArrayList<Transaction>();
		try {
			StringBuffer orderUrl = new StringBuffer(RDPUtil.getSysConfig("efapiao.shinho.orderInfo"));
			String openId = StringUtil.safeToString(paramMap.get("openId"));
			String serialNo = StringUtil.safeToString(paramMap.get("PICKUPCODE"));
			orderUrl.append("openId=").append(openId).append("&serialNo=").append(serialNo);
			List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
			String orderStr=HttpUtil.readContentFromGet(orderUrl.toString());
			Map<String, String> returnmap = JSONUtil.json2Map(orderStr);
			if(CollectionUtil.isNotEmpty(returnmap)&&"1".equals(returnmap.get("status"))){
				orderList = JSONUtil.getJSONArrayFromStr(returnmap.get("data"));
				for(int i = 0 ; i < orderList.size() ; i++) {
					Map<String, Object> orderMap =  orderList.get(i);
					Map<String, Object> transMap = (Map<String, Object>) orderMap.get("cashMainVo");
					String TRANSACTION_ID = StringUtil.getUuid32();
					transMap.put("TRANSACTION_ID", TRANSACTION_ID);
					transMap.put("NOWTIME", StringUtil.getNowTime());
					transMap.put("TRANSACTION_NUMBER", transMap.get("serialNo"));
					transMap.put("TRANSACTION_DATETIME", transMap.get("buyTime"));
					transMap.put("TRANSACTION_MONTH", StringUtil.safeToString(transMap.get("buyTime")).substring(0, 7));
					transMap.put("STORE_NUMBER", transMap.get("storeId"));
					transMap.put("STORE_NAME_CN", transMap.get("storeName"));
					transMap.put("STORE_NAME_EN", transMap.get("storeNameEn"));
					transMap.put("POS_NAME", transMap.get("posCode"));
					transMap.put("TRANSACTION_AMOUNT", transMap.get("amount"));
					transMap.put("openId", openId);
					String pickcode=(String) transMap.get("serialNo");
					Map<String, Object> invoiceServiceParamMap = new HashMap<String, Object>();
					invoiceServiceParamMap.put("PICKUPCODE", pickcode);
					invoiceServiceParamMap.put("WITHOUT_CACHE", "true");
					invoiceServiceParamMap.put("method", "getTransaction");
					Map<String, Object> execBaseBizService = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
					if(StringUtil.isEmpty(execBaseBizService.get("transactionData"))){
						List<Map<String, Object>> foodVo =(List<Map<String, Object>>) orderMap.get("foodVo");
						for (Map<String, Object> foodmap : foodVo) {
							String ITEM_ID = StringUtil.getUuid32();
							foodmap.put("TRANSACTION_ID", TRANSACTION_ID);
							foodmap.put("ITEM_ID", ITEM_ID);
						}
						List<Map<String, Object>> payTypeVo = (List<Map<String, Object>>) orderMap.get("payTypeVo");
						//获取基础数据
				        Map<String, Object> defaultPayments = new HashMap<String, Object>();
				        defaultPayments = RDPUtil.execBaseBizService("eFapiaoBaseService", "getPaymentMap", defaultPayments);
						BigDecimal dealAmount = BigDecimal.ZERO;
				        for (Map<String, Object> paymap : payTypeVo) {
							String PAYMENT_ID = StringUtil.getUuid32();
							paymap.put("TRANSACTION_ID", TRANSACTION_ID);
							paymap.put("PAYMENT_ID", PAYMENT_ID);
//							defaultPayments
							if(defaultPayments.containsKey(paymap.get("payCode"))){
									try{
										dealAmount = dealAmount.add(new BigDecimal(StringUtil.safeToString(paymap.get("payAmount"))));
									}catch(Exception e){
										
									}
							}
							
						}
				        if(dealAmount.compareTo(BigDecimal.ZERO)>0){
				        	transMap.put("DEAL_AMOUNT", dealAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
				        }else{
				        	transMap.put("DEAL_AMOUNT", "0.00");
				        }
				        
				        if(CollectionUtil.isNotEmpty(transMap)){
				        	//保存订单主信息
				        	requestRemoteMapper.saveTransactionData(transMap);
				        	//增加禁止开票信息
				        	List<String> codeForbiddens = (List<String>) orderMap.get("cashCodeList");
				        	if(CollectionUtil.isNotEmpty(codeForbiddens)){
				        		List<Map<String,Object>> codeForbiddenArray = new ArrayList<Map<String,Object>>();
				        		Map<String,Object> mapTemp = new HashMap<String,Object>();
				        		for (String codeForbidden : codeForbiddens) {
				        			mapTemp.put("EXTRACTED_CODE", ShinHoDataUtil.deCode(codeForbidden));
				        			mapTemp.put("IS_DELETE","0");
				        			mapTemp.put("USER","SYSADMIN");
				        			mapTemp.put("NOWTIME",StringUtil.getNowTime());
				        			codeForbiddenArray.add(mapTemp);
								}
				        		requestRemoteMapper.saveCodeForbiddens(codeForbiddenArray);
				        	}
				        }
				        if(CollectionUtil.isNotEmpty(foodVo)){
				        	//保存交易明细
				        	requestRemoteMapper.saveSalesItem(foodVo);
				        }
				        if(CollectionUtil.isNotEmpty(payTypeVo)){
				        	//保存支付明细
				        	requestRemoteMapper.savePaymentItem(payTypeVo);
				        }
						execBaseBizService = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
					}else if(StringUtil.isNotEmpty(openId)){
						//判断是否需要更新openId
						Transaction orderTrans = (Transaction) execBaseBizService.get("transactionData");
						if(StringUtil.isEmpty(orderTrans.getOPEN_ID())){
							Map<String,Object> params = new HashMap<String,Object>();
							params.put("TRANSACTION_ID", orderTrans.getTRANSACTION_ID());
							params.put("OPEN_ID", openId);
							requestRemoteMapper.updateTransOpenId(params);
						}
					}
					returnList.add((Transaction) execBaseBizService.get("transactionData"));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("tradeinfos", returnList);
		return returnMap;
	}
	
	public Map<String, Object> getInvoiceInfosByOpenId(Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String,Object>> invoiceList = requestRemoteMapper.getInvoiceInfosByOpenId(paramMap);		
		returnMap.put("tradeinfos", invoiceList);
		return returnMap;
	}
	

	@Override
	public String execute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
