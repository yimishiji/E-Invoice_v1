package com.rongzer.efapiao.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.model.TransactionItem;
import com.rongzer.efapiao.model.TransactionPayment;

/**
 * 
 * @author qrl
 *
 */
@Repository("requestRemoteMapper")
public interface RequestRemoteMapper {

	/**
	 * 保存订单主信息
	 * @param transMap
	 */
	void saveTransactionData(Map<String, Object> transMap);

	/**
	 * 保存订单明细
	 * @param salesItem
	 */
	void saveSalesItem(List<Map<String,Object>> salesItem);

	/**
	 * 保存支付信息
	 * @param paymentItem
	 */
	void savePaymentItem(List<Map<String,Object>> paymentItem);

	/**
	 * 查询订单主信息
	 * @param params
	 * @return
	 */
	Transaction getTransData(Map<String, Object> params);

	/**
	 * 查询订单明细
	 * @param params
	 * @return
	 */
	List<TransactionItem> getSalesItems(Map<String, Object> params);

	/**
	 * 查询支付信息
	 * @param params
	 * @return
	 */
	List<TransactionPayment> getPayments(Map<String, Object> params);

	/**
	 * 查询发票主信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getInvoiceInfo(Map<String, Object> params);

	/**
	 * 根据openId查询已开发票
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> getInvoiceInfosByOpenId(Map<String, Object> paramMap);

	/**
	 * 保存禁止开票提取码
	 * @param codeForbiddenArray
	 */
	void saveCodeForbiddens(List<Map<String, Object>> codeForbiddenArray);

	/**
	 * 更新订单的OPENID
	 * @param orderTrans
	 */
	void updateTransOpenId(Map<String, Object> params);

}
