package com.rongzer.efapiao.service;

import com.rongzer.efapiao.dao.InvoiceCheckMapper;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("invoiceInfoService")
public class InvoiceInfoService {

	@Autowired
	private InvoiceCheckMapper invoiceCheckMapper;

	/**
	 * 查询已开发票数据
	 * 
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getInvoiceInfo(Map<String, Object> params) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try{
			//对提取码进行异或处理
			String transactionNumber = StringUtil.safeToString(params.get("TRANSACTION_NUMBER"));
			if(StringUtil.isNotEmpty(transactionNumber)){
				params.put("TRANSACTION_NUMBER", ShinHoDataUtil.deCode(transactionNumber));
			}
			//先查询对应的发发票信息
			result = invoiceCheckMapper.getInvoiceInfo(params);
			//查询订单关联的门店和提取码
			if(CollectionUtil.isNotEmpty(result)){
				for (Map<String, Object> mapTemp : result) {
					List<Map<String,Object>> transactionInfo = invoiceCheckMapper.getTransactionInfo(mapTemp);
					if(CollectionUtil.isNotEmpty(transactionInfo)){
						//如果交易不为空，则拼接提取码和门店号,和订单号
						StringBuffer extractedCode = new StringBuffer(); 
						StringBuffer storeNo = new StringBuffer();
						StringBuffer cashCode = new StringBuffer();
						for (Map<String, Object> transMap : transactionInfo) {
							transactionNumber = StringUtil.safeToString(transMap.get("TRANSACTION_NUMBER"));
							extractedCode.append(ShinHoDataUtil.deCode(transactionNumber)+",");
							storeNo.append(transMap.get("STORE_NUMBER")+",");
							cashCode.append(transactionNumber+",");
						}
						if(StringUtil.isNotEmpty(extractedCode)){
							mapTemp.put("EXTRACTED_CODE", extractedCode.toString().substring(0, extractedCode.toString().length()-1));
							mapTemp.put("STORE_NUMBER", storeNo.toString().substring(0, storeNo.toString().length()-1));
							mapTemp.put("TRANSACTION_NUMBER", cashCode.toString().substring(0, cashCode.toString().length()-1));
						}				
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		return result;
	}

	/**
	 * 查询已开发票的数量
	 * 
	 * @param params
	 * @return
	 */
	public int getInvoiceInfoCount(Map<String, Object> params) {
		int recordCount = 0;
		//对提取码进行异或处理
		String transactionNumber = StringUtil.safeToString(params.get("TRANSACTION_NUMBER"));
		if(StringUtil.isNotEmpty(transactionNumber)){
			params.put("TRANSACTION_NUMBER", ShinHoDataUtil.deCode(transactionNumber));
		}
		try{
			recordCount = invoiceCheckMapper.getInvoiceInfoCount(params);
		}catch(Exception e){
			e.printStackTrace();
		}
		return recordCount;
	}


	/**
	 * 查询异常发票信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getInvoiceExceptionInfo(Map<String, Object> params) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try{
			//对提取码进行异或处理
			String transactionNumber = StringUtil.safeToString(params.get("TRANSACTION_NUMBER"));
			if(StringUtil.isNotEmpty(transactionNumber)){
				params.put("TRANSACTION_NUMBER", ShinHoDataUtil.deCode(transactionNumber));
			}
			result = invoiceCheckMapper.getInvoiceExceptionInfo(params);
			if(CollectionUtil.isNotEmpty(result)){
				//如果交易不为空，则拼接提取码
				StringBuffer extractedCode = new StringBuffer();
				for (Map<String, Object> transMap : result) {
					transactionNumber = StringUtil.safeToString(transMap.get("TRANSACTION_NUMBER"));
					extractedCode.append(ShinHoDataUtil.deCode(transactionNumber)+",");
					if(StringUtil.isNotEmpty(extractedCode)){
						transMap.put("EXTRACTED_CODE", extractedCode.toString().substring(0, extractedCode.toString().length()-1));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public int getInvoiceExceptionInfoCount(Map<String, Object> params) {
		int recordCount = 0;
		//对提取码进行异或处理
		String transactionNumber = StringUtil.safeToString(params.get("TRANSACTION_NUMBER"));
		if(StringUtil.isNotEmpty(transactionNumber)){
			params.put("TRANSACTION_NUMBER", ShinHoDataUtil.deCode(transactionNumber));
		}
		try{
			recordCount = invoiceCheckMapper.getInvoiceExceptionInfoCount(params);
		}catch(Exception e){
			e.printStackTrace();
		}
		return recordCount;
	}
}
