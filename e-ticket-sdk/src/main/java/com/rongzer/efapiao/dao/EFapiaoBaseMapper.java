package com.rongzer.efapiao.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("eFapiaoBaseMapper")
public interface EFapiaoBaseMapper {

    /**
     * 获取开票内容
     */
    List<Map<String,Object>> getInvoiceContentList();

    /**
     * 获取支付内容
     */
    List<Map<String,Object>> getPaymentList();

    /**
     * 获取支付内容
     */
    List<Map<String,Object>> getGoodsInfoList();

    /**
     * 获取门店信息
     */
    List<Map<String,Object>> getStroeInfoList();

    /**
     * 获取分类信息
     */
    List<Map<String,Object>> getGroupInfoList();

    /**
     * 获取纳税人信息
     * @return
     */
	List<Map<String, Object>> getTaxpayerInfoList();

}
