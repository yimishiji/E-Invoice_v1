package com.rongzer.efapiao.service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.EFapiaoBaseMapper;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by he.bing on 2017/2/7.
 */
@Service("eFapiaoBaseService")
public class EFapiaoBaseService extends BaseBusinessService {

    @Autowired
    private EFapiaoBaseMapper eFapiaoBaseMapper;

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
     * 获取开票过程中需要的基础数据
     *
     * @param paramMap
     * @return
     */
    public Map<String, Object> getBaseData(Map<String, Object> paramMap) {
        Map<String, Object> baseData = new HashMap<String, Object>();
        //获取开票内容
        Map<String, Object> invoiceContentMap = getInvoiceContentMap(paramMap);
        Map<String, Object> paymentMap = getPaymentMap(paramMap);
        Map<String, Object> goodsInfoMap = getGoodsInfoMap(paramMap);
        Map<String, Object> storeInfoMap = getStoreInfoMap(paramMap);
        Map<String, Object> groupInfoMap = getGroupInfoMap(paramMap);
        Map<String, Object> taxpayerInfoMap = getTaxpayerInfoMap(paramMap);
        //组合对应基础信息，返回map
        baseData.put(EfapiaoConstant.CacheKey.INVOICE_CONTENT, invoiceContentMap);
        baseData.put(EfapiaoConstant.CacheKey.PAYMENT, paymentMap);
        baseData.put(EfapiaoConstant.CacheKey.GOODS_INFO, goodsInfoMap);
        baseData.put(EfapiaoConstant.CacheKey.STORE_INFO, storeInfoMap);
        baseData.put(EfapiaoConstant.CacheKey.GROUP_INFO, groupInfoMap);
        baseData.put(EfapiaoConstant.CacheKey.TAXPAYER_INFO, taxpayerInfoMap);
        return baseData;
    }

    /**
     * 查询开票内容
     * @param paramMap
     * @return
     */
    public Map<String, Object> getInvoiceContentMap(Map<String, Object> paramMap) {
    	 Map<String, Object> invoiceContentMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.INVOICE_CONTENT);
         if (CollectionUtil.isEmpty(invoiceContentMap)) {
             List<Map<String, Object>> invoiceContentList = eFapiaoBaseMapper.getInvoiceContentList();
             invoiceContentMap = new HashMap<String, Object>();
             for (Map<String, Object> invoiceContent : invoiceContentList) {
                 String contentId = (String) invoiceContent.get("CONTENT_ID");
                 if (!invoiceContentMap.containsKey(contentId)) {
                     invoiceContentMap.put(contentId, invoiceContent);
                 } else {
                     //存在相同contentCode 异常需要处理 TODO
                 }
             }
             if (CollectionUtil.isNotEmpty(invoiceContentMap)) {
                 saveCache(EfapiaoConstant.CacheKey.INVOICE_CONTENT, invoiceContentMap);
             }
         }
    	return invoiceContentMap;
    }
    
    /**
     * 获取支付方式
     * @param paramMap
     * @return
     */
    public Map<String, Object> getPaymentMap(Map<String, Object> paramMap) {
    	//获取支付方式
        Map<String, Object> paymentMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.PAYMENT);
        if (CollectionUtil.isEmpty(paymentMap)) {
            List<Map<String, Object>> paymentList = eFapiaoBaseMapper.getPaymentList();
            paymentMap = new HashMap<String, Object>();
            for (Map<String, Object> payment : paymentList) {
                String paymentCode = (String) payment.get("PAYMENT_CODE");
                if (!paymentMap.containsKey(paymentCode)) {
                    paymentMap.put(paymentCode, payment);
                } else {
                    //paymentCode 异常需要处理 TODO
                }
            }
            if (CollectionUtil.isNotEmpty(paymentMap)) {
                saveCache(EfapiaoConstant.CacheKey.PAYMENT, paymentMap);
            }
        }
    	return paymentMap;
    }
    
    /**
     * 获取商品信息
     * @param paramMap
     * @return
     */
    public Map<String, Object> getGoodsInfoMap(Map<String, Object> paramMap) {
    	 //获取货品信息
        Map<String, Object> goodsInfoMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.GOODS_INFO);
        if (CollectionUtil.isEmpty(goodsInfoMap)) {
            List<Map<String, Object>> goodsInfoList = eFapiaoBaseMapper.getGoodsInfoList();
            goodsInfoMap = new HashMap<String, Object>();
            for (Map<String, Object> goodsInfo : goodsInfoList) {
                String goodsCode = (String) goodsInfo.get("GOODS_CODE");
                if (!goodsInfoMap.containsKey(goodsCode)) {
                    goodsInfoMap.put(goodsCode, goodsInfo);
                } else {
                    //goodsCode 异常需要处理 TODO
                }
            }
            if (CollectionUtil.isNotEmpty(goodsInfoMap)) {
                saveCache(EfapiaoConstant.CacheKey.GOODS_INFO, goodsInfoMap);
            }
        }
    	return goodsInfoMap;
    }
    
    /**
     * 获取门店信息
     * @param paramMap
     * @return
     */
    public Map<String, Object> getStoreInfoMap(Map<String, Object> paramMap) {
    	 //获取门店信息
        Map<String, Object> storeInfoMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.STORE_INFO);
        if (CollectionUtil.isEmpty(storeInfoMap)) {
            List<Map<String, Object>> storeInfoList = eFapiaoBaseMapper.getStroeInfoList();
            storeInfoMap = new HashMap<String, Object>();
            for (Map<String, Object> storeInfo : storeInfoList) {
                String storeNo = (String) storeInfo.get("STORE_NO");
                if (!storeInfoMap.containsKey(storeNo)) {
                    storeInfoMap.put(storeNo, storeInfo);
                } else {
                    //storeInfoCode 异常需要处理 TODO
                }
            }
            if (CollectionUtil.isNotEmpty(storeInfoMap)) {
                saveCache(EfapiaoConstant.CacheKey.STORE_INFO, storeInfoMap);
            }
        }
    	return storeInfoMap;
    }
    
    /**
     * 获取商品分类
     * @param paramMap
     * @return
     */
    public Map<String, Object> getGroupInfoMap(Map<String, Object> paramMap) {
    	  //获取分组和开票内容之间的关系
        Map<String, Object> groupInfoMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.GROUP_INFO);
        if (CollectionUtil.isEmpty(groupInfoMap)) {
            List<Map<String, Object>> groupInfoList = eFapiaoBaseMapper.getGroupInfoList();
            groupInfoMap = new HashMap<String, Object>();
            for (Map<String, Object> groupInfo : groupInfoList) {
                String groupId = (String) groupInfo.get("GROUP_ID");
                if (!groupInfoMap.containsKey(groupId)) {
                    groupInfoMap.put(groupId, groupInfo);
                } else {
                    //storeInfoCode 异常需要处理 TODO
                }
            }
            if (CollectionUtil.isNotEmpty(groupInfoMap)) {
                for (Map.Entry<String, Object> entry : groupInfoMap.entrySet()) {
                    Map<String, Object> tmp = (Map<String, Object>) entry.getValue();
                    String groupId = StringUtil.safeToString(tmp.get("GROUP_ID"));
                    String contentId = getContentId(groupId, groupInfoMap);
                    tmp.put("CONTENT_ID",contentId);
                }
                saveCache(EfapiaoConstant.CacheKey.GROUP_INFO, groupInfoMap);
            }
        }
     
    	return groupInfoMap;
    }
    
    /**
     * 获取纳税人信息
     * @param paramMap
     * @return
     */
    public Map<String, Object> getTaxpayerInfoMap(Map<String, Object> paramMap) {
    	 //获取分组和开票内容之间的关系
        Map<String, Object> taxpayerInfoMap = (Map<String, Object>) getCache(EfapiaoConstant.CacheKey.TAXPAYER_INFO);
        if (CollectionUtil.isEmpty(taxpayerInfoMap)) {
            List<Map<String, Object>> taxpayerInfoList = eFapiaoBaseMapper.getTaxpayerInfoList();
            taxpayerInfoMap = new HashMap<String, Object>();
            for (Map<String, Object> taxpayerInfo : taxpayerInfoList) {
                String taxpayerIdentifyNo = (String) taxpayerInfo.get("TAXPAYER_IDENTIFY_NO");
                if (!taxpayerInfoMap.containsKey(taxpayerIdentifyNo)) {
                	taxpayerInfoMap.put(taxpayerIdentifyNo, taxpayerInfo);
                } else {
                    //storeInfoCode 异常需要处理 TODO
                }
            }
            if (CollectionUtil.isNotEmpty(taxpayerInfoMap)) {
                saveCache(EfapiaoConstant.CacheKey.TAXPAYER_INFO, taxpayerInfoMap);
            }
        }
    	return taxpayerInfoMap;
    }    
    
    public String getContentId(String groupId, Map<String, Object> groupInfoMap) {
        String contentId = "";
        Map<String, Object> groupInfo = (Map<String, Object>) groupInfoMap.get(groupId);
        if (CollectionUtil.isNotEmpty(groupInfo)) {
            contentId = StringUtil.safeToString(groupInfo.get("CONTENT_ID"));
            if (StringUtil.isEmpty(contentId)) {
                String parentId = StringUtil.safeToString(groupInfo.get("PARENT_GROUP_ID"));
                contentId = getContentId(parentId, groupInfoMap);
            }
        }
        return contentId;
    }

}