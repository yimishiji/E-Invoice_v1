package com.rongzer.efapiao.dao;
import com.rongzer.efapiao.model.InvoiceOrder;
import com.rongzer.efapiao.model.TransactionRelation;
import org.springframework.stereotype.Repository;
import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;

import java.util.List;
import java.util.Map;

@Repository("invoiceMapper")
public interface InvoiceMapper {


    void saveInvoice(List<FPKJXX_FPTXXContent> invoiceList);

    void saveInvoiceDetail(List<FPKJXX_XMXXContent> invoiceDetailList);

    void updateInvoice(FPKJXX_FPTXXContent invoice);

    void updateInvoiceDetail(List<FPKJXX_XMXXContent> invoiceDetail);

    void saveOrder(InvoiceOrder order);

    void updateOrder(InvoiceOrder order);

    Map<String,Object> getOrder(Map<String, Object> params);

    Map<String,Object> getRelation(Map<String, Object> params);

    void saveRelation(TransactionRelation relation);

    void insertInvoiceOrder(Map<String, String> order_map);

    void insertInvoiceOrderTrans(List<Map<String,String>> order_x_trans_map_list);

    List<String> getTrans(List<String> trans_orginal);

    List<Map<String,String>> getTaxpayerStore(List<String> stores);

    List<String> listTransOrders(List<String> trans_orginal);

	List<Map<String, Object>> getInvoiceApply(String downLoadDate);

	List<Map<String, Object>> getFileBack();

	void updateInvoiceStatus(Map<String, Object> fileInfo);

	void addFileInfo(Map<String, Object> fileInfo);
    List<Map<String,String>> selectInvoiceInfoByOrderId(String param);

	void updateOrderById(Map<String, Object> order);

	List<Map<String,Object>> getTransRelation(String orderId);

    Map<String,Object> getRelationOrder(Map<String, Object> paramMap);

    Map<String,Object> getForbiddenPickCode(Map<String, Object> paramMap);

    Map<String,Object> getOrderByRequestNum(Map<String, Object> param);

    void updateOrderResponseContext(Map<String, Object> orderMap);

    Map<String,Object> getStoreInfoByStoreNo(String store_no);

    List<Map<String,String>> getInvoiceDetailByInvoiceId(String invoiceId);

    Map<String,Object> getInvoiceInfoByInvoiceId(String invoiceId);

    Map<String,String> getCardIdByTaxpayer(String taxpayer);

    Map<String,String> getPdfByInvoiceId(String invoiceId);

    void updateInvoiceWeChatStatus(Map<String, Object> param);

    void updateOrdersAuthStatus(Map<String, Object> paramMap);

    void updateInvoiceWechatCode(Map<String, Object> paramMap);

    Map<String,Object> getTransDate(Map<String, Object> paramMap);


    Map<String,String> getCardTemplate();


    List<Map<String, Object>> getInvoiceInfoByOrderId(String orderId);

    List<Map<String,Object>> getInvoiceDetail(String orderId);

    void insertRecord(Map<String, String> recordMap);

    void insertTask(Map<String, String> recordMap);

    void updateTask(Map<String, String> recordMap);

    List<Map<String,Object>> getInvoiceCallBackTask();

}
