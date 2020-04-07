package com.rongzer.efapiao.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author
 * @create 2017-06-06 16:59
 **/
@Repository
public interface InvoiceContentMapper {
    void insertInvoiceContent(List<Map<String,Object>> insertInvoiceContentList);

    void updateInvoiceContent(List<Map<String,Object>> updateInvoiceContentList);

    List<String> listContentCode();

    Map<String,String> getContentByCode(String commodityCode);
}
