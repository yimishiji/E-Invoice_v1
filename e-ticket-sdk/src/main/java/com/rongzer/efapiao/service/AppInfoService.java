package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.util.HMAC;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by he.bing on 2017/2/7.
 */
@Service("appInfoService")
public class AppInfoService extends BaseBusinessService {

    @Override
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

    /**
     *校验传递的url参数是否正确
     * @param paramMap
     * @return
     */
    public Map<String, Object> checkJson(Map<String, Object> paramMap){
        Map<String,Object> returnMap = new HashMap<String, Object>();
        String appId = (String) paramMap.get("APP_ID");
        String json = (String) paramMap.get("JSON");
        String sign = (String) paramMap.get("SIGN");
        Map<String,Object> appList = new HashMap<String,Object>();
        Map<String,Object> appInfo = (Map<String, Object>) appList.get("appId");
        //String appSecret = (String) appInfo.get("APP_SECRET");
        String appSecret =  "imjCkUsm+VVZNatR/fC/kldbhSkfT+fp5OYGqFnY5O1KMPoZwwxo6EJwSG04Y3ZLbbghLE0mWqfp";
        //对json体进行加密
        String checkSign = HMAC.encryptHMAC(json, appSecret);
        if(sign.equals(checkSign)){
            //校验成功
            returnMap.put("STATUS", EfapiaoConstant.PageStatus.APP_SUCCESS);
        }else{
            //校验失败
            returnMap.put("STATUS",EfapiaoConstant.PageStatus.APP_ERROR);
        }
        return returnMap;
    }

}