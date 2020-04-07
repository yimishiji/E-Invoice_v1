package com.rongzer.efapiao.service;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.dao.FapiaoFileMapper;
import com.rongzer.rdp.common.util.StringUtil;

@Service("noteRequstLogService")
public class NoteRequstLogService extends BaseBusinessService {

	@Autowired
	private FapiaoFileMapper fapiaoFileMapper;
	
	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, Object> process(Map<String, Object> paramMap) {
		if (!paramMap.containsKey("method")) {
		} else {
			String methodName = (String) paramMap.get("method");
			try {
				Method method = this.getClass().getMethod(methodName, Map.class);
				paramMap = (Map<String, Object>) method.invoke(this, paramMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return paramMap;
	}

	public void noteRequestLog(Map<String, Object> paramMap){
		String requestt_pk = StringUtil.getUuid32();
		paramMap.put("REQUESTT_PK", requestt_pk);//获取主键
		fapiaoFileMapper.saveRequestContent(paramMap);//保存日志信息
	}
}
