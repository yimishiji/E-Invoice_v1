package com.rongzer.efapiao.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;

@Repository("taskServiceMapper")
public interface TaskServiceMapper {

	/**
	 * 查询门店信息
	 * 
	 * @return
	 */
	List<Map<String, String>> getStoreInfo();

	/**
	 * 新增门店
	 * 
	 * @param storeAddList
	 */
	void insertStoreInfo(List<Map<String, Object>> storeAddList);

	/**
	 * 更新门店
	 * 
	 * @param storeAddList
	 */
	void updateStoreInfo(List<Map<String, Object>> storeAddList);
	
	/**
	 * 查询优惠
	 * 
	 * @return
	 */
	List<Map<String, String>> getDiscountInfo();

	/**
	 * 新增优惠
	 * 
	 * @param discountAddList
	 * @return
	 */
	int insertDiscountInfo(List<Map<String, Object>> discountAddList);

	/**
	 * 更新优惠
	 * 
	 * @param discountUpdateList
	 */
	int updateDiscountInfo(List<Map<String, Object>> discountUpdateList);

	

	/**
	 * 查询分类
	 * 
	 * @return
	 */
	List<Map<String, String>> getGroupInfo();

	/**
	 * 新增分类
	 * 
	 * @param groupAddList
	 * @return
	 */
	int insertGroupInfo(List<Map<String, Object>> groupAddList);

	/**
	 * 更新父级编码
	 * 
	 * @param groupUpdateList
	 */
	int updateGroupInfo(List<Map<String, Object>> groupUpdateList);

	/**
	 * 查询商品信息
	 * 
	 * @return
	 */
	List<Map<String, String>> getGoodsInfo();

	/**
	 * 新增商品信息
	 * 
	 * @param goodsAddList
	 */
	void insertGoodsInfo(List<Map<String, Object>> goodsAddList);

	/**
	 * 更新商品信息
	 * 
	 * @param goodsUpdateList
	 */
	void updateGoodsInfo(List<Map<String, Object>> goodsUpdateList);

	/**
	 * 查询支付信息
	 * @return paymentList
	 */
	List<Map<String, String>> getPaymentInfo();

	/**
	 * 新增支付方式
	 * @param paymentAddList
	 */
	void insertPaymentsInfo(List<Map<String, Object>> paymentAddList);

	/**
	 * 更新支付方式
	 * @param paymentUpdateList
	 */
	void updatePaymentsInfo(List<Map<String, Object>> paymentUpdateList);

	/**
	 * 查询组织信息
	 * @return
	 */
	List<Map<String, String>> getDepartInfo();

	/**
	 * 查询用户信息
	 * @return
	 */
	List<Map<String, String>> getUserInfo();
	/**
	 * 新增用户信息
	 * @param userAddList
	 */
	void insertUserInfo(List<Map<String, Object>> userAddList);

	/**
	 * 查询需要备份的文件
	 * @return
	 */
	List<Map<String, Object>> getFileBack();

	/**
	 * 更细发票状态
	 * @param fileInfo
	 */
	void updateInvoiceStatus(Map<String, Object> fileInfo);

	/**
	 * 保存文件信息
	 * @param fileInfo
	 */
	void addFileInfo(Map<String, Object> fileInfo);

	/**
	 * 获取需要反补的发票列表
	 * @return
	 */
	List<Map<String, Object>> getInvoiceApply();

	/**
	 * 更新发票主信息
	 * @param invoiceInfo
	 */
	void updateInvoice(FPKJXX_FPTXXContent invoiceInfo);

	/**
	 * 更新明细信息
	 * @param detail
	 */
	void updateInvoiceDetail(FPKJXX_XMXXContent detail);


}
