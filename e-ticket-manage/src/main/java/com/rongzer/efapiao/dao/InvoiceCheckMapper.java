package com.rongzer.efapiao.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * ${DESCRIPTION}
 *
 * @author
 * @create 2017-06-21 14:27
 **/
@Repository
public interface InvoiceCheckMapper {
    Map<String,Object> getInvoiceRegister(Map<String, Object> invoiceTmp);

    Map<String,Object> getInvoice(Map<String, Object> invoiceTmp);

    void saveInvoiceRegister(Map<String, Object> invoiceTmp);

	int getInvoiceInfoCount(Map<String, Object> params);

	List<Map<String, Object>> getInvoiceInfo(Map<String, Object> params);

	List<Map<String, Object>> getTransactionInfo(Map<String, Object> mapTemp);

    List<Map<String,Object>> getInvoiceExceptionInfo(Map<String, Object> params);

    int getInvoiceExceptionInfoCount(Map<String, Object> params);
}
