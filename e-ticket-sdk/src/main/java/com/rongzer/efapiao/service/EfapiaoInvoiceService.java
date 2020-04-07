package com.rongzer.efapiao.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.InvoiceMapper;
import com.rongzer.efapiao.dao.RequestRemoteMapper;
import com.rongzer.efapiao.model.InvoiceOrder;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.model.TransactionRelation;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;

@Service("efapiaoInvoiceService")
public class EfapiaoInvoiceService extends BaseBusinessService{
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	RequestRemoteMapper requestRemoteMapper;
	@Autowired
    InvoiceMapper invoiceMapper;
	
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

	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushAPPOrderDetail(Map<String,Object> params) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("return_code", "0000");
		resultMap.put("return_msg", "开票申请接受成功");
		
		//基础交易信息
		JSONObject transactionStr = (JSONObject) params.get("transaction");
		if(StringUtil.isEmpty(transactionStr)){
			resultMap.put("return_code", "0001");
			resultMap.put("return_msg", "transaction不能为空");
			return resultMap;
		}
		JSONObject transaction = null;
		try{
			transaction =transactionStr;
		}catch(Exception e){
			resultMap.put("return_code", "0002");
			resultMap.put("return_msg", "transaction格式不正确");
			return resultMap;
		}
		if(CollectionUtil.isEmpty(transaction)){
			resultMap.put("return_code", "0002");
			resultMap.put("return_msg", "transaction格式不正确");
			return resultMap;
		}		

		//交易流水号
		String transaction_num = (String) transaction.get("transaction_num");
		if(StringUtil.isEmpty(transaction_num) ){
			resultMap.put("return_code", "0003");
			resultMap.put("return_msg", "transaction_num不能为空");
			return resultMap;
		}
		//交易时间
		String transaction_time = (String) transaction.get("transaction_time");
		if(StringUtil.isEmpty(transaction_time) ){
			resultMap.put("return_code", "0004");
			resultMap.put("return_msg", "transaction_time不能为空");
			return resultMap;
		}else{
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-HH-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyHHddHHmmss");
			try {
				transaction_time = sdf2.format(sdf1.parse(transaction_time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//门店号
		String store_no = (String) transaction.get("store_no");
		if(StringUtil.isEmpty(store_no) ){
			resultMap.put("return_code", "0005");
			resultMap.put("return_msg", "store_no不能为空");
			return resultMap;
		}
		store_no = store_no.toUpperCase();
		//交易总金额
		String total_amount = (String) transaction.get("total_amount");
		if(StringUtil.isEmpty(total_amount) ){
			resultMap.put("return_code", "0006");
			resultMap.put("return_msg", "total_amount不能为空");
			return resultMap;
		}
		//发票信息
		JSONObject fapiao_info = null;
		try {
			fapiao_info = (JSONObject) transaction.get("fapiao_info");
		} catch (Exception e) {
			resultMap.put("return_code", "0007");
			resultMap.put("return_msg", "fapiao_info参数错误");
			return resultMap;
		}
		if(CollectionUtil.isEmpty(fapiao_info) ){
			resultMap.put("return_code", "0007");
			resultMap.put("return_msg", "fapiao_info参数错误");
			return resultMap;
		}
		//交易明细列表
		JSONArray details = null;
		try {
			details = (JSONArray) transaction.get("details");
		} catch (Exception e) {
			resultMap.put("return_code", "0008");
			resultMap.put("return_msg", "details参数错误");
			return resultMap;
		}
		if(CollectionUtil.isEmpty(details) ){
			resultMap.put("return_code", "0008");
			resultMap.put("return_msg", "details参数错误");
			return resultMap;
		}
		//支付信息列表
		JSONArray payments = null;
		try {
			payments = (JSONArray) transaction.get("payments");
		} catch (Exception e) {
			resultMap.put("return_code", "0009");
			resultMap.put("return_msg", "payments参数错误");
			return resultMap;
		}
		if(CollectionUtil.isEmpty(payments) ){
			resultMap.put("return_code", "0009");
			resultMap.put("return_msg", "payments参数错误");
			return resultMap;
		}

		//发票抬头
		String purchaser_name = (String) fapiao_info.get("purchaser_name");
		if(StringUtil.isEmpty(purchaser_name)){
			resultMap.put("return_code", "0010");
			resultMap.put("return_msg", "purchaser_name不能为空");
			return resultMap;
		}
		//开票内容类型 0：明细；1：分类汇总；3：预付卡
		String detail_type = (String) fapiao_info.get("detail_type");
		if(StringUtil.isEmpty(detail_type)){
			resultMap.put("return_code", "0011");
			resultMap.put("return_msg", "detail_type不能为空");
			return resultMap;
		}
		if(!"0".equals(detail_type) && !"1".equals(detail_type) && !"2".equals(detail_type)){
			resultMap.put("return_code", "0012");
			resultMap.put("return_msg", "detail_type值不合法");
			return resultMap;
		}
		//消费者邮箱 ,非必填
		String email = (String) fapiao_info.get("email");
		if(StringUtil.isNotEmpty(email)){
			//邮箱格式校验
			if(!checkEmail(email)){
				resultMap.put("return_code", "0013");
				resultMap.put("return_msg", "email格式不正确");
				return resultMap;
			}
		}
		//纳税人识别号,发票抬头为"个人"时，非必填，为企业时，必填
		String taxpayer_no = (String) fapiao_info.get("taxpayer_no");
		if(!"个人".equals(purchaser_name)){
			if(StringUtil.isEmpty(taxpayer_no)){
				resultMap.put("return_code", "0014");
				resultMap.put("return_msg", "taxpayer_no不能为空");
				return resultMap;
			}
		}
		//电话,非必填
		String mobile = StringUtil.safeToString(fapiao_info.get("mobile"));
		//纳税人地址,非必填
		String address = StringUtil.safeToString(fapiao_info.get("address"));
		//电话,非必填
		String tel = StringUtil.safeToString(fapiao_info.get("tel"));
		//开户行,非必填
		String bank = StringUtil.safeToString(fapiao_info.get("bank"));
		//开户行账号,非必填
		String account = StringUtil.safeToString(fapiao_info.get("account"));
		
		//商品字段校验标志
		boolean checkFlag = true;
		//用于入库的参数,预先定义
		String transactionId = StringUtil.getUuid32();
		String orderId = StringUtil.getUuid32();
		List<Map<String,Object>> salesItemList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> paymentItemList = new ArrayList<Map<String,Object>>();
		List<TransactionRelation> relations = new ArrayList<TransactionRelation>();
		
		for(Object detail : details){
			JSONObject map = JSONObject.fromObject(detail);
			//商品编码
			String item_code = (String) map.get("item_code");
			//商品销售数量
			String item_quantity = (String) map.get("item_quantity");
			//商品原价（汇总金额）
			String item_amount = (String) map.get("item_amount");
			//商品优惠价（汇总金额）
			String item_disamount = (String) map.get("item_disamount");
			//重量
			String item_orderWeight = (String) map.get("item_orderWeight");
			if(
					StringUtil.isEmpty(item_code) ||
					StringUtil.isEmpty(item_quantity) ||
					StringUtil.isEmpty(item_amount) ||
					StringUtil.isEmpty(item_disamount)
					){
				checkFlag = false;
				break;
			}else{/**
					 '${item.TRANSACTION_ID}',
					 '${item.ITEM_ID}',
					 '${item.priseCode}',
					 '${item.foodNum}',
					 '${item.foodPrice}',
					 '${item.foodDiscountsPrice}',
					 '${item.discountCode}',
					 '${item.foodCode}',
					 '${item.orderWeight}',
					 '${item.discountMoney}',
					 '${item.unitCn}',
					 '${item.unitEn}'

					 */
				Map<String,Object> salesItemMap = new HashMap<String,Object>();
    			salesItemMap.put("TRANSACTION_ID", transactionId);
    			salesItemMap.put("ITEM_ID", StringUtil.getUuid32());
    			salesItemMap.put("priseCode", item_code);
    			salesItemMap.put("foodNum", item_quantity);
    			salesItemMap.put("foodPrice", item_amount);
    			salesItemMap.put("foodDiscountsPrice", item_disamount);
				salesItemMap.put("orderWeight", item_orderWeight);

//				salesItemMap.put("ITEM_CODE", item_code);
//				salesItemMap.put("ITEM_QUANTITY", item_quantity);
//				salesItemMap.put("ITEM_AMOUNT", item_amount);
//				salesItemMap.put("ITEM_AMOUNT_AFTER_DISCOUNT", item_disamount);
    			salesItemList.add(salesItemMap);
			}
		}


		TransactionRelation relation = new TransactionRelation();
		relation.setORDER_ID(orderId);
		relation.setTRANSACTION_NUMBER(transaction_num);
		relations.add(relation);

		if(checkFlag){
			
			//现在用于支付信息字段校验标志
			for(Object payment: payments){
				JSONObject map = JSONObject.fromObject(payment);
				String payment_code = (String) map.get("payment_code");
				String payment_quantity = (String) map.get("payment_quantity");
				String payment_amount = (String) map.get("payment_amount");
				if(
						StringUtil.isEmpty(payment_code) ||
						StringUtil.isEmpty(payment_quantity) ||
						StringUtil.isEmpty(payment_amount) 
						){
					checkFlag = false;
					break;
				}else{
					/**
					 * '${item.TRANSACTION_ID}',
					 '${item.PAYMENT_ID}',
					 '${item.payCode}',
					 '${item.payNum}',
					 '${item.payAmount}'
					 */
					Map<String,Object> paymentItemMap = new HashMap<String,Object>();
	    			paymentItemMap.put("TRANSACTION_ID", transactionId);
	    			paymentItemMap.put("PAYMENT_ID", StringUtil.getUuid32());
	    			//paymentItemMap.put("PAYMENT_QUANTITY", payment_quantity);//废弃
	    			//paymentItemMap.put("PAYMENT_CODE", payment_code);//废弃
	    			//paymentItemMap.put("PAYMENT_AMOUNT", payment_amount);//废弃

					paymentItemMap.put("payCode", payment_code);
					paymentItemMap.put("payNum", payment_quantity);
					paymentItemMap.put("payAmount", payment_amount);
					//paymentItemMap.put("orderWeight", orderWeight);



	    			paymentItemList.add(paymentItemMap);
				}
				
			}
			
			//所有校验全部通过,进行入库操作
			if(checkFlag){
				//入库
				Map<String, Object> orderInfo = new HashMap<String, Object>();
				
				orderInfo.put("TRANSACTION_ID", transactionId);
				orderInfo.put("serialNo", transaction_num);
				orderInfo.put("buyTime", transaction_time);
				orderInfo.put("storeId", store_no);
				//storeName,storeNameEn,posCode,amount,NOWTIME,NOWTIME
		    	orderInfo.put("POS_NAME", "");
		    	orderInfo.put("amount", total_amount);
		    	orderInfo.put("INVOICE_STATUS", EfapiaoConstant.InvoiceStatus.NO_INVOICE);
		    	orderInfo.put("NOW_TIME", StringUtil.getNowTime());
		    	orderInfo.put("USER", "SYSADD");


//				orderInfo.put("TRANSACTION_NUMBER", transaction_num);
//				orderInfo.put("TRANSACTION_DATETIME", transaction_time);
//				orderInfo.put("STORE_NUMBER", store_no);
//				orderInfo.put("POS_NAME", "");
//				orderInfo.put("TRANSACTION_AMOUNT", total_amount);
//				orderInfo.put("INVOICE_STATUS", EfapiaoConstant.InvoiceStatus.NO_INVOICE);
//				orderInfo.put("NOW_TIME", StringUtil.getNowTime());
//				orderInfo.put("USER", "SYSADD");

		    	
		    	orderInfo.put("TRANSACTION", salesItemList);
	    		orderInfo.put("PAYMENT", paymentItemList);
				
				if(CollectionUtil.isNotEmpty(orderInfo)){
					try {
						//保存订单主信息
						requestRemoteMapper.saveTransactionData(orderInfo);
						//保存交易明细
						requestRemoteMapper.saveSalesItem((List<Map<String, Object>>) orderInfo.get("TRANSACTION"));
						//保存支付明细
						requestRemoteMapper.savePaymentItem((List<Map<String, Object>>) orderInfo.get("PAYMENT"));
					} catch (Exception e) {
						e.printStackTrace();
						resultMap.put("return_code", "0009");
						resultMap.put("return_msg", "transaction_num已提交");
						return resultMap;
					}

				}
				
				//生成订单信息
				InvoiceOrder orderMap = new InvoiceOrder();
				orderMap.setORDER_ID(orderId);
				orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.NO_INVOICE);//订单状态
				orderMap.setINVOICE_TYPE(EfapiaoConstant.InvoiceType.DEFAULT);//订单类型
		        orderMap.setIS_MANUAL(EfapiaoConstant.DefaultKey.FALSE);//是否手工票
		        orderMap.setADD_TIME(StringUtil.getNowTime());
		        orderMap.setUPDATE_TIME(StringUtil.getNowTime());
		        orderMap.setIS_DELETE("0");
		        orderMap.setINVOICE_TRADE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_ONLINE);
		        String invoiceDetailType = "";
		        if("0".equals(detail_type)){
		        	invoiceDetailType = EfapiaoConstant.InvoiceType.INVOICE_DETAIL;
		        }else if("1".equals(detail_type)){
		        	invoiceDetailType = EfapiaoConstant.InvoiceType.INVOICE_GROUP;
		        }else if("3".equals(detail_type)){
		        	invoiceDetailType = EfapiaoConstant.InvoiceType.INVOICE_CARD;
		        }
		        orderMap.setINVOICE_DETAIL_TYPE(invoiceDetailType);
		        String invoiceWriteType = "";
		        if("个人".equals(purchaser_name)){
		        	invoiceWriteType = EfapiaoConstant.InvoiceType.INVOICE_PERSONAL;
		        }else{
		        	invoiceWriteType = EfapiaoConstant.InvoiceType.INVOICE_ENTERPRISE;
		        }
		        orderMap.setINVOICE_WRITE_TYPE(invoiceWriteType);
		        orderMap.setRelationShip(relations);
				
		        //开具发票
		        Map<String,Object> invoiceMap = new HashMap<String,Object>();
		        invoiceMap.put("method", "dealInvoice");
		        invoiceMap.put("ORDER_ID", orderId);
				invoiceMap.put("STORE_NO", store_no);
				invoiceMap.put("TRANSACTION_NUM", transaction_num);
		        invoiceMap.put("INVOICE_TRADE_TYPE",EfapiaoConstant.InvoiceType.INVOICE_ONLINE);
		        invoiceMap.put("INVOICE_WRITE_TYPE",invoiceWriteType);
		        invoiceMap.put("INVOICE_DETAIL_TYPE",invoiceDetailType);
		        if("E00801".equals(invoiceWriteType)){
		        	invoiceMap.put("PURCHASER_NAME", "个人");
		        	orderMap.setINVOICE_WRITE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_PERSONAL);
		        }else if("E00802".equals(invoiceWriteType)){
		        	invoiceMap.put("PURCHASER_NAME", purchaser_name);
		        	invoiceMap.put("PURCHASER_ID", taxpayer_no);
		        	invoiceMap.put("PURCHASER_ADDRESS", address);
		        	invoiceMap.put("PURCHASER_MOBILE",mobile);
		        	invoiceMap.put("PURCHASER_BANK_ACCOUNT", bank+account);
		        	orderMap.setINVOICE_WRITE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_ENTERPRISE);
		        }
		        
		        //保存订单
		        invoiceMapper.saveOrder(orderMap);
		        saveCache(orderId, orderMap);
		        //保存transaction与order之间关联关系
		        for(TransactionRelation relation2 : relations ){
					relation.setID(StringUtil.getUuid32());
					relation2.setADD_TIME(StringUtil.getNowTime());
					relation2.setUPDATE_TIME(StringUtil.getNowTime());
					String transactionNumber = StringUtil.safeToString(relation2.getTRANSACTION_NUMBER());
					String key = transactionNumber + EfapiaoConstant.CacheKey.RELATION_KEY;
					//保存入库
					invoiceMapper.saveRelation(relation2);
					//保存入缓存
					saveCache(key, relation2);
				}

				//重新查询交易信息
				Map<String, Object> invoiceServiceParamMap = new HashMap<String, Object>();
				//内部获取交易信息
				invoiceServiceParamMap.put("PICKUPCODE", transaction_num);
				invoiceServiceParamMap.put("WITHOUT_CACHE", "true");
				invoiceServiceParamMap.put("method", "getTransaction");
				Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
				Transaction transactionData = (Transaction) tempResult.get("transactionData");
		        //开票
				try {
					OrderRemoteService orderRemoteService = (OrderRemoteService)RDPContext.getContext().getBean("orderRemoteService");
					orderRemoteService.dealInvoice(invoiceMap);
				}catch (Exception e){
					e.printStackTrace();
					resultMap.put("return_code", "0017");
					resultMap.put("return_msg", "开票异常");
					return resultMap;
				}
				
			}else{
				resultMap.put("return_code", "0016");
				resultMap.put("return_msg", "支付参数缺失");
				return resultMap;
			}
		}else{
			resultMap.put("return_code", "0015");
			resultMap.put("return_msg", "商品参数缺失");
			return resultMap;
		}
		try {
			byte[] encodeMsgbyte = Base64.encodeBase64(StringUtil.safeToString(resultMap.get("return_msg")).getBytes("utf-8"));
			String encodeMsg = new String(encodeMsgbyte, "UTF-8");
			resultMap.put("return_msg", encodeMsg);
		}catch (Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}

	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> invoiceRed(Map<String,Object> params){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("return_code", "0000");
		resultMap.put("return_msg", "红冲申请接受成功");
		
		
		//交易流水号
		String transaction_num = (String) params.get("transaction_num");
		if(StringUtil.isEmpty(transaction_num)){
			resultMap.put("return_code", "0001");
			resultMap.put("return_msg", "transaction_num不能为空");
			return resultMap;
		}
		//IF10
		String store_no = (String) params.get("store_no");
		if(StringUtil.isEmpty(store_no)){
			resultMap.put("return_code", "0002");
			resultMap.put("return_msg", "store_no不能为空");
			return resultMap;
		}
		//查询交易信息
		Map<String, Object> invoiceServiceParamMap = new HashMap<String, Object>();
		//内部获取交易信息
		invoiceServiceParamMap.put("PICKUPCODE", transaction_num);
		invoiceServiceParamMap.put("WITHOUT_CACHE", "true");
		invoiceServiceParamMap.put("method", "getTransaction");
		Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
		Transaction transactionData = (Transaction) tempResult.get("transactionData");
		if(transactionData==null){
			resultMap.put("return_code", "0003");
			resultMap.put("return_msg", "该交易还未入库");
			return resultMap;
		}else{
			String orderId = transactionData.getORDER_ID();
			if(StringUtil.isEmpty(orderId)){
				resultMap.put("return_code", "0004");
				resultMap.put("return_msg", "该交易还未开具发票");
				return resultMap;
			}
			String orderStatus = transactionData.getINVOICE_STATUS();
			if(orderStatus.equals(EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE)){
				//进行红冲申请
				Map<String, Object> manualInvoiceInfoServiceParamMap = new HashMap<String, Object>();
				manualInvoiceInfoServiceParamMap.put("method", "redBufferApply");
				manualInvoiceInfoServiceParamMap.put("ORDER_ID", orderId);
				manualInvoiceInfoServiceParamMap.put("RED_STATUS", "DV0101");
				RDPUtil.execBaseBizService("manualInvoiceInfoService", manualInvoiceInfoServiceParamMap);
				resultMap.put("return_code", "0000");
				resultMap.put("return_msg", "申请红冲成功");
			}else{
				resultMap.put("return_code", "0005");
				resultMap.put("return_msg", "该交易的发票状态不是开票成功");
				return resultMap;
			}
		}
		try {
			byte[] encodeMsgbyte = Base64.encodeBase64(StringUtil.safeToString(resultMap.get("return_msg")).getBytes("utf-8"));
			String encodeMsg = new String(encodeMsgbyte, "UTF-8");
			resultMap.put("return_msg", encodeMsg);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	
	/**
	 * 开票,红冲回调
	 * 
	 * @param params
	 * @return
	 */
	public void callBack(Map<String, Object> params) {
		logger.debug("------------start service:" + this.getClass().getName() + ".callBack.");
		// 主机位置
		String serverUrl = RDPUtil.getSysConfig("efapiao.invoice.callbackurl");
		Map<String, Object> callBackParam = (Map<String, Object>) params.get("callBackParam");
		String json = null;
       //回调欧店云任务标志
		String isTask = StringUtil.safeToString(callBackParam.get("istask"));
		//发票反补任务标志
		String isDeny = StringUtil.safeToString(callBackParam.get("isdeny"));
		//发票下载成功标志
		String returnCode = null;
		if("1".equals(isTask)){
			json = StringUtil.safeToString(callBackParam.get("requestJson"));
			JSONObject jsonObject = JSONObject.fromObject(json);
			returnCode = jsonObject.getString("return_code");
		}else {
			JSONObject jsonObject = JSONObject.fromObject(callBackParam);
			json = jsonObject.toString();
			returnCode = StringUtil.safeToString(callBackParam.get("return_code"));
		}
		logger.debug("-------------request param:\n" + json);
		//如果是回调任务，但是没有开票成功的不回调给欧店云
		if("1".equals(isTask) && !"0000".equals(returnCode)){
			return;
		}
		Map<String,Object> returnMap = postByHttpClient(serverUrl,json);
		String responseCode = StringUtil.safeToString(returnMap.get("responseCode"));
		String responseStr = StringUtil.safeToString(returnMap.get("responseStr"));

		//插入日志
		Map<String,String> recordMap = new HashMap<String,String>();
		recordMap.put("transaction_num",(String)callBackParam.get("transaction_num"));
		recordMap.put("request_time",StringUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
		recordMap.put("request_param",json);
		recordMap.put("request_status",responseCode);
		String code = responseCode;
		if(StringUtil.isNotEmpty(responseStr)){
			JSONObject jsonObject = JSONObject.fromObject(responseStr);
			if(jsonObject.containsKey("code")) {
				code = jsonObject.getString("code");
			}else if(jsonObject.containsKey("status")){
				code = jsonObject.getString("status");
			}
			recordMap.put("request_status",code);
		}
		recordMap.put("response",responseStr);
		invoiceMapper.insertRecord(recordMap);

		if(!"0".equals(code)){
			/*
			* 如果是发票反补，说明肯定开票成功了，更新request_param字段
			* 如果不是回调任务，插入新的失败记录，后期通过任务定时调用
			* */
			if("1".equals(isDeny)){
				invoiceMapper.updateTask(recordMap);
			}else if(!"1".equals(isTask)){
				recordMap.put("success","0");
				invoiceMapper.insertTask(recordMap);
			}
		}else{
			if("1".equals(isTask)){
				recordMap.put("success","1");
				invoiceMapper.updateTask(recordMap);
			}
		}
		logger.debug("调用服务:" + this.getClass().getName() + ",执行callBack方法结束.");
	}

	public  Map<String,Object> postByHttpClient(String url, String strBody){
		Map<String,Object> returnMap = new HashMap<String, Object>();
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(url);
		post.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
		try {
			byte[] b = strBody.getBytes("utf-8");
			InputStream is = new ByteArrayInputStream(b, 0, b.length);
			RequestEntity re = new InputStreamRequestEntity(is, b.length);
			post.setRequestEntity(re);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response = "";
		try {
			int responseCode = httpclient.executeMethod(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response += line;
			}
			returnMap.put("responseCode",responseCode);
			returnMap.put("responseStr",response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	
	/**
     * 验证邮箱
     * @param email
     * @return
     */
    public boolean checkEmail(String email){
        boolean flag = false;
        try{
                String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                Pattern regex = Pattern.compile(check);
                Matcher matcher = regex.matcher(email);
                flag = matcher.matches();
            }catch(Exception e){
                flag = false;
            }
        return flag;
    }
    
	
	public static void main(String args[]){
		
		JSONObject transaction = null;
		Map<String,String> recordMap = new HashMap<String,String>();
		recordMap.put("a",null);
		recordMap.put("b","222");
		try{
			JSONObject jsonObject = JSONObject.fromObject("{a:22}");
			System.out.println(jsonObject.toString());
			//transaction = JSONObject.fromObject(recordMap);
		}catch(Exception e){
			
		}
		/*System.out.println(transaction);
		System.out.println(transaction.get("fapiao_info"));*/
	}
}