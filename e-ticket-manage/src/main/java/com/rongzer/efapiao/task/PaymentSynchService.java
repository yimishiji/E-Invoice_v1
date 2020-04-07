package com.rongzer.efapiao.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.TaskServiceMapper;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPBaseService;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.HttpUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;

/**
 * 支付方式同步
 * 
 * @author qrl
 * 
 */
@Service("paymentSynchService")
public class PaymentSynchService implements RDPBaseService {

	@Autowired
	private TaskServiceMapper taskServiceMapper;

	@Override
	public String execute(String arg0) {
		StringBuffer result = new StringBuffer();
		{
			// 获取支付方式分类
			Map<String, Object> paymentInfoOld = getPaymentInfo();
			// 获取接口支付方式
			List<Map<String, Object>> paymentlist = getPaymentList();
			// 处理同步信息
			Map<String, Object> storeResponse = addPaymentInfo(paymentInfoOld,paymentlist);
			String resultCode = StringUtil.toStringWithEmpty(storeResponse.get("resultCode"));
			if ("success".equals(resultCode)) {
				result.append("store infomation access succeed."
						+ storeResponse.get("resultDesc"));
			} else {
				result.append("store infomation access failed.").append(
						storeResponse.get("resultDesc"));
			}
			updateCache();
		}
		return result.toString();
	}

	/**
	 * 保存新增的支付方式
	 * 
	 * @param paymentInfoOld
	 * @param paymentlist
	 * @return
	 */
	private Map<String, Object> addPaymentInfo(
			Map<String, Object> paymentInfoOld,
			List<Map<String, Object>> paymentlist) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String resultCode = "success";
		String resultDesc = "";
		try {
			if (CollectionUtil.isNotEmpty(paymentlist)) {
				List<Map<String, Object>> paymentAddList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> paymentUpdateList = new ArrayList<Map<String, Object>>();
				// 循环遍历门店list，有放入更新列表，无放入新增列表
				String paymentCode = "";
				String canInvoice = "";
				Map<String, Object> mapTemp = new HashMap<String, Object>();
				for (int i = 0; i < paymentlist.size(); i++) {
					mapTemp = paymentlist.get(i);
					paymentCode = StringUtil.toStringWithEmpty(mapTemp.get("code"));// 编码
					canInvoice = "1".equals(StringUtil.toStringWithEmpty(mapTemp.get("invoiceFlag")))?"E00102":"E00101";//名称
					mapTemp.put("invoiceFlag", canInvoice);
					mapTemp.put("NOWTIME", StringUtil.getNowTime());
					mapTemp.put("USER", "SYSADMIN");
					if (paymentInfoOld.containsKey(paymentCode)) {
						if (!canInvoice.equals(paymentInfoOld.get(paymentCode))) {// 分类发生调整
							paymentUpdateList.add(mapTemp);
						}
					} else {
						mapTemp.put("PAYMENT_ID", StringUtil.getUuid32());
						mapTemp.put("IS_USED", "D00002");
						paymentAddList.add(mapTemp);
					}
				}
				if (CollectionUtil.isNotEmpty(paymentAddList)) {
					taskServiceMapper.insertPaymentsInfo(paymentAddList);
				}
				if (CollectionUtil.isNotEmpty(paymentUpdateList)) {
					taskServiceMapper.updatePaymentsInfo(paymentUpdateList);
				}
				resultDesc = "add payment :" + paymentAddList.size()
						+ ";update payment :" + paymentUpdateList.size();
			} else {
				resultCode = "fail";
				resultDesc = "no payment data!";
			}
		} catch (Exception e) {
			resultCode = "fail";
			e.printStackTrace();
			resultDesc = "System error.Please contact IT supporter!";
		}
		responseMap.put("resultCode", resultCode);
		responseMap.put("resultDesc", resultDesc);
		return responseMap;
	}

	/**
	 * 查询接口的支付信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getPaymentList() {
		List<Map<String, Object>> paymentList = new ArrayList<Map<String, Object>>();
		try {
			// TODO
			String paymenturl = RDPUtil.getSysConfig("efapiao.shinho.paymentInfo");
			String payment = HttpUtil.readContentFromGet(paymenturl);
			Map<String, String> returnmap = JSONUtil.json2Map(payment);
			if (CollectionUtil.isNotEmpty(returnmap) && "1".equals(returnmap.get("status"))) {
				paymentList = JSONUtil.getJSONArrayFromStr(returnmap.get("data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paymentList;
	}

	/**
	 * 查询支付信息
	 * 
	 * @return
	 */
	private Map<String, Object> getPaymentInfo() {
		Map<String, Object> paymentInfoMap = new HashMap<String, Object>();
		try {
			// 从数据库重新获取
			List<Map<String, String>> paymentList = taskServiceMapper
					.getPaymentInfo();
			for (Map<String, String> map : paymentList) {
				paymentInfoMap.put(map.get("PAYMENT_CODE"),
						map.get("CAN_INVOICE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paymentInfoMap;
	}

	/**
	 * 获取支付方式
	 * 
	 * @param paramMap
	 * @return
	 */
	public void updateCache() {
		try {
			// 获取支付方式
			CacheClient cacheClient = (CacheClient) RDPContext.getContext()
					.getBean("cacheClient");
			Map<String, Object> paymentMap = new HashMap<String, Object>();
			List<Map<String, String>> paymentList = taskServiceMapper
					.getPaymentInfo();
			paymentMap = new HashMap<String, Object>();
			for (Map<String, String> payment : paymentList) {
				String paymentCode = (String) payment.get("PAYMENT_CODE");
				if (!paymentMap.containsKey(paymentCode)) {
					paymentMap.put(paymentCode, payment);
				} else {
					// paymentCode 异常需要处理 TODO
				}
			}
			if (CollectionUtil.isNotEmpty(paymentMap)) {
				cacheClient.save(EfapiaoConstant.CacheKey.PAYMENT, paymentMap);
			}
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

}
