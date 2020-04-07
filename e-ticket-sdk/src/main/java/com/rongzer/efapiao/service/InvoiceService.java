package com.rongzer.efapiao.service;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.util.CollectionUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.InvoiceMapper;
import com.rongzer.efapiao.dao.RequestRemoteMapper;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.model.TransactionItem;
import com.rongzer.efapiao.model.TransactionPayment;
import com.rongzer.rdp.common.util.StringUtil;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("invoiceService")
public class InvoiceService extends BaseBusinessService {

	private static Logger logger = Logger.getLogger(InvoiceService.class);
	
    @Autowired
    private InvoiceMapper invoiceMapper;
    
	@Autowired
	private RequestRemoteMapper requestRemoteMapper;

    @SuppressWarnings("unchecked")
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
     * 根据提取码(已解密)获取交易信息
     * @param paramMap 
     * @return 
     */
    public Map<String,Object> getTransaction(Map<String, Object> paramMap){
    	Map<String,Object> resultMap = new HashMap<String,Object>();//方法返回对象 
		String pickUpCode = (String) paramMap.get("PICKUPCODE");//获取提取码
		String withoutCacheStr = (String) paramMap.get("WITHOUT_CACHE");//是否不从缓存读取
		boolean withoutCache = false;
		if(StringUtil.isNotEmpty(withoutCacheStr) && "true".equals(withoutCacheStr)){
			withoutCache = true;
		}
		Transaction transaction = null;
		if(withoutCache){
			transaction = queryTransDataFromDB(pickUpCode);
		}else{
			//缓存中没有则从数据库中查询
			transaction = (Transaction) getCache(pickUpCode);
			if(transaction == null){
				transaction = queryTransDataFromDB(pickUpCode);
			}
		}
		//返回交易信息
		resultMap.put("transactionData", transaction);
		return resultMap;
	}
    
    /**
     * 去数据库查询对应的交易和相关订单信息,并且更新缓存
     * @param pickUpCode
     * @return
     */
	private Transaction queryTransDataFromDB(String pickUpCode) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("TRANSACTIONEQUE", pickUpCode);
		Transaction orderInfo = requestRemoteMapper.getTransData(params);// 根据提取码查询订单主数据
		if (orderInfo != null) {
			//如果开票状态为空，则认为是未开票
			orderInfo.setTRANSACTION_MONTH(orderInfo.getTRANSACTION_DATETIME().substring(0, 7));
			if(StringUtil.isEmpty(orderInfo.getINVOICE_STATUS())){
				orderInfo.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.NO_INVOICE);
			}
			//判断该提取码是不是禁用了
			params.put("extractedCode", ShinHoDataUtil.deCode(pickUpCode));
			Map<String, Object> returnMap = checkPickupCodeIsForbidden(params);
			if(CollectionUtil.isNotEmpty(returnMap)){
				Boolean suc = (Boolean)returnMap.get("suc");
				if(!suc) {
					orderInfo.setIS_FORBINDDEN("YES");
				}
			}
			params.put("TRANSACTION_ID", orderInfo.getTRANSACTION_ID());
			params.put("ALLOWED_INVOICE", orderInfo.getALLOWED_INVOICE());
			params.put("INVOICE_STATUS", orderInfo.getINVOICE_STATUS());
			List<TransactionItem> salesItemList = requestRemoteMapper.getSalesItems(params);// 商品信息
			List<TransactionPayment> paymentList = requestRemoteMapper.getPayments(params);// 支付记录
			// 将商品和支付信息放入order对象
			orderInfo.setTransactionItemList(salesItemList);
			orderInfo.setTransactionPaymentList(paymentList);
			saveCache(pickUpCode, orderInfo);
		}
		return orderInfo;
	}


	/**
	 * 判断提取码能否进行开票
	 * 1.判断该提取码是不是禁用了,如果禁用了则给予提示，根据提取码查找表t_extracted_forbidden
	 * 2.根据门店号查询t_store_info 查看门店是否允许开票，如果不允许给予提示
	 * 3.根据提取码查询T_INVOICE_TRANACTION_RELATION,T_INVOICE_ORDER表中蓝票和红票的数量，如果数量相同，则可以开票，
	 * @param paramMap
	 * @return
	 */
	public Map<String,Object> checkPickupCodeCanInvoice(Map<String, Object> paramMap){
		Map<String,Object>  returnMap = new HashMap<String,Object>();
		Map<String,Object> contentMap = null;
		//判断该提取码是不是禁用了
		returnMap = checkPickupCodeIsForbidden(paramMap);
		if(CollectionUtil.isNotEmpty(returnMap)){
			Boolean suc = (Boolean)returnMap.get("suc");
			if(!suc) {
				return returnMap;
			}
		}
		//根据提取码查询T_INVOICE_TRANACTION_RELATION,T_INVOICE_ORDER表中蓝票和红票的数量
		contentMap = invoiceMapper.getRelationOrder(paramMap);
		if(CollectionUtil.isNotEmpty(contentMap)){
			String redCount = StringUtil.safeToString(contentMap.get("RED_COUNT"));
			String blueCount = StringUtil.safeToString(contentMap.get("BLUE_COUNT"));
			//如果蓝票数量和红票数量相等，则可以开票
			if(!redCount.equals(blueCount)){
				returnMap.put("suc", false);
				returnMap.put("msg", "该提取码已开票");
				return returnMap;
			}
		}
		returnMap = new HashMap<String,Object>();
		returnMap.put("suc", true);
		return returnMap;
	}

	public Map<String, Object> checkStoreCanInvoice(Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		returnMap.put("suc", true);
		Map<String, Object> contentMap;
		String extractedCode = StringUtil.safeToString(paramMap.get("extractedCode"));
		String pickCode = ShinHoDataUtil.deCode(extractedCode);
		String store_no = pickCode.substring(0,4);
		contentMap = invoiceMapper.getStoreInfoByStoreNo(store_no);
		if(CollectionUtil.isNotEmpty(contentMap)) {
			String allowedInvoice = StringUtil.safeToString(contentMap.get("ALLOWED_INVOICE"));
			String taxpayerId = StringUtil.safeToString(contentMap.get("TAXPAYER_ID"));
			if (!EfapiaoConstant.DefaultKey.STORE_ALLOWED_INVOICE.equals(allowedInvoice)
					|| StringUtil.isEmpty(taxpayerId)) {
				returnMap.put("suc", false);
				returnMap.put("msg", "门店" + store_no + "暂时不允许开票");
				return returnMap;
			}
		}else{
			returnMap.put("suc", false);
			returnMap.put("msg", "门店" + store_no + "信息不存在");
			return returnMap;
		}
		return returnMap;
	}

	public Map<String,Object> checkPickupCodeIsForbidden(Map<String, Object> paramMap){
		//判断该提取码是不是禁用了
		Map<String,Object> returnMap = invoiceMapper.getForbiddenPickCode(paramMap);
		if(CollectionUtil.isNotEmpty(returnMap)){
			returnMap.put("suc", false);
			returnMap.put("msg", "该提取码已被禁用!");
			return returnMap;
		}
		returnMap = new HashMap<String,Object>();
		returnMap.put("suc", true);
		return returnMap;
	}

	public Map<String,Object> checkPickupCodeHasInvoice(Map<String, Object> paramMap){
		Map<String,Object>  returnMap = new HashMap<String,Object>();
		Map<String,Object> contentMap = null;
		//判断该提取码是不是禁用了
		returnMap = checkPickupCodeIsForbidden(paramMap);
		if(CollectionUtil.isNotEmpty(returnMap)){
			Boolean suc = (Boolean)returnMap.get("suc");
			if(!suc) {
				returnMap.put("msg", "pickupCodeForbidden");
				return returnMap;
			}
		}
		//根据提取码查询T_INVOICE_TRANACTION_RELATION,T_INVOICE_ORDER表中蓝票和红票的数量
		/*contentMap = invoiceMapper.getRelationOrder(paramMap);
		if(CollectionUtil.isNotEmpty(contentMap)){
			String redCount = StringUtil.safeToString(contentMap.get("RED_COUNT"));
			String blueCount = StringUtil.safeToString(contentMap.get("BLUE_COUNT"));
			//如果蓝票数量和红票数量相等，则可以开票
			if(!redCount.equals(blueCount)){
				returnMap.put("suc", false);
				returnMap.put("msg", "pickupCodeHasInvoice");
				return returnMap;
			}
		}*/
		returnMap = new HashMap<String,Object>();
		returnMap.put("suc", true);
		return returnMap;
	}

	/**
	 * 更新订单表中的responsecontext（接口响应信息）
	 * @param param
	 * @return
	 */
	public void updateOrderResponseContext(Map<String,Object> param){
		//根据发票流水查询是否存在订单
		Map<String,Object> orderMap = invoiceMapper.getOrderByRequestNum(param);
		//如果存在则更新接口返回信息
		if(CollectionUtil.isNotEmpty(orderMap)){
			orderMap.put("responseContext",param.get("returnMsg"));
			invoiceMapper.updateOrderResponseContext(orderMap);
		}
	}
}
