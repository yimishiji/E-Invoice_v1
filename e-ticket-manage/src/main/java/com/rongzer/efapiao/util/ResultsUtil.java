package com.rongzer.efapiao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/29.
 */
public class ResultsUtil {

    private static final int STATUS_SUCCESS=1;
    private static final int STATUS_ERROR=0;
    private static final boolean SUCCESS_OK=true;
    private static final boolean SUCCESS_FAIL=false;


    /**
     * 成功
     * @param data
     * @return
     */
    public static Map<String ,Object> success(Object data){
        Map<String ,Object> map=new HashMap<>();
        map.put("success",SUCCESS_OK);
        map.put("status",STATUS_SUCCESS);
        map.put("data",data);
        map.put("errorMessage","");
        return map;
    }

    public static Map<String ,Object> success(){
        Map<String ,Object> map=new HashMap<>();
        map.put("success",SUCCESS_OK);
        map.put("status",STATUS_SUCCESS);
        map.put("data","successful");
        map.put("errorMessage","");
        return map;
    }

    /**
     * 失败
     * @param
     * @return
     */
    public static Map<String ,Object> error(String errorMessage){
        Map<String ,Object> map=new HashMap<>();
        map.put("success",SUCCESS_FAIL);
        map.put("status",STATUS_ERROR);
        map.put("data",null);
        map.put("errorMessage",errorMessage);
        return map;
    }

    /**
     * 失败
     * @param data,errorMessage
     * @return
     */
    public static Map<String ,Object> error(Object data,String errorMessage){
        Map<String ,Object> map=new HashMap<>();
        map.put("success",SUCCESS_FAIL);
        map.put("status",STATUS_ERROR);
        map.put("data",data);
        map.put("errorMessage",errorMessage);
        return map;
    }

}
