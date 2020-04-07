package com.rongzer.efapiao.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;

import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.model.TransactionPayment;
import com.rongzer.efapiao.service.RandomGUID;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.HttpUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;

/**
 * 欣和相关数据转换工具
 * 
 * @author qrl
 * 
 */
public class ShinHoDataUtil {
	private static DecimalFormat df = new DecimalFormat("#0.00");
	private static final Logger log = LoggerFactory.getLogger(ShinHoDataUtil.class);

	/**
	 * 加/解密提取码
	 * 
	 * @param extractCode
	 *            /flowCode
	 * @return密文/明文
	 */
	public static String deCode(String flowCode) {
		String extractCode = "";
		try {
			// 特殊处理
			ArrayList<Character> specialCharArray = new ArrayList<Character>();
			specialCharArray.add('a');
			specialCharArray.add('A');
			specialCharArray.add('z');
			specialCharArray.add('Z');
			specialCharArray.add('-');
			// 获取字符数组
			char[] array = flowCode.toCharArray();
			// 遍历字符数组
			for (int i = 0; i < array.length; i++) {
				// 对每个数组元素进行异或运算
				if (!specialCharArray.contains(array[i])) {
					array[i] = (char) (array[i] ^ 1);
				}
			}
			// 输出密钥
			extractCode = new String(array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extractCode;
	}

	/**
	 * Object2String
	 * 
	 * @param obj
	 * @return
	 */
	public static String objectToStr(Object obj) {
		String returnStr = "";
		if (obj instanceof String) {
			if (StringUtil.isNotEmpty(obj)) {
				returnStr = (String) obj;
			}
		} else if (obj instanceof Double) {
			returnStr = df.format((Double) obj);
		} else if (obj instanceof BigDecimal) {
			returnStr = df.format(obj);
		} else if (obj instanceof Integer) {
			returnStr = obj.toString();
		}
		return returnStr;
	}

	/**
	 * 发票拆分
	 * 
	 * @param invoiceContent原始的开票内容
	 * @return 拆分后的list<FPKJXX_FPTXXContent>
	 */
	public static List<FPKJXX_FPTXXContent> splitInvoice(
			FPKJXX_FPTXXContent invoiceContent) {
		List<FPKJXX_FPTXXContent> invoiceList = new ArrayList<FPKJXX_FPTXXContent>();
		try {
			// 获取发票限额
			BigDecimal invoiceLimitAmount = new BigDecimal(StringUtil.safeToString(invoiceContent.getINVOICE_LIMIT_AMOUNT()));
			List<FPKJXX_XMXXContent> fpmxList = invoiceContent.getFpmxList();
			List<FPKJXX_XMXXContent> itemDetailListTmp = new ArrayList<FPKJXX_XMXXContent>();
			LinkedHashMap<String, List<FPKJXX_XMXXContent>> contentDetailMap = new LinkedHashMap<String, List<FPKJXX_XMXXContent>>();
			BigDecimal amountTotal = BigDecimal.ZERO;
			// 判断是否拆分发票
			for (int i = 0; i < fpmxList.size(); i++) {
				FPKJXX_XMXXContent itemDetail = fpmxList.get(i);
				// 发票行性质
				String invoiceLineNature = StringUtil.safeToString(itemDetail.getFPHXZ());
				FPKJXX_XMXXContent itemDetailDis = null;
				// 判断是否为被折扣项 如果是 则需要计算对应折扣项
				if ("2".equals(invoiceLineNature)) {
					itemDetailDis = fpmxList.get(i + 1);
					i = i + 1;
				}
				BigDecimal amountRemain = invoiceLimitAmount.add(amountTotal.multiply(new BigDecimal("-1")));
				Map<String, List<FPKJXX_XMXXContent>> dealTest = dealFapiaoDetail(invoiceLimitAmount,amountRemain,itemDetail,itemDetailDis);
				List<FPKJXX_XMXXContent> listAdd = dealTest.get("ADDLIST");
				List<FPKJXX_XMXXContent> listUpdate = dealTest.get("UPDATELIST");			
				if(CollectionUtil.isNotEmpty(listAdd)){
					itemDetailListTmp.addAll(listAdd);
					for (FPKJXX_XMXXContent detailTemp : listAdd) {
						amountTotal = amountTotal.add(new BigDecimal(StringUtil.safeToString(detailTemp.getXMJE())));						
					}
				}
				if(CollectionUtil.isNotEmpty(listUpdate)){
					//剩余金额数量不足1，end
					RandomGUID myGUID = new RandomGUID();
					String invoiceRequestSerialNumber = StringUtil.getUuid32().substring(22)+ myGUID.valueAfterMD5.substring(22);
					List<FPKJXX_XMXXContent> list = new ArrayList<FPKJXX_XMXXContent>();
					list.addAll(itemDetailListTmp);
					contentDetailMap.put(invoiceRequestSerialNumber, list);
					//重置前面的数据
					itemDetailListTmp =  new ArrayList<FPKJXX_XMXXContent>();
					amountTotal = BigDecimal.ZERO;
					for (int j = 0; j < listUpdate.size(); j++) {
						fpmxList.add(i+j+1, listUpdate.get(j));
					}
				}
			}

			if (CollectionUtil.isNotEmpty(itemDetailListTmp)) {
				RandomGUID myGUID = new RandomGUID();
				String invoiceRequestSerialNumber = StringUtil.getUuid32()
						.substring(22) + myGUID.valueAfterMD5.substring(22);
				contentDetailMap.put(invoiceRequestSerialNumber,
						itemDetailListTmp);
			}

			for (Map.Entry<String, List<FPKJXX_XMXXContent>> entry : contentDetailMap
					.entrySet()) {
				FPKJXX_FPTXXContent invoiceContentTmp = (FPKJXX_FPTXXContent) invoiceContent
						.clone();
				invoiceContentTmp.setFPQQLSH(entry.getKey());
				invoiceContentTmp.setFpmxList(entry.getValue());
				RandomGUID myGUID = new RandomGUID();
				String invoiceRequestSerialNumber = StringUtil.getUuid32().substring(22) + myGUID.valueAfterMD5.substring(22);
				invoiceContentTmp.setINVOICE_ID(invoiceRequestSerialNumber);
				BigDecimal totalAmount = new BigDecimal(0);
				// 重新计算合计开票金额KPHJJE
				for (FPKJXX_XMXXContent invoiceDetail : entry.getValue()) {
					totalAmount = totalAmount.add(new BigDecimal(invoiceDetail
							.getXMJE()));
				}
				invoiceContentTmp.setKPHJJE(ShinHoDataUtil
						.objectToStr(totalAmount));
				invoiceList.add(invoiceContentTmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invoiceList;
	}
	
	/**
	 * 根据交易信息,获取可开票金额
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getOrderAmountByTransactions(List<Transaction> trades,Map<String, Object> defaultPayments){
		//可开票金额
		String buildAmountStr = "0.00";
		//获取基础参数
		log.info("List<Transaction> trades "+(trades==null?"is null":"not null and size="+trades.size()));
		if(CollectionUtil.isNotEmpty(trades)){
			for(Transaction transaction : trades){
				log.info("Transaction transaction "+(transaction==null?"is null":"not null and TRANSACTION_NUMBER="+transaction.getTRANSACTION_NUMBER()));
				//获取订单的支付信息
				List<TransactionPayment> transactionPaymentList = transaction.getTransactionPaymentList();
				log.info("List<TransactionPayment> transactionPaymentList "+(transactionPaymentList==null?"is null":"not null and size="+transactionPaymentList.size()));
				if(CollectionUtil.isNotEmpty(transactionPaymentList)){
					for(TransactionPayment paymentItem : transactionPaymentList){
						String PAYMENT_CODE = paymentItem.getPAYMENT_CODE();
						Map<String, Object> paymentInfo = (Map<String, Object>) defaultPayments.get(PAYMENT_CODE);
						log.info("paymentInfo "+(paymentInfo==null?"is null":"not null and CAN_INVOICE="+paymentInfo.get("CAN_INVOICE")+" paymentItem.getPAYMENT_AMOUNT()="+paymentItem.getPAYMENT_AMOUNT()));
						if(CollectionUtil.isNotEmpty(paymentInfo)){
							String CAN_INVOICE = (String) paymentInfo.get("CAN_INVOICE");
							if(StringUtil.isNotEmpty(CAN_INVOICE) &&EfapiaoConstant.DefaultKey.TRUE.equals(CAN_INVOICE)){
								//计算总金额
								try {
									buildAmountStr = df.format(StringUtil.toBigDecimal(buildAmountStr).add(StringUtil.toBigDecimal(paymentItem.getPAYMENT_AMOUNT())).setScale(2));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		return buildAmountStr;
	}
	
	// 保留a-z|A-Z|0-9|所有中文|,|.|()|（）|-|#|《|》|·|等符合规范的字符
	//取消保留|空格\\u0020
	public static String getReplaceString(String str) {
		String regFilter = "[^(a-zA-Z0-9\\u4e00-\\u9fa5\\u002e\\u002c\\uff08\\uff09\\u3002\\uff1f\\uff0c\\u2019\\u2022\\u0040\\u0023\\u002d\\u300a\\u300b·)]";
		return str.replaceAll(regFilter, "");
	}
	
	/***
	 * 根据可添加金额，计算出可使用的明细和添加到下次计算的明细
	 * @param amountRemain
	 * @param detail
	 * @param detailDiscount
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	private static Map<String, List<FPKJXX_XMXXContent>> dealFapiaoDetail(BigDecimal limitAmount,BigDecimal amountRemain,FPKJXX_XMXXContent itemDetail,FPKJXX_XMXXContent itemDetailDis) throws CloneNotSupportedException{
		Map<String,List<FPKJXX_XMXXContent>>  result = new HashMap<String,List<FPKJXX_XMXXContent>>();
		List<FPKJXX_XMXXContent> itemDetailListAdd = new ArrayList<FPKJXX_XMXXContent>();
		List<FPKJXX_XMXXContent> itemDetailListUpdate = new ArrayList<FPKJXX_XMXXContent>();
		BigDecimal itemPrice = new BigDecimal(itemDetail.getXMDJ());// 获取开票内容单价
		BigDecimal itemAmount = new BigDecimal(itemDetail.getXMJE());// 获取开票内容总价
		BigDecimal itemDisAmount = BigDecimal.ZERO;// 折扣价
		BigDecimal itemAmountAfterDis = BigDecimal.ZERO;// 折后价
		BigDecimal itemNum = new BigDecimal(itemDetail.getXMSL());
		// 判断是否为被折扣项 如果是 则需要计算对应折扣项
		if (itemDetailDis != null) {
			itemDisAmount = new BigDecimal(StringUtil.safeToString(itemDetailDis.getXMJE()));
			itemAmountAfterDis = itemAmount.add(itemDisAmount);
		} else {
			itemAmountAfterDis = itemAmount;
		}
		//判断已开金额加上此次的实际金额是否有超出限额
		int r = itemAmountAfterDis.compareTo(amountRemain);
		if(r<=0){
			itemDetailListAdd.add(itemDetail);
			if(itemDetailDis!=null){
				itemDetailListAdd.add(itemDetailDis);
			}
		}else{
			BigDecimal addNum = (amountRemain.add(itemDisAmount.multiply(new BigDecimal("-1")))).divide(itemPrice,0,BigDecimal.ROUND_DOWN);
			//组织可添加数量的数量和金额
			BigDecimal curAmount = addNum.multiply(itemPrice);
			if(curAmount.add(itemDisAmount).compareTo(BigDecimal.ZERO)>0){
				//折扣行跟着一起
				itemDetail.setXMSL(StringUtil.safeToString(addNum));
				
				itemDetail.setXMJE(StringUtil.safeToString(addNum.multiply(itemPrice)));
				itemDetailListAdd.add(itemDetail);
				if(itemDetailDis!=null){
					itemDetailListAdd.add(itemDetailDis);
				}
				//重置前面的数据			
				FPKJXX_XMXXContent itemDetailNext = (FPKJXX_XMXXContent)itemDetail.clone();
				BigDecimal itemNumNext = itemNum.add(addNum.multiply(new BigDecimal("-1")));
				itemDetailNext.setXMSL(StringUtil.safeToString(itemNumNext));
				
				itemDetailNext.setXMJE(StringUtil.safeToString(itemNumNext.multiply(itemPrice)));
				itemDetailNext.setFPHXZ("0");
				itemDetailListUpdate.add(itemDetailNext);
			}else if(itemPrice.compareTo(limitAmount)<0){
				itemDetailListUpdate.add(0,itemDetail);
				if(itemDetailDis!=null){
					itemDetailListUpdate.add(1,itemDetailDis);
				}
			}else{
				//折扣行跟着一起
				itemDetail.setXMSL("1");
				itemDetail.setGGXH("1");
				itemDetail.setXMDJ(StringUtil.safeToString(limitAmount));
				itemDetail.setXMJE(StringUtil.safeToString(limitAmount));
				itemDetailListAdd.add(itemDetail);
				if(itemDetailDis!=null){
					itemDetailListAdd.add(itemDetailDis);
				}
				//重置前面的数据			
				FPKJXX_XMXXContent itemDetailNext = (FPKJXX_XMXXContent)itemDetail.clone();
				itemDetailNext.setXMDJ(StringUtil.safeToString(itemAmount.add(limitAmount.multiply(new BigDecimal("-1")))));
				itemDetailNext.setXMJE(StringUtil.safeToString(itemAmount.add(limitAmount.multiply(new BigDecimal("-1")))));
				itemDetailNext.setFPHXZ("0");
				itemDetailListUpdate.add(itemDetailNext);
			}
		}
		result.put("ADDLIST", itemDetailListAdd);
		result.put("UPDATELIST", itemDetailListUpdate);
		return result;
	}
	
	public static void main(String args[]) throws IOException{
		String[] str16 = {};
		for (String extrCode : str16) {
			System.out.println(deCode(extrCode));
		}	
	}
}
