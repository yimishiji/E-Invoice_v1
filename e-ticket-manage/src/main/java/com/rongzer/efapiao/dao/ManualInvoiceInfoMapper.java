package com.rongzer.efapiao.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author
 * @create 2017-06-14 9:38
 **/
@Repository
public interface ManualInvoiceInfoMapper {
    void saveOrder(Map<String, Object> manualInvoice);

    void saveRelation(List<Map<String,Object>> orderRelations);

    void updateOrderStatus(Map<String, Object> params);

	Map<String, Object> getOrderById(String orderId);

    Map<String,Object> getInvoiceInfoById(String orderId);

    List<Map<String,Object>> getInvoiceHandlerDetailById(String invoice_id);

    Map<String,Object> getInvoiceContentByCode(String commodityCode);

	List<Map<String, Object>> getOriginalInfolistById(String orderId);

	List<Map<String, Object>> getInvoiceDetailById(String invoiceId);

    void insertInvoiceOrder(List<Map<String,String>> orders);

    void insertInvoiceInfo(List<Map<String,String>> invoiceInfos);

    void insertInvoiceDetail(List<Map<String,String>> details);

    void cancelInvoice(Map<String, Object> params);

    Map<String,Object> getTransInfoByTransNum(Map<String, Object> params);

    int forbiddenExtractedCode(Map<String, Object> param);

    Map<String,Object> getForbiddenByExtractedCode(Map<String, Object> extractedCode);

    int updateForbiddenByExtractedCode(Map<String, Object> extractedCode);
}
