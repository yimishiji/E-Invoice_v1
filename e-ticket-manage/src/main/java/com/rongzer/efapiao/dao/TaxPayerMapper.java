package com.rongzer.efapiao.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author
 * @create 2017-06-01 14:25
 **/
@Repository
public interface TaxPayerMapper {
    List<String> listNsrsbh();

    void insertTaxPayer(List<Map<String,Object>> insertTaxpayerList);

    void updateTaxPayer(List<Map<String,Object>> insertTaxpayerList);

    Map<String,Object> getTaxpayerById(Map<String, Object> paramMap);

    Map<String,String> getTaxpayerByNsrsbh(String taxpayerId);

    List<Map<String,Object>> getTaxPayerList();

    void updateTaxPayerInvoiceInventory(Map<String, String> param);
}
