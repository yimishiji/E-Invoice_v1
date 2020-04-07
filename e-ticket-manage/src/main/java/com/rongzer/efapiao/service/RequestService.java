package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("requestService")
public class RequestService extends BaseBusinessService {

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
     * 处理前台请求，跳转不同页面
     *
     * @param paramMap
     * @return
     */
    public Map<String, Object> dealRequest(Map<String, Object> paramMap) {
        final String cookieValue = (String) paramMap.get("COOKIE_VALUE");//获取Cookie
        Map<String, Object> requestMap = new HashMap<String, Object>();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        String json = (String) paramMap.get("t");
        String sign = (String) paramMap.get("s");
        String appId = (String) paramMap.get("a");
        if (StringUtil.isNotEmpty(json) && StringUtil.isNotEmpty(sign) && StringUtil.isNotEmpty(appId)) {
            requestMap.put("COOKIE_VALUE", cookieValue);
            requestMap.put("JSON", json);
            requestMap.put("SIGN", sign);
            requestMap.put("APP_ID", appId);
            RDPUtil.execBaseBizService("requestRemoteService", "dealRequest", requestMap);
            final Map<String, Object> responseMap = new HashMap<String,Object>();
            //心跳获取请求状态 跳转不同页面
            class Task extends TimerTask {
                private Timer timer;

                public Task(Timer timer) {
                    this.timer = timer;
                }
                int i = 0;
                Boolean isActive = true;
                @Override
                public void run(){
                    //发票信息
                    CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
                    Map<String, Object> tmp = new HashMap<String,Object>();
                    try {
                        tmp = (Map<String, Object>) cacheClient.get(cookieValue);
                    } catch (MemcachedException e) {
                        e.printStackTrace();
                    }
                    if(CollectionUtil.isNotEmpty(tmp)){
                        responseMap.putAll(tmp);
                        isActive = false;
                        timer.cancel();
                    }
                    if(i++>5){
                        isActive = false;
                        timer.cancel();
                    }
                }
            }
            Timer timer= new Timer();
            Task task = new Task(timer);
            timer.schedule(task, new Long(1000), new Long(5000));

            while(task.isActive) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //判断返回状态
            if(CollectionUtil.isNotEmpty(responseMap)){
                returnMap.put("STATUS", EfapiaoConstant.PageStatus.TRANSACTION_SUCCESS);
            }

        } else {//参数传递错误，直接跳转至提取码输入页面
            returnMap.put("STATUS", EfapiaoConstant.PageStatus.TRANSACTION_EMPTY);//进入提取码输入页面
        }
        return returnMap;
    }
}
