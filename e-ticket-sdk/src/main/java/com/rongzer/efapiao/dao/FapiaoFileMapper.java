package com.rongzer.efapiao.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("fapiaoFileMapper")
public interface FapiaoFileMapper {

	void updateInvoiceStatus(Map<String, Object> fileInfo);

	void addFileInfo(Map<String, Object> fileInfo);

	Map<String, Object> queryFileInfoById(String bizId);

	void saveRequestContent(Map<String, Object> paramMap);

}
