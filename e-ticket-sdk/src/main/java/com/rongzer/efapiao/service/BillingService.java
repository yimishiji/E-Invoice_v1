package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.model.InvoiceOrder;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *	前端画面相关的service
 * 	@author zhuzhibin
 *
 */
@Service("billingService")
public class BillingService extends BaseBusinessService {
	
	private static final SimpleDateFormat sdfxs = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdfs = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	 * 递交提取码，后台确认交易信息
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> billing(Map<String, Object> paramMap){
		Map<String, Object> mapReturn = new HashMap<String, Object>();
		mapReturn.put("suc", false);
		//非空校验
		String pickupCode = (String) paramMap.get("PICKUPCODE");//提取码
		pickupCode = pickupCode.toUpperCase();
		String validate = (String) paramMap.get("IDENTIFYCODE");//验证码
		if(StringUtil.isEmpty(pickupCode)){
			mapReturn.put("msg", "pickupCodeEmpty");
			return mapReturn;
		}
		if(StringUtil.isEmpty(validate)){
			mapReturn.put("msg", "identifyCodeEmpty");
			return mapReturn;
		}
		try {
			//校验验证码
			Map<String, Object> validateMap = validateCode(paramMap);
			boolean checkFlag = (Boolean)validateMap.get("suc");
			//开发测试模式下,验证码校验永远为正确
			String needCheck = RDPUtil.getSysConfig("efapiao.dev.checkCode");
			if(StringUtil.isNotEmpty(needCheck) && "0".equals(needCheck)){
				checkFlag = true;
			}
			if(checkFlag){
				//判断提取码是否被禁用以及提取码是否禁止开票
                paramMap.put("PICKUPCODE",ShinHoDataUtil.deCode(pickupCode));
				paramMap.put("extractedCode",pickupCode);
				paramMap.put("method", "checkPickupCodeHasInvoice");
				mapReturn = RDPUtil.execBaseBizService("invoiceService", paramMap);
				if(CollectionUtil.isNotEmpty(mapReturn)){
					Boolean suc = (Boolean)mapReturn.get("suc");
					if(!suc) {
						return mapReturn;
					}
				}
				paramMap.put("method", "getTransDataFromBw");
				paramMap.put("pickupCode", pickupCode);
				RDPUtil.execBaseBizService("requestRemoteService", paramMap,true);
				mapReturn.put("suc", true);
			}else{
				mapReturn.put("msg", "errCode");// 验证码错误
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			mapReturn.put("msg", "systemError");// 系统错误
		}
		return mapReturn;
	}
	
	
	/**
	 * 
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> addKeyToCaChe(Map<String, Object> paramMap){
		Map<String, Object> mapReturn = new HashMap<String, Object>();
		mapReturn.put("suc", true);
		String CookieValue = (String) paramMap.get("COOKIE_VALUE");
		String orderId = (String) paramMap.get("ORDER_ID");//提取码
		this.saveCache(CookieValue+ "_MERGE_ORDER_ID", orderId);
		return mapReturn;
	}
	
	/**
	 * 递交提取码，后台确认交易信息
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> posDatatimer(Map<String, Object> paramMap){
		Map<String, Object> mapReturn = new HashMap<String, Object>();
		mapReturn.put("suc", false);
		String CookieValue = (String) paramMap.get("COOKIE_VALUE");
		String pickupCode = (String) paramMap.get("PICKUPCODE");//提取码
		String isAddMerge = StringUtil.safeToString(paramMap.get("isAddMerge"));
		if(StringUtil.isNotEmpty(pickupCode)){
			try{
				//解码
				pickupCode = ShinHoDataUtil.deCode(pickupCode.toUpperCase());
				Transaction transaction = (Transaction) getCache(pickupCode);
				if(transaction != null){
					//获取订单，判断是否已存在，判断开票状态
					String invoiceStatus = transaction.getINVOICE_STATUS();
					if(EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(invoiceStatus)
							||EfapiaoConstant.InvoiceStatus.ERROR_INVOICE.equals(invoiceStatus)
							||EfapiaoConstant.InvoiceStatus.IN_INVOICE.equals(invoiceStatus)
							||EfapiaoConstant.InvoiceStatus.DOWNLOADING_INVOICE.equals(invoiceStatus)){

						//验证是否已经超过开票日期
						int applyDate = StringUtil.toInt(RDPUtil.getSysConfig("efapiao.invoice.applydate"),0);
						String nowDate = StringUtil.getNowDate();
						Date transDate = sdf1.parse(transaction.getTRANSACTION_DATETIME());
						Calendar cal = Calendar.getInstance();
						cal.setTime(transDate);
						cal.add(Calendar.DATE,applyDate);
						String dealDate = sdfxs.format(cal.getTime());
						//未开具发票的订单才进行日期校验
						if(EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(invoiceStatus)&&dealDate.compareTo(nowDate)<0){
							mapReturn.put("suc", true);
							mapReturn.put("msg", "outOfDate");//超出时间
						}else{
							//获取金额
							Double buildAppData = new Double(0);
							buildAppData = StringUtil.toDouble((String) transaction.getTRANSACTION_AMOUNT(), buildAppData) ;
							if(buildAppData>0){
								//获取到交易信息，并可以开具发票
								updateSessionPickupCodeList(CookieValue,pickupCode,isAddMerge);
								mapReturn.put("suc", true);
								mapReturn.put("msg", "transactionSucess");
							}else{
								//获取到交易信息，不可以开具发票
								mapReturn.put("suc", true);
								mapReturn.put("msg", "noInvoiceToApply");
							}
						}
					}else if(EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE.equals(invoiceStatus)){
						//开票已成功，跳转至开票成功页
						mapReturn.put("suc", true);//已经开具过发票,跳转至第三个页面
						mapReturn.put("orderId", transaction.getORDER_ID());
						//将OrderId放入此次会话的缓存中
						this.saveCache(CookieValue + "_MERGE_ORDER_ID", transaction.getORDER_ID());
						mapReturn.put("msg", "invoiceSucess");
					}else if(EfapiaoConstant.InvoiceStatus.IN_INVOICE.equals(invoiceStatus)){
						//该发票在手工开票处理中
						mapReturn.put("suc", true);
						mapReturn.put("msg", "invoiceManualProcessing");
					}
				}else{
					//判断是否超出可开票日期
					int applyDate = StringUtil.toInt(RDPUtil.getSysConfig("efapiao.invoice.applydate"),0);
					String dateString = pickupCode.substring(4,10);
					String nowDate = StringUtil.getNowDate();
					Date transDate = sdfs.parse(nowDate.substring(0, 2) + dateString);
					Calendar cal = Calendar.getInstance();
					cal.setTime(transDate);
					cal.add(Calendar.DATE,applyDate);
					String dealDate = sdfxs.format(cal.getTime());
					//未开具发票的订单才进行日期校验
					if(dealDate.compareTo(nowDate)<0){
						mapReturn.put("suc", true);
						mapReturn.put("msg", "outOfDate");//超出时间
					}else{
						mapReturn.put("suc", false);
						mapReturn.put("msg", "noPosData");
					}
				}
			}catch(Exception e){
				mapReturn.put("suc", true);
				mapReturn.put("msg", "systemError");// 系统错误
			}
		}
		
		return mapReturn;
	}
	
	//为本次的会话添加提取码
	private void updateSessionPickupCodeList(String CookieValue,String pickupcode,String isAddMerge){
		//会话中所有的提取码的集合缓存key
		String sessionPickupcodeListKey = CookieValue +EfapiaoConstant.CacheKey.SESSION_PICKUPCODE_LIST;
		//获取到本次会话所有添加的交易提取码
		@SuppressWarnings("unchecked")
		LinkedList<String> sessionPickupcodeList = (LinkedList<String>) getCache(sessionPickupcodeListKey);
		if(CollectionUtil.isEmpty(sessionPickupcodeList)||StringUtil.isEmpty(isAddMerge)){
			sessionPickupcodeList = new LinkedList<String>();
		}
		if(StringUtil.isNotEmpty(pickupcode) && !sessionPickupcodeList.contains(pickupcode)){
			sessionPickupcodeList.add(pickupcode);
		}
		//更新到缓存
		saveCache(sessionPickupcodeListKey,sessionPickupcodeList);
	}
	
	
	
	/**
	 * 获取本次会话的交易列表
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTradeInfos(Map<String, Object> params) {
		// 请求返回对象
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String CookieValue = (String) params.get("COOKIE_VALUE");
		String openId = (String) params.get("openId");
		LinkedList<Transaction> tradeinfos = new LinkedList<Transaction>();
		//会话中所有的提取码的集合缓存key
		String sessionPickupcodeListKey = CookieValue +EfapiaoConstant.CacheKey.SESSION_PICKUPCODE_LIST;
		//获取到本次会话所有添加的交易提取码
		LinkedList<String> sessionPickupcodeList = (LinkedList<String>) getCache(sessionPickupcodeListKey);
		if(sessionPickupcodeList == null){
			sessionPickupcodeList = new LinkedList<String>();
		}
		Map<String, Object> baseData = new HashMap<String, Object>();
		baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
		Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
		for(String code : sessionPickupcodeList){
			Transaction tempTransaction = (Transaction) getCache(code);
			if(tempTransaction != null){
				ArrayList<Transaction>  tempList = new ArrayList<Transaction>();
				tempList.add(tempTransaction);
				String amount = ShinHoDataUtil.getOrderAmountByTransactions(tempList, defaultPayments);
				if("0.00".equals(amount)){
					tempTransaction.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.ZERO_INVOICE);
				}
				tradeinfos.add(tempTransaction);
			}
		}
		returnMap.put("tradeinfos", tradeinfos);
		returnMap.put("openId",openId);
		return returnMap;
	}
	

	/**
	 * 获取本次会话的交易列表
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTradeInfosByOpenId(Map<String, Object> params) {
		params.put("method", "getTransDataFromInterface");
		Map<String, Object> baseDataMap = RDPUtil.execBaseBizService("requestRemoteService", params);
		Map<String,List<Transaction>> transMap = new LinkedHashMap<String,List<Transaction>>();
		if(CollectionUtil.isNotEmpty(baseDataMap)){
			List<Transaction> transList = (List<Transaction>)baseDataMap.get("tradeinfos");
			if(CollectionUtil.isNotEmpty(transList)){
				for (int i = 0; i < transList.size(); i++) {
					Transaction transaction = transList.get(i);
					if(!"E00505".equals(transaction.getINVOICE_STATUS())){//已开票不返回
						if(transMap.containsKey(transaction.getTRANSACTION_MONTH())){


							transMap.get(transaction.getTRANSACTION_MONTH()).add(transaction);
						}else{
							List<Transaction> transListTemp = new ArrayList<Transaction>();
							transListTemp.add(transaction);
							transMap.put(transaction.getTRANSACTION_MONTH(),transListTemp);
						}
					}
				}
			}
		}
		//删除未下单的月份
		Iterator<Map.Entry<String, List<Transaction>>> it = transMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, List<Transaction>> entry = it.next();
			if(transMap.get(entry.getKey()).size() == 0){
				it.remove();
			}
		}

		baseDataMap.put("tradeinfos",transMap);
		return baseDataMap;
	}
	
	/**
	 * 获取本次会话的交易列表
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInvoiceInfosByOpenId(Map<String, Object> params) {
		params.put("method", "getInvoiceInfosByOpenId");
		Map<String, Object> baseDataMap = RDPUtil.execBaseBizService("requestRemoteService", params);
		if(CollectionUtil.isNotEmpty(baseDataMap)){
			List<Transaction> transList = (List<Transaction>)baseDataMap.get("tradeinfos");
		}
		return baseDataMap;
	}
	/**
	 * 将要合并开票的提取码保存到缓存中去
	 * @param params
	 * @return
	 */
	public Map<String,Object> mergeApply(Map<String, Object> params){
		String CookieValue = (String) params.get("COOKIE_VALUE");
		String cookieMergeKey = CookieValue + "_MERGECODELIST";//合并开票的提取码在缓存中的key值cookieValue+_MERGECODELIST
		String cookieTradeTypeKey = CookieValue + "_TRADETYPE";//交易类型的key值
		String cookieTradeIsCard = CookieValue + "_ISCARD";//交易内容是否为预付卡
		String extrCodesStr = StringUtil.toStringWithEmpty(params.get("EXTRCODES"));//获取前端提交的16进制提取码
		String tradeType = StringUtil.toStringWithEmpty(params.get("tradeType"));//交易类型(线上,线下)
		String isCard = StringUtil.toStringWithEmpty(params.get("isCard"));//交易类型(线上,线下)
		this.saveCache(cookieMergeKey, extrCodesStr);//存入缓存，在填写页面初始化的时候使用
		this.saveCache(cookieTradeTypeKey, tradeType);//存入缓存，在填写页面初始化的时候使用
		this.saveCache(cookieTradeIsCard, isCard);//存入缓存，在填写页面初始化的时候使用
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("suc", true);//返回标志位，跳转到抬头填写页面
		return resultMap;
	}
	
	/**
	 * 获取订单信息
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMergeInfo(Map<String, Object> params){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//请求中是否包含订单
		String orderId = (String) params.get("orderId");
		if(StringUtil.isEmpty(orderId)){
			//没有订单,则是根据选中的交易来生成订单(临时订单,不做入库,只用于前台展示)
			RandomGUID myGUID = new RandomGUID();
			orderId = StringUtil.getUuid32().substring(22) + myGUID.valueAfterMD5.substring(22);
			InvoiceOrder invoiceOrder = new InvoiceOrder();
			invoiceOrder.setORDER_ID(orderId);
			invoiceOrder.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.NO_INVOICE);//订单状态 (未开票)
			invoiceOrder.setINVOICE_TYPE(EfapiaoConstant.InvoiceType.DEFAULT);//订单类型
			invoiceOrder.setIS_MANUAL(EfapiaoConstant.DefaultKey.FALSE);//是否手工票
			invoiceOrder.setADD_TIME(StringUtil.getNowTime());
			invoiceOrder.setIS_DELETE("0");
			String CookieValue = (String) params.get("COOKIE_VALUE");//获取cookieValue
			String cookieMergeKey = CookieValue + "_MERGECODELIST";//合并开票的提取码在缓存中的key值cookieValue+_MERGECODELIST
			String cookieTradeTypeKey = CookieValue + "_TRADETYPE";//交易类型的key值
			String cookieTradeIsCard = CookieValue + "_ISCARD";//交易内容是否为预付卡
			String tradeType = StringUtil.toStringWithEmpty(this.getCache(cookieTradeTypeKey));//交易类型(线上,线下)
			String extrCodesStr = StringUtil.toStringWithEmpty(this.getCache(cookieMergeKey));//获取缓存中的提取码
			String isCardStr = StringUtil.toStringWithEmpty(this.getCache(cookieTradeIsCard));//获取缓存中的交易内容是否为预付卡
			List<Transaction> trades = new ArrayList<Transaction>();//返回的汇总后的交易list
			//计算总金额
			if(StringUtil.isNotEmpty(extrCodesStr)){
				JSONArray extrCodeArray = JSONUtil.getJSONArrayFromStr(extrCodesStr);//提取码转为list
				String extrCode = "";//提取码
				for (int i = 0; i < extrCodeArray.size(); i++) {
					extrCode = StringUtil.toStringWithEmpty(extrCodeArray.get(i));
					if(StringUtil.isNotEmpty(extrCode)){
						Transaction transaction = (Transaction) getCache(extrCode);//获取缓存中的交易数据
						if(transaction!=null){
							trades.add(transaction);
						}
					}
				}
			}
			Map<String, Object> baseData = new HashMap<String, Object>();
			baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
			// 获取支付方式(初始化数据)
			Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
			String buildAmountStr = ShinHoDataUtil.getOrderAmountByTransactions(trades,defaultPayments);
			invoiceOrder.setTOTAL_AMOUNT(buildAmountStr);
			invoiceOrder.setINVOICE_TRADE_TYPE(tradeType);
			invoiceOrder.setISCARD(isCardStr);
			invoiceOrder.setINVOICE_DETAIL_TYPE(EfapiaoConstant.InvoiceType.INVOICE_GROUP);
			invoiceOrder.setINVOICE_WRITE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_PERSONAL);
			resultMap.put("SUC", true);
			resultMap.put("orderMap", invoiceOrder);
		}else{
			//有订单,获取订单信息
			Map<String,Object> orderMap = (Map<String, Object>) getCache(orderId);
			if(CollectionUtil.isEmpty(orderMap)){
				//订单信息为空,返回异常,跳转首页
				resultMap.put("SUC", false);
			}else{
				resultMap.put("SUC", true);
				resultMap.put("orderMap", orderMap);
				resultMap.put("READONLY", true);
			}
		}
		return resultMap;
	}
	
	/**
     * 处理合并的订单
     * @param paramMap
     * @return
     */
    public Map<String, Object> dealOrder(Map<String, Object> paramMap){
    	String CookieValue = (String) paramMap.get("COOKIE_VALUE");//获取cookieValue
		String cookieMergeKey = CookieValue + "_MERGECODELIST";//合并开票的提取码在缓存中的key值cookieValue+_MERGECODELIST
		String extrCodesStr = StringUtil.toStringWithEmpty(this.getCache(cookieMergeKey));//获取缓存中的提取码
		List<Transaction> trades = new ArrayList<Transaction>();//返回的汇总后的交易list
		//计算总金额
		if(StringUtil.isNotEmpty(extrCodesStr)){
			JSONArray extrCodeArray = JSONUtil.getJSONArrayFromStr(extrCodesStr);//提取码转为list
			String extrCode = "";//提取码
			for (int i = 0; i < extrCodeArray.size(); i++) {
				extrCode = StringUtil.toStringWithEmpty(extrCodeArray.get(i));
				if(StringUtil.isNotEmpty(extrCode)){
					Transaction transaction = (Transaction) getCache(extrCode);//获取缓存中的交易数据
					if(transaction!=null){
						trades.add(transaction);
					}
				}
			}
		}
		Map<String, Object> baseData = new HashMap<String, Object>();
		baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
		// 获取支付方式(初始化数据)
		Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
		String buildAmountStr = ShinHoDataUtil.getOrderAmountByTransactions(trades,defaultPayments);
		if("0".equals(buildAmountStr)){
			paramMap.put("SUC", false);
		}else{
			//校验字段是否符合规范
			String invoiceTitle = StringUtil.safeToString(paramMap.get("INVOICETITLE"));
			try {
				if(StringUtil.isEmpty(invoiceTitle)||invoiceTitle.getBytes("GBK").length>100){
					paramMap.put("SUC", true);
					return paramMap;
				}
				String invoicePhone = StringUtil.safeToString(paramMap.get("INVOICEPHONE"));
				if(StringUtil.isEmpty(invoicePhone)||invoicePhone.getBytes("GBK").length>20){
					paramMap.put("SUC", true);
					return paramMap;
				}
				String invoiceMail = StringUtil.safeToString(paramMap.get("MAILACCOUNT"));
				if(StringUtil.isEmpty(invoiceMail)||invoiceMail.getBytes("GBK").length>50){
					paramMap.put("SUC", true);
					return paramMap;
				}
				String invoiceWriteType = StringUtil.safeToString(paramMap.get("INVOICE_WRITE_TYPE"));
				if("E00802".equals(invoiceWriteType)){
					String taxpayerNumber = StringUtil.safeToString(paramMap.get("TAXPAYERNUMBER"));
					if(StringUtil.isEmpty(taxpayerNumber)||taxpayerNumber.getBytes("GBK").length>20){
						paramMap.put("SUC", true);
						return paramMap;
					}
					String address = StringUtil.safeToString(paramMap.get("ADDRESS"));
					if(StringUtil.isNotEmpty(address)&&address.getBytes("GBK").length>80){
						paramMap.put("SUC", true);
						return paramMap;
					}
					String telePhoneNumber = StringUtil.safeToString(paramMap.get("TELEPHONENUMBER"));
					if(StringUtil.isNotEmpty(telePhoneNumber)&&telePhoneNumber.getBytes("GBK").length>20){
						paramMap.put("SUC", true);
						return paramMap;
					}
					String depositBank = StringUtil.safeToString(paramMap.get("TELEPHONENUMBER"));
					String bankAccount = StringUtil.safeToString(paramMap.get("BANKACCOUNT"));
					if(depositBank.getBytes("GBK").length + bankAccount.getBytes("GBK").length>100){
						paramMap.put("SUC", true);
						return paramMap;
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//调用异步billing方法
			RandomGUID myGUID = new RandomGUID();
			String orderId = StringUtil.getUuid32().substring(22) + myGUID.valueAfterMD5.substring(22);
			paramMap.put("method", "dealOrder");
			//将OrderId放入此次会话的缓存中
			this.saveCache(CookieValue + "_MERGE_ORDER_ID", orderId);
			//虚拟订单ID
			paramMap.put("ORDER_ID",orderId);
			RDPUtil.execBaseBizService("orderRemoteService", paramMap,true);
			//返回虚拟订单信息
			paramMap.put("SUC", true);
		}
		
        return paramMap;
    }
    
    
	/**
	 * 确认开票
	 * @param params
	 * @return
	 */
	public Map<String, Object> invoiceConfirm(Map<String, Object> params) {
		Map<String, Object> returnMap = new HashMap<String, Object>();// 请求返回对象
		params.put("method", "invoiceConfirm");
		RDPUtil.execBaseBizService("aisinoRemoteService", params,true);
		returnMap.put("suc", false);
		return returnMap;
	}

	/**
	 * 开票成功后获取发票明细
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInvoiceInfo(Map<String, Object> params) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> InvoiceObj = new HashMap<String,Object>();
		String CookieValue = (String) params.get("COOKIE_VALUE");//获取cookieValue
		String orderId = StringUtil.toStringWithEmpty(this.getCache(CookieValue+"_MERGE_ORDER_ID"));//获取缓存中的提取码
//		String orderId = "65402a45c2fa60661ae0";
		if(StringUtil.isNotEmpty(orderId)){
			Map<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("method", "selectInvoiceInfoByOrderId");
			paramMap.put("orderId", orderId);
			Map<String, Object> tempResult = RDPUtil.execBaseBizService("orderRemoteService", paramMap);
			List<Map<String, String>> invoiceInfoList = (List<Map<String, String>>) tempResult.get("invoiceInfoList");
			Set<String> orderSet = new HashSet<String>();
			Set<String> invoiceNumSet = new HashSet<String>();
			List<Map<String, String>> invoiceList = new ArrayList<Map<String, String>>();
			for(Map<String,String> orderMap : invoiceInfoList){
				orderSet.add(orderMap.get("TRANSACTION_NUMBER"));
				if(!invoiceNumSet.contains(orderMap.get("INVOICE_NUMBER"))){
					invoiceNumSet.add(orderMap.get("INVOICE_NUMBER"));
					invoiceList.add(orderMap);
				}
			}
			Map<String, String> invoiceMap = invoiceInfoList.get(0);
			InvoiceObj.put("BANK", invoiceMap.get("TAXPAYER_BANK"));
			InvoiceObj.put("ACCOUNT", invoiceMap.get("TAXPAYER_ACCOUNT"));
			InvoiceObj.put("TRANSACTION_NUMBER", orderSet);
			InvoiceObj.put("TRANSACTION_DATETIME", invoiceMap.get("TRANSACTION_DATETIME"));
			InvoiceObj.put("EMAIL", invoiceMap.get("PURCHASER_EMAIL"));
			InvoiceObj.put("XHFMC", invoiceMap.get("TAXPAYER_NAME_CN"));
			InvoiceObj.put("XHF_DZ", invoiceMap.get("TAXPAYER_ADDRESS"));
			InvoiceObj.put("XHF_DH", invoiceMap.get("TAXPAYER_PHONE"));
			InvoiceObj.put("GHFMC", invoiceMap.get("PURCHASER_NAME"));
			InvoiceObj.put("GHFDH", invoiceMap.get("PURCHASER_TEL"));
			InvoiceObj.put("TSFS", "0");
			InvoiceObj.put("NSRSBH", invoiceMap.get("TAXPAYER_IDENTIFY_NO"));
			InvoiceObj.put("INVOICE_ID", invoiceMap.get("INVOICE_ID"));
			InvoiceObj.put("INVOICE_TIME", invoiceMap.get("INVOICE_TIME"));
			InvoiceObj.put("BILLING_DATE", invoiceMap.get("BILLING_DATE"));
			//发票信息
			InvoiceObj.put("invoiceList",invoiceList);
		}

		
		returnMap.put("INVOICEOBJ", InvoiceObj);
		//从缓存中读取发票明细
		return returnMap;
	}
	
	
	
	/**
	 * 校验图形验证码
	 * @param params
	 * @return
	 */
	public Map<String, Object> validateCode(Map<String, Object> params) {
		// 请求返回对象
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String cookieValue = (String) params.get("COOKIE_VALUE");
		String cookieId = cookieValue + "_RANDONCODEKEY";
		String codeValue = (String) getCache(cookieId);
		String validate = (String) params.get("IDENTIFYCODE");
		if (StringUtil.isEmpty(validate)) {
			returnMap.put("suc", false);
			returnMap.put("msg", "验证码不能为空");
		} else {
			if (validate.equalsIgnoreCase(codeValue)) {
				returnMap.put("suc", true);
				delete(cookieId);
			} else {
				returnMap.put("suc", false);
				returnMap.put("msg", "验证码错误");
			}
		}
		return returnMap;
	}
	
	
	/**
	 * 根据orderId获取订单状态
	 * @param params
	 * @return
	 */
	public Map<String, Object> getOrderStatusById(Map<String, Object> params) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		String orderId = (String) params.get("orderId");
		if(StringUtil.isEmpty(orderId)){
			returnMap.put("SUC", false);
		}else{
			InvoiceOrder orderMap = (InvoiceOrder) getCache(orderId);
			if(orderMap != null && EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE.equals(orderMap.getINVOICE_STATUS())){
				returnMap.put("SUC", true);
				returnMap.put("orderId", orderId);
			}else{
				returnMap.put("SUC", false);
			}
		}
		//从缓存中读取发票明细
		return returnMap;
	}
	
	/**
	 * 根据邮箱和发票id重新发送邮件
	 * @param params
	 * @return
	 */
	public Map<String, Object> reSendEmail(Map<String, Object> params) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		String invoiceId = (String) params.get("invoiceId");
		String newEmail = (String) params.get("newEmail");
		returnMap.put("SUC", false);
		boolean flag = true;
		RDPUtil.execBaseBizService("orderRemoteService", params,true);
		if(flag && StringUtil.isEmpty(invoiceId) ){
			returnMap.put("MSG", "发票ID不能为空");
			flag = false;
		}
		if(flag && StringUtil.isEmpty(newEmail)){
			returnMap.put("MSG", "邮箱不能为空");
			flag = false;
		}
		if(flag){
			returnMap.put("SUC", true);
		}
		return returnMap;
	}
	
	public Map<String, Object> reSendEmail2(Map<String, Object> params) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		String orderId = (String) params.get("ORDER_ID");
		String purchaseMail = (String) params.get("PURCHASER_EMAIL");
		returnMap.put("SUC", true);
		if(StringUtil.isEmpty(purchaseMail)){
			returnMap.put("MSG", "邮箱不能为空");
			returnMap.put("SUC", false);
			return returnMap;
		}
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("method", "selectInvoiceInfoByOrderId");
		paramMap.put("orderId", orderId);
		Map<String, Object> tempResult = RDPUtil.execBaseBizService("orderRemoteService", paramMap);
		List<Map<String, String>> invoiceInfoList = (List<Map<String, String>>) tempResult.get("invoiceInfoList");
		if(CollectionUtil.isNotEmpty(invoiceInfoList)){
			for(Map<String, String> invoiceMap:invoiceInfoList){
				String invoiceId = invoiceMap.get("INVOICE_ID");
				Map<String, Object> paramsMap = new HashMap<String,Object>();
				paramsMap.put("invoiceId",invoiceId);
				paramsMap.put("newEmail",purchaseMail);
				paramsMap.put("method","reSendEmail");
				RDPUtil.execBaseBizService("orderRemoteService", paramsMap,true);
			}
		}
		return returnMap;
	}
}