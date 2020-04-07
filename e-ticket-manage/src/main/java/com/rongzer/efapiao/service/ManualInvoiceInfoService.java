package com.rongzer.efapiao.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.InvoiceContentMapper;
import com.rongzer.efapiao.dao.InvoiceMapper;
import com.rongzer.efapiao.dao.ManualInvoiceInfoMapper;
import com.rongzer.efapiao.dao.RequestRemoteMapper;
import com.rongzer.efapiao.dao.TaxPayerMapper;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.model.TransactionItem;
import com.rongzer.efapiao.model.TransactionPayment;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;

import sajt.shdzfp.sl.model.Interface;

@Service("manualInvoiceInfoService")
public class ManualInvoiceInfoService extends BaseBusinessService {

	@Autowired
	private ManualInvoiceInfoMapper manualInvoiceInfoMapper;
	@Autowired
	private InvoiceMapper invoiceMapper;
	@Autowired
	private InvoiceContentMapper invoiceContentMapper;
	@Autowired
	private TaxPayerMapper taxPayerMapper;
	@Autowired
	private RequestRemoteMapper requestRemoteMapper;

	private static Logger logger = Logger.getLogger(ManualInvoiceInfoService.class);


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
	 * Object转String
	 *
	 * @param obj
	 * @return
	 */
	public String objectToStr(Object obj) {
		String returnStr = "";
		if (obj instanceof String) {
			if (StringUtil.isNotEmpty(obj)) {
				returnStr = (String) obj;
			}
		} else if (obj instanceof Double) {
			returnStr = Double.toString((Double) obj);
		} else if (obj instanceof BigDecimal) {
			returnStr = obj.toString();
		}
		return getReplaceString(returnStr);
	}

	// 保留a-z|A-Z|0-9|所有中文|空格|,|.|()|（）|-|#|《|》|等符合规范的字符
	public static String getReplaceString(String str) {
		String regFilter = "[^(a-zA-Z0-9\\u4e00-\\u9fa5\\u0020\\u002e\\u002c\\uff08\\uff09\\u3002\\uff1f\\uff0c\\u2019\\u2022\\u0040\\u0023\\u002d\\u300a\\u300b·)]";
		return str.replaceAll(regFilter, "");
	}

	/**
	 * 发票红冲
	 * @param Map ORDER_ID:订单主键 USER_ID 用户主键 RED_STATUS 红冲审核状态
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> redBufferApply(Map<String,Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String orderId = StringUtil.safeToString(params.get("ORDER_ID"));
		String redStatus = StringUtil.safeToString(params.get("RED_STATUS"));
		String returnCode = "";
		String returnMsg = "";
		if(StringUtil.isNotEmpty(orderId)){
			//获取当前登录系统的用户，作为更新人信息保存数据库
			params.put("NOWTIME", StringUtil.getNowTime());
			//更新订单状态
			manualInvoiceInfoMapper.updateOrderStatus(params);
			returnCode = "true";
			if("DV0102".equals(redStatus)){
				//查询必要信息进行红冲
				returnMap = invoiceRedBuffer(params);
			}
		}else{
			returnCode = "false";
			returnMsg = "无效的订单";
		}
		returnMap.put("suc", returnCode);
		returnMap.put("msg", returnMsg);
		return returnMap;
	}

	/**
	 * 审核通过后进行红冲
	 * @param params
	 * @return
	 */
	private Map<String, Object> invoiceRedBuffer(Map<String, Object> params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//通过发票主键获取到提取码
		String orderId = (String) params.get("ORDER_ID");
		/**手工开票**/
		Map<String,Object> orderMap = manualInvoiceInfoMapper.getOrderById(orderId);//获取订单主信息
		List<Map<String,Object>> originalInfolist = manualInvoiceInfoMapper.getOriginalInfolistById(orderId);//获取原始发票信息
		if(CollectionUtil.isNotEmpty(originalInfolist)){
			//生成红冲订单
			String orderIdNew = StringUtil.getUuid32();
			orderMap.put("PARENT_ORDER_ID", orderId);
			orderMap.put("ORDER_ID", orderIdNew);
			orderMap.put("STATUS", "");
			orderMap.put("INVOICE_STATUS", EfapiaoConstant.InvoiceStatus.NO_INVOICE);
			orderMap.put("INVOICE_TYPE", EfapiaoConstant.InvoiceType.OFF_SET);
			orderMap.put("IS_DELETE", "0");
			orderMap.put("ADD_USER", params.get("USER_ID"));
			orderMap.put("ADD_TIME", StringUtil.getNowTime());
			orderMap.put("UPDATE_USER", params.get("USER_ID"));
			orderMap.put("UPDATE_TIME", StringUtil.getNowTime());
			orderMap.put("TAXPAYER_ID", orderMap.get("TAXPAYER_IDENTIFY_NO"));
			//保存红冲订单
			manualInvoiceInfoMapper.saveOrder(orderMap);
			//组织红冲的开票信息
			for (int i = 0; i < originalInfolist.size(); i++) {
				Map<String, Object> invoiceMap = originalInfolist.get(i);
				//生成红冲发票申请
				invoiceMap.put("ORIGINAL_INVOICE_CODE", invoiceMap.get("INVOICE_CODE"));
				invoiceMap.put("ORIGINAL_INVOICE_NUMBER", invoiceMap.get("INVOICE_NUMBER"));
				invoiceMap.putAll(orderMap);
				String invoiceId = StringUtil.safeToString(invoiceMap.get("INVOICE_ID"));
				List<Map<String,Object>> invoiceDetail = manualInvoiceInfoMapper.getInvoiceDetailById(invoiceId);
				invoiceMap.put("DETAIL_LIST", invoiceDetail);
			}
			//更新订单状态为开票中
			orderMap.put("NOWTIME", StringUtil.getNowTime());
			orderMap.put("INVOICE_STATUS", EfapiaoConstant.InvoiceStatus.IN_INVOICE);
			manualInvoiceInfoMapper.updateOrderStatus(orderMap);
			issudRedInvoice(originalInfolist,orderMap);
			//清空order_id对应的缓存
			try {
				CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
				cacheClient.delete(orderId);

				//根据order_id查询出交易流水号，清除交易流水号对应的缓存信息
				List<Map<String, Object>> relationShips = invoiceMapper.getTransRelation(orderId);//获取订单关联关系
				if (CollectionUtil.isNotEmpty(relationShips)) {
					for (int i = 0; i < relationShips.size(); i++) {
						Map<String, Object> mapTemp = relationShips.get(i);
						String pickCode = StringUtil.safeToString(mapTemp.get("TRANSACTION_NUMBER"));
						cacheClient.delete(pickCode);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				returnMap.put("suc", false);
				returnMap.put("msg", "订单"+orderId+"清除缓存失败");
			}
			returnMap.put("suc", true);
			returnMap.put("msg", "红冲成功");
		}else{
			returnMap.put("suc", false);
			returnMap.put("msg", "该订单无原始发票信息");
		}
		return returnMap;
	}

	/**
	 * 生成发票信息，并进行红冲开票
	 * @param originalInfolist
	 * @param relationShips
	 * @return
	 */
	private void issudRedInvoice(
			List<Map<String, Object>> originalInfolist,
			Map<String, Object> orderMap) {
		if(CollectionUtil.isNotEmpty(originalInfolist)){
			for (Map<String, Object> invoiceMap : originalInfolist) {
				//发票内容
				FPKJXX_FPTXXContent invoiceContent = new FPKJXX_FPTXXContent();
				//发票明细
				List<FPKJXX_XMXXContent> invoiceDetailList = new ArrayList<FPKJXX_XMXXContent>();
				//放入销方和购方的信息
				//发票限额
				invoiceContent.setINVOICE_LIMIT_AMOUNT(StringUtil.safeToString(invoiceMap.get("INVOICE_LIMIT_AMOUNT")));
				//平台编码
				invoiceContent.setDSPTBM(StringUtil.safeToString(invoiceMap.get("PLATFORM_CODE")));
				invoiceContent.setREGISTRATION_CODE(StringUtil.safeToString(invoiceMap.get("REGISTRATION_CODE")));
				invoiceContent.setAUTHORIZATION_CODE(StringUtil.safeToString(invoiceMap.get("AUTHORIZATION_CODE")));
				//开票方识别号
				invoiceContent.setNSRSBH(StringUtil.safeToString(invoiceMap.get("TAXPAYER_IDENTIFY_NO")));
				//开票方名称
				invoiceContent.setNSRMC(StringUtil.safeToString(invoiceMap.get("TAXPAYER_NAME_CN")));
				//销货方识别号
				invoiceContent.setXHF_NSRSBH(StringUtil.safeToString(invoiceMap.get("TAXPAYER_IDENTIFY_NO")));
				//销货方名称
				invoiceContent.setXHFMC(StringUtil.safeToString(invoiceMap.get("TAXPAYER_NAME_CN")));
				//销货方地址
				invoiceContent.setXHF_DZ(StringUtil.safeToString(invoiceMap.get("TAXPAYER_ADDRESS")));
				//销货方电话
				invoiceContent.setXHF_DH(StringUtil.safeToString(invoiceMap.get("TAXPAYER_PHONE")));
				//销货方银行账号
				invoiceContent.setXHF_YHZH(StringUtil.safeToString(invoiceMap.get("TAXPAYER_BANK")) + StringUtil.safeToString(invoiceMap.get("TAXPAYER_ACCOUNT")));
				//购货方名称
				invoiceContent.setGHFMC(StringUtil.safeToString(invoiceMap.get("PURCHASER_NAME")));
				//购货方纳税人识别号
				invoiceContent.setGHF_NSRSBH(StringUtil.safeToString(invoiceMap.get("PURCHASER_ID")));
				//购货方纳税人地址
				invoiceContent.setGHF_DZ(StringUtil.safeToString(invoiceMap.get("PURCHASER_ADDRESS")));
				//购货方纳税人固定电话
				invoiceContent.setGHF_GDDH(StringUtil.safeToString(invoiceMap.get("PURCHASER_TEL")));
				//购货方手机
				invoiceContent.setGHF_SJ(StringUtil.safeToString(invoiceMap.get("PURCHASER_MOBILE")));
				//购货方邮箱
				invoiceContent.setGHF_EMAIL(StringUtil.safeToString(invoiceMap.get("PURCHASER_EMAIL")));
				//购货方纳税人银行账号
				invoiceContent.setGHF_YHZH(StringUtil.safeToString(invoiceMap.get("PURCHASER_BANK_ACCOUNT")));
				//开票员
				invoiceContent.setKPY(StringUtil.safeToString(invoiceMap.get("ISSUER")));
				//复核人
				invoiceContent.setFHR(StringUtil.safeToString(invoiceMap.get("REVIEW_CLERK")));
				//收款
				invoiceContent.setSKY(StringUtil.safeToString(invoiceMap.get("PAYEE")));
				//开票类型为红票
				invoiceContent.setKPLX("2");
				//KPLX为2时，原发票代码必填
				invoiceContent.setYFP_DM(StringUtil.safeToString(invoiceMap.get("INVOICE_CODE")));
				invoiceContent.setYFP_HM(StringUtil.safeToString(invoiceMap.get("INVOICE_NUMBER")));
				//订单状态
				invoiceContent.setORDER_ID(StringUtil.safeToString(invoiceMap.get("ORDER_ID")));
				List<Map<String,Object>> detailList = (List<Map<String,Object>>)invoiceMap.get("DETAIL_LIST");
				for (Map<String, Object> detailMap : detailList) {
					FPKJXX_XMXXContent detailContent = new FPKJXX_XMXXContent();
					detailContent.setXMMC(StringUtil.safeToString(detailMap.get("ITEM_TITLE")));
					detailContent.setSPBM(StringUtil.safeToString(detailMap.get("COMMODITY_CODE")));
					detailContent.setXMSL("-1");
					detailContent.setSL(StringUtil.safeToString(detailMap.get("TAX_RATE")));
					detailContent.setXMDJ(StringUtil.safeToString(detailMap.get("AMOUNT_WITHOUT_TAX")));
					detailContent.setXMJE("-"+StringUtil.safeToString(detailMap.get("AMOUNT_WITHOUT_TAX")));
					detailContent.setSE("-"+StringUtil.safeToString(detailMap.get("TAX_AMOUNT")));
					detailContent.setFPHXZ("0");//只存在正常行
					detailContent.setHSBZ("0");
					Double taxRatio = StringUtil.toDouble(StringUtil.safeToString(detailMap.get("TAX_RATE")),0d);
					if (taxRatio < 0) {
						// 不征税项目
						detailContent.setYHZCBS("1");
						detailContent.setLSLBS("2");// 非零税率
						detailContent.setZZSTSGL("不征税");// 优惠政策标识
					} else if (taxRatio > 0) {
						detailContent.setLSLBS("");// 非零税率
					} else {
						detailContent.setLSLBS("3");// 非零税率
					}
					invoiceDetailList.add(detailContent);
				}
				invoiceContent.setFpmxList(invoiceDetailList);
				//开票大类
				invoiceContent.setKPXM(invoiceDetailList.get(0).getXMMC());
				dealInvoice(invoiceContent,orderMap);//处理开票内容
			}
		}
	}

	/**
	 *  1.审核通过时，生成发票信息 t_ivoice_info   t_invoice_detail
	 *  2.审核不通过，修改订单审核状态
	 * @param params
	 * @return
	 */
	public Map<String,Object> manualInvoiceApproval(Map<String,Object> params){
		//抓取relationShip数据
		String orderId = StringUtil.safeToString(params.get("ORDER_ID"));
		/**手工开票**/
		List<Map<String,Object>> relationShips = invoiceMapper.getTransRelation(orderId);//获取订单关联关系
		Map<String,Object> orderMap = manualInvoiceInfoMapper.getOrderById(orderId);//获取开票主信息
		if(orderMap==null){
			orderMap = new HashMap<>();
			orderMap.put("success",false);
			orderMap.put("message","未找到销方名称和销方税号");
			return orderMap;
		}
		/***更新订单状态为已审核,订单状态改为手工开票中***/
		params.put("INVOICE_STATUS",EfapiaoConstant.InvoiceStatus.IN_INVOICE);
		params.put("NOWTIME",StringUtil.getNowTime());
		manualInvoiceInfoMapper.updateOrderStatus(params);
		/**end**/

		List<TransactionItem> transItems = new ArrayList<TransactionItem>();
		List<TransactionPayment> paymentItems = new ArrayList<TransactionPayment>();
		FPKJXX_FPTXXContent invoiceContent = null;
		if(CollectionUtil.isNotEmpty(relationShips)){
			//orderMap.put("ISSUER",EfapiaoConstant.DefaultKey.ISSUE_DEFAULT);
			//提取码开票
			CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
			for (int i = 0; i < relationShips.size(); i++) {
				Map<String, Object> mapTemp = relationShips.get(i);
				String pickCode = StringUtil.safeToString(mapTemp.get("TRANSACTION_NUMBER"));
				//获取交易信息,汇总到list里面去
				Transaction transactionData = getTransactionData(pickCode,cacheClient);
				transItems.addAll(transactionData.getTransactionItemList());
				paymentItems.addAll(transactionData.getTransactionPaymentList());
				//处理开票
				orderMap.put("TransactionItems", transItems);
				orderMap.put("TransactionPayments", paymentItems);
				invoiceContent = buildInvoice(orderMap);
			}
		}else{
			//手工开票，查询临时表，封装为交易信息和支付信息
			Map<String,Object> invoiceInfoHandler = manualInvoiceInfoMapper.getInvoiceInfoById(orderId);//获取开票员信息
			orderMap.putAll(invoiceInfoHandler);
			List<Map<String,Object>> detailHandler = manualInvoiceInfoMapper.getInvoiceHandlerDetailById(orderId);//获取开票员信息
			orderMap.put("invoiceDetails", detailHandler);
			invoiceContent = dealManualInvoiceContent(orderMap);
		}
		dealInvoice(invoiceContent,orderMap);
		return null;
	}

	/**
	 * 处理订单信息进行开票
	 * @param orderMap
	 */
	private FPKJXX_FPTXXContent buildInvoice(Map<String, Object> orderMap) {
		// 订单开票状态
		FPKJXX_FPTXXContent invoiceContent  = null;
		String invoiceStatus = StringUtil.safeToString(orderMap.get("INVOICE_STATUS"));
		//判断是否已开过票,未开票的才可以执行开票动作
		if (EfapiaoConstant.InvoiceStatus.IN_INVOICE.equals(invoiceStatus)
				||EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(invoiceStatus)
				||EfapiaoConstant.InvoiceStatus.ERROR_INVOICE.equals(invoiceStatus)) {
			invoiceContent  = dealInvoiceContent(orderMap);
		}
		return invoiceContent;
	}

	private void dealInvoice(FPKJXX_FPTXXContent invoiceContent,Map<String, Object> orderMap){
		//订单拆分
		List<FPKJXX_FPTXXContent> invoiceList = ShinHoDataUtil.splitInvoice(invoiceContent);
		//保存开票信息
		saveInvoice(invoiceList);
		//更新订单状态
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Boolean invoiceFlag = true;// 发票是否传输成功成功
		//批量调用开票请求接口
		String returnCode = null;
		String returnMsg = null;
		for (FPKJXX_FPTXXContent invoice : invoiceList) {
			paramMap.put("INVOICE_CONTENT", invoice);
			Map<String, Object> returnMap = RDPUtil.execBaseBizService("aisinoService", "invoiceIssued", paramMap);
			returnCode = StringUtil.toStringWithEmpty(returnMap.get("RETURN_CODE"));
			if (!"0000".equals(returnCode)) {
				invoiceFlag = false;
				Interface result = (Interface)returnMap.get("RESULT_OBJ");
				returnMsg = result.getReturnStateInfo().getReturnMessage();
				break;
			}
		}
		//下载发票信息
		if (invoiceFlag) {
			//发票下载中
			paramMap.put("NOWTIME",StringUtil.getNowTime());
			paramMap.put("INVOICE_STATUS",EfapiaoConstant.InvoiceStatus.DOWNLOADING_INVOICE);
			manualInvoiceInfoMapper.updateOrderStatus(paramMap);
			for (FPKJXX_FPTXXContent invoice : invoiceList) {
				final FPKJXX_FPTXXContent invoiceTmp = invoice;
				//心跳获取请求状态 跳转不同页面
				class Task extends TimerTask {
					private Timer timer;

					public Task(Timer timer) {
						this.timer = timer;
					}
					int i = 0;
					Boolean isActive = true;

					@Override
					public void run() {
						Map<String,Object> paramTemp = new HashMap<String,Object>();
						paramTemp.put("INVOICE_CONTENT", invoiceTmp);
						Map<String, Object> returnMap = RDPUtil.execBaseBizService("aisinoService", "invoiceDetail", paramTemp);
						String returnCode = StringUtil.toStringWithEmpty(returnMap.get("RETURNCODE"));
						if ("0000".equals(returnCode)) {
							String pdfUrl = StringUtil.toStringWithEmpty(returnMap.get("PDF_URL"));
							String invoiceNumber = StringUtil.toStringWithEmpty(returnMap.get("FP_HM"));
							String invoiceCode = StringUtil.toStringWithEmpty(returnMap.get("FP_DM"));
							String totalAmountWithoutTax = StringUtil.toStringWithEmpty(returnMap.get("HJBHSJE"));
							String totalTaxAmount = StringUtil.toStringWithEmpty(returnMap.get("KPHJSE"));
							String kprq = StringUtil.toStringWithEmpty(returnMap.get("KPRQ"));
							invoiceTmp.setINVOICE_NUMBER(invoiceNumber);
							invoiceTmp.setINVOICE_CODE(invoiceCode);
							invoiceTmp.setINVOICE_URL(pdfUrl);
							invoiceTmp.setHJBHSJE(totalAmountWithoutTax);
							invoiceTmp.setHJSE(totalTaxAmount);
							invoiceTmp.setKPRQ(kprq);
							//获取明细
							List<Map<String, Object>> detaiList = (ArrayList<Map<String, Object>>) returnMap.get("FPMXXZ_XMXXS");//订单明细信息
							//重新组织明细信息
							List<FPKJXX_XMXXContent> fpmxList = new ArrayList<FPKJXX_XMXXContent>();
							for (Map<String, Object> detailMap: detaiList) {
								FPKJXX_XMXXContent detailMapTemp = new FPKJXX_XMXXContent();
								detailMapTemp.setINVOICE_ID(invoiceTmp.getINVOICE_ID());//订单ID
								detailMapTemp.setXMMC(StringUtil.safeToString(detailMap.get("XMMC")));
								detailMapTemp.setSPBM(StringUtil.safeToString(detailMap.get("SWBM")));
								detailMapTemp.setXMDJ(StringUtil.safeToString(detailMap.get("XMDJ")));
								detailMapTemp.setXMSL(StringUtil.safeToString(detailMap.get("XMSL")));
								detailMapTemp.setXMJE(StringUtil.safeToString(detailMap.get("XMJE")));
								detailMapTemp.setSE(StringUtil.safeToString(detailMap.get("SE")));
								fpmxList.add(detailMapTemp);
							}
							invoiceTmp.setFpmxList(fpmxList);
							isActive = false;
							timer.cancel();
						}
						if (i++ > 20) {
							isActive = false;
							timer.cancel();
						}
					}
				}
				Timer timer = new Timer();
				Task task = new Task(timer);
				timer.schedule(task, new Long(1000), new Long(5000));
				while (task.isActive) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				invoice=invoiceTmp;
			}
			paramMap.put("NOWTIME",StringUtil.getNowTime());
			paramMap.put("ORDER_ID",orderMap.get("ORDER_ID"));
			paramMap.put("INVOICE_STATUS",EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE);
			for (FPKJXX_FPTXXContent invoice : invoiceList) {
				String invoiceNumber = StringUtil.toStringWithEmpty(invoice.getINVOICE_NUMBER());
				paramMap.put("TAXPAYER_ID",invoice.getNSRSBH());
				if (StringUtil.isNotEmpty(invoiceNumber)) {
					updateInvoiceInfo(invoice);
				} else {
					paramMap.put("INVOICE_STATUS",EfapiaoConstant.InvoiceStatus.ERROR_INVOICE);
				}
			}
			manualInvoiceInfoMapper.updateOrderStatus(paramMap);
		}
	}

	/**
	 * 更新发票数据
	 * @param invoiceInfo
	 */
	public void updateInvoiceInfo(FPKJXX_FPTXXContent invoiceInfo) {
		invoiceMapper.updateInvoice(invoiceInfo);
		List<FPKJXX_XMXXContent> fpmxList = invoiceInfo.getFpmxList();
		invoiceMapper.updateInvoiceDetail(fpmxList);
	}

	/**
	 * 保存开票信息
	 * @param invoiceList
	 */
	public void saveInvoice(List<FPKJXX_FPTXXContent> invoiceList) {
		List<FPKJXX_XMXXContent> invoiceDetailList = new ArrayList<FPKJXX_XMXXContent>();
		int i = 0;
		for (FPKJXX_FPTXXContent invoice : invoiceList) {
			invoice.setNOWTIME(StringUtil.getNowTime());
			invoice.setINVOICE_TIME(StringUtil.getNowTime());
			String invoiceId = StringUtil.safeToString(invoice.getINVOICE_ID());
			List<FPKJXX_XMXXContent> fpmxList = invoice.getFpmxList();
			for (FPKJXX_XMXXContent detail : fpmxList) {
				detail.setINVOICE_DETAIL_ID(StringUtil.getUuid32());
				detail.setINVOICE_ID(invoiceId);
				detail.setNOWTIME(StringUtil.getNowTime());
				i++;
			}
			invoiceDetailList.addAll(fpmxList);
		}
		invoiceMapper.saveInvoice(invoiceList);
		invoiceMapper.saveInvoiceDetail(invoiceDetailList);
	}

	/**
	 * 处理发票信息
	 * @param orderMap
	 * @return
	 */
	private FPKJXX_FPTXXContent dealInvoiceContent(Map<String, Object> orderMap) {
		//发票内容
		FPKJXX_FPTXXContent invoiceContent = new FPKJXX_FPTXXContent();
		//发票明细
		List<FPKJXX_XMXXContent> invoiceDetailList = new ArrayList<FPKJXX_XMXXContent>();
		//放入销方和购方的信息
		//发票限额
		invoiceContent.setINVOICE_LIMIT_AMOUNT(StringUtil.safeToString(orderMap.get("INVOICE_LIMIT_AMOUNT")));
		//平台编码
		invoiceContent.setDSPTBM(StringUtil.safeToString(orderMap.get("PLATFORM_CODE")));
		invoiceContent.setREGISTRATION_CODE(StringUtil.safeToString(orderMap.get("REGISTRATION_CODE")));
		invoiceContent.setAUTHORIZATION_CODE(StringUtil.safeToString(orderMap.get("AUTHORIZATION_CODE")));
		//开票方识别号
		invoiceContent.setNSRSBH(StringUtil.safeToString(orderMap.get("TAXPAYER_IDENTIFY_NO")));
		//开票方名称
		invoiceContent.setNSRMC(StringUtil.safeToString(orderMap.get("TAXPAYER_NAME_CN")));
		//销货方识别号
		invoiceContent.setXHF_NSRSBH(StringUtil.safeToString(orderMap.get("TAXPAYER_IDENTIFY_NO")));
		//销货方名称
		invoiceContent.setXHFMC(StringUtil.safeToString(orderMap.get("TAXPAYER_NAME_CN")));
		//销货方地址
		invoiceContent.setXHF_DZ(StringUtil.safeToString(orderMap.get("TAXPAYER_ADDRESS")));
		//销货方电话
		invoiceContent.setXHF_DH(StringUtil.safeToString(orderMap.get("TAXPAYER_PHONE")));
		//销货方银行账号
		invoiceContent.setXHF_YHZH(StringUtil.safeToString(orderMap.get("TAXPAYER_BANK")) + StringUtil.safeToString(orderMap.get("TAXPAYER_ACCOUNT")));
		//购货方名称
		invoiceContent.setGHFMC(StringUtil.safeToString(orderMap.get("PURCHASER_NAME")));
		//购货方纳税人识别号
		invoiceContent.setGHF_NSRSBH(StringUtil.safeToString(orderMap.get("PURCHASER_ID")));
		//购货方纳税人地址
		invoiceContent.setGHF_DZ(StringUtil.safeToString(orderMap.get("PURCHASER_ADDRESS")));
		//购货方纳税人固定电话
		invoiceContent.setGHF_GDDH(StringUtil.safeToString(orderMap.get("PURCHASER_TEL")));
		//购货方手机
		//invoiceContent.setGHF_SJ(StringUtil.safeToString(orderMap.get("PURCHASER_MOBILE")));
		//备注
		invoiceContent.setBZ(StringUtil.safeToString(orderMap.get("REMARK")));
		//购货方邮箱
		invoiceContent.setGHF_EMAIL(StringUtil.safeToString(orderMap.get("PURCHASER_EMAIL")));
		//购货方纳税人银行账号
		invoiceContent.setGHF_YHZH(StringUtil.safeToString(orderMap.get("PURCHASER_BANK_ACCOUNT")));
		//开票员
		invoiceContent.setKPY(StringUtil.safeToString(orderMap.get("ISSUER")));
		//复核人
		invoiceContent.setFHR(StringUtil.safeToString(orderMap.get("FHR")));
		//收款人
		invoiceContent.setSKY(StringUtil.safeToString(orderMap.get("SKY")));
		//订单状态
		invoiceContent.setORDER_ID(StringUtil.safeToString(orderMap.get("ORDER_ID")));
		//开票明细
		List<TransactionItem> transItems = (List<TransactionItem>)orderMap.get("TransactionItems");
		List<TransactionPayment> paymentItems = (List<TransactionPayment>)orderMap.get("TransactionPayments");
		//获取基础数据
		Map<String, Object> baseData = new HashMap<String,Object>();
		baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
		// 获取开票内容(初始化数据)
		Map<String, Object> defaultInvoiceContents = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.INVOICE_CONTENT);
		// 获取支付方式(初始化数据)
		Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
		// 获取货品信息(初始化数据)
		Map<String, Object> defaultGoodsInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GOODS_INFO);
		//获取分组和开票内容之间的关系(初始化数据)
		Map<String, Object> groupInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GROUP_INFO);
		//计算总的开票金额和不可开票金额
		BigDecimal totalAmount = new BigDecimal(0);//总开票金额
		BigDecimal totalDisAmount = new BigDecimal(0);//折扣金额
		/** 计算可开票总金额和不可开票总金额 **/
		for (TransactionPayment transactionPayment : paymentItems) {
			String paymentCode = transactionPayment.getPAYMENT_CODE();
			BigDecimal curAmount = new BigDecimal(transactionPayment.getPAYMENT_AMOUNT());
			totalAmount = totalAmount.add(curAmount);
			if(!(defaultPayments.containsKey(paymentCode)&&EfapiaoConstant.DefaultKey.TRUE.equals(((Map<String,Object>)defaultPayments.get(paymentCode)).get("CAN_INVOICE")))){
				totalDisAmount = totalDisAmount.add(curAmount);
			}
		}
		BigDecimal disRate = totalDisAmount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP);
		String taxpayerType = StringUtil.safeToString(orderMap.get("TAXPAYER_TYPE"));
		/** 组织明细信息，确认可开票金额和不可开票金额 **/
		BigDecimal disAmountUsed = new BigDecimal(0);//折扣金额
		for(int i = 0 ; i<transItems.size();i++) {
			TransactionItem transactionItem = transItems.get(i);
			//判断商品是否可以开票
			if (defaultGoodsInfos.containsKey(transactionItem.getITEM_CODE())){
				Map<String, Object> defaultGoodsInfo = (Map<String, Object>) defaultGoodsInfos.get(transactionItem.getITEM_CODE());
				//商品金额
				BigDecimal itemAmount = new BigDecimal(ShinHoDataUtil.objectToStr(transactionItem.getITEM_AMOUNT_AFTER_DISCOUNT()));
	        	//单品金额需要大于0才可放入开票内容
	        	if (itemAmount.compareTo(BigDecimal.ZERO) != 1){
	        		continue;
	        	}
				//获取对应商品编码(判断货品上是否直接绑定开票内容 如果未绑定 则通过分类关系获取开票内容)
				String contentId = StringUtil.safeToString(defaultGoodsInfo.get("CONTENT_ID"));
				if (StringUtil.isEmpty(contentId)) {
					String groupId = StringUtil.safeToString(defaultGoodsInfo.get("GROUP_ID"));
					//通过groupId获取contentId
					Map<String, Object> groupInfo = (Map<String, Object>) groupInfos.get(groupId);
					contentId = StringUtil.safeToString(groupInfo.get("CONTENT_ID"));
				}
				//开票内容
				Map<String, Object> defaultContent = (Map<String, Object>) defaultInvoiceContents.get(contentId);
				//商品编码
				String commodityCode = StringUtil.safeToString(defaultContent.get("CONTENT_CODE"));
				//判断是否小额纳税人
				Double taxRatio = new Double(0);
				if ("E00201".equals(taxpayerType)) {//普通纳税人
					taxRatio = (Double) defaultContent.get("TAXRATE");
				} else {//小额纳税人
					taxRatio = (Double) defaultContent.get("SMALL_TAXRATE");
				}
				//开票内容
				String itemTitle = StringUtil.safeToString(defaultContent.get("CONTENT_NAME_CN"));
				if(EfapiaoConstant.InvoiceType.INVOICE_DETAIL.equals(orderMap.get("INVOICE_DETAIL_TYPE"))){
					itemTitle = (String) defaultGoodsInfo.get("GOODS_NAME_CN");
				}
				//重量
                String orderWeight=ShinHoDataUtil.objectToStr(transactionItem.getORDER_WEIGHT());
                BigDecimal itemOrderWeight = new BigDecimal(ShinHoDataUtil.objectToStr(transactionItem.getORDER_WEIGHT()));
                //数量
                String itemItemQuantity = ShinHoDataUtil.objectToStr(transactionItem.getITEM_QUANTITY());
                BigDecimal quantity = new BigDecimal(itemItemQuantity);
                //单位
                String UNIT_CN=ShinHoDataUtil.objectToStr(transactionItem.getUNIT_CN());
                //非折扣行
                FPKJXX_XMXXContent itemDetail = new FPKJXX_XMXXContent();
                itemDetail.setXMMC(itemTitle);//项目名称
                itemDetail.setHSBZ("1");//含税标志
                int r = disRate.compareTo(BigDecimal.ZERO);//存在不可开票的支付方式时，增加折扣行
                if (r == 1) {
                	if(i != transItems.size() -1) {
	                	BigDecimal curDisAmount = itemAmount.multiply(disRate);
						BigDecimal num = new BigDecimal("0.01");																			
						  if(curDisAmount.compareTo(num)==-1){
							  itemDetail.setFPHXZ("0");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
							  r=0;
						  }else{
	                	      itemDetail.setFPHXZ("2");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
						  }
	                	}else{
	                		r=1;
	                		itemDetail.setFPHXZ("2");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
	                		}
                	} else {
                	itemDetail.setFPHXZ("0");//发票行性质 （是否折扣行）
                }
                int weight = itemOrderWeight.compareTo(BigDecimal.ZERO);  //根据重量判断数量
                if(weight==0){
               	 BigDecimal danjia =itemAmount.divide(quantity,2,BigDecimal.ROUND_HALF_UP);  //单价
                    String UnitPrice = ShinHoDataUtil.objectToStr(danjia);
               	 itemDetail.setXMSL(itemItemQuantity);//项目数量                		 
               	 itemDetail.setXMDJ(UnitPrice);//项目单价      
                }else{
               	 itemDetail.setXMSL("1");//项目数量
               	 itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(itemAmount));//项目单价   
               	 itemDetail.setGGXH(orderWeight);//规格型号
                }
                itemDetail.setSPBM(commodityCode);//商品编码
                itemDetail.setXMJE(ShinHoDataUtil.objectToStr(itemAmount));//项目金额
                itemDetail.setSL(ShinHoDataUtil.objectToStr(taxRatio));//税率
				if (taxRatio < 0) {
					// 不征税项目
					itemDetail.setSL("0");
					itemDetail.setYHZCBS("1");
					itemDetail.setLSLBS("2");// 非零税率
					itemDetail.setZZSTSGL("不征税");// 优惠政策标识
				} else if (taxRatio > 0) {
					itemDetail.setLSLBS("");// 非零税率
				} else {
					itemDetail.setLSLBS("3");// 非零税率
				}
				invoiceDetailList.add(itemDetail);
				//折扣行
				if (r == 1) {
					itemDetail = new FPKJXX_XMXXContent();
					itemDetail.setXMMC(itemTitle);//项目名称
					itemDetail.setHSBZ("1");//含税标志
					itemDetail.setFPHXZ("1");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
					itemDetail.setXMSL("-1");//项目数量
					//当行数为最后一行的时候，折扣直接减掉之前已经折扣的
					if(i == transItems.size() -1 ){
						BigDecimal curDisAmount = totalDisAmount.add(disAmountUsed.multiply(new BigDecimal(-1)));
						itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(curDisAmount));//项目单价
						itemDetail.setXMJE(ShinHoDataUtil.objectToStr(curDisAmount.multiply(new BigDecimal(-1))));//项目金额
					}else{						
						BigDecimal curDisAmount = itemAmount.multiply(disRate);						   				
						disAmountUsed=disAmountUsed.add(curDisAmount);
						itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(curDisAmount));//项目单价
						itemDetail.setXMJE(ShinHoDataUtil.objectToStr(curDisAmount.multiply(new BigDecimal(-1))));//项目金额
						  
						}
					itemDetail.setSPBM(commodityCode);//商品编码
					itemDetail.setSL(ShinHoDataUtil.objectToStr(taxRatio));//税率
					if (taxRatio < 0) {
						// 不征税项目
						itemDetail.setSL("0");
						itemDetail.setYHZCBS("1");
						itemDetail.setLSLBS("2");// 非零税率
						itemDetail.setZZSTSGL("不征税");// 优惠政策标识
					} else if (taxRatio > 0) {
						itemDetail.setLSLBS("");// 非零税率
					} else {
						itemDetail.setLSLBS("3");// 非零税率
					}
					invoiceDetailList.add(itemDetail);
				}
			}
		}
		//处理开票
		invoiceContent.setFpmxList(invoiceDetailList);
		//开票大类
		invoiceContent.setKPXM(invoiceDetailList.get(0).getXMMC());
		return invoiceContent;
	}

	/**
	 * 抓取订单交易信息
	 * @param pickUpCode
	 * @return
	 */
	private Transaction getTransactionData(String pickUpCode,CacheClient cacheClient) {
		Transaction transactionData = null;
		try {
			transactionData = (Transaction) cacheClient.get(pickUpCode);
			if (transactionData == null) {
				// 从数据库重新获取
				transactionData = queryTransDataFromDB(pickUpCode);
				if (transactionData!=null) {
					cacheClient.save(pickUpCode, transactionData);
				}
			}
		} catch (Exception e) {
			logger.error("database connect failed");
		}
		return transactionData;
	}

	/**
	 * 去数据库查询对应的交易信息
	 * @param pickUpCode
	 * @return
	 */
	private Transaction queryTransDataFromDB(String pickUpCode) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("TRANSACTIONEQUE", pickUpCode);
		Transaction orderInfo = requestRemoteMapper.getTransData(params);// 根据提取码查询订单主数据
		if (orderInfo != null) {
			//如果开票状态为空，则认为是未开票
			if(StringUtil.isEmpty(orderInfo.getINVOICE_STATUS())){
				orderInfo.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.NO_INVOICE);
			}
			params.put("TRANSACTION_ID", orderInfo.getTRANSACTION_ID());
			List<TransactionItem> salesItemList = requestRemoteMapper.getSalesItems(params);// 商品信息
			List<TransactionPayment> paymentList = requestRemoteMapper.getPayments(params);// 支付记录
			// 将商品和支付信息放入order对象
			orderInfo.setTransactionItemList(salesItemList);
			orderInfo.setTransactionPaymentList(paymentList);
		}
		return orderInfo;
	}

	/**
	 * 保存对应的主档信息
	 * @param
	 */
	public void saveManualInvoice(Map<String, String> dataMap,String userId) {
		Map<String,Object> orderMap = new HashMap<String,Object>();
		Map<String, String> salesMap = JSONUtil.json2Map(dataMap.get("sales"));
		Map<String, String> buyerMap = JSONUtil.json2Map(dataMap.get("buyer"));

		//保存订单
		orderMap.put("ORDER_ID", dataMap.get("orderId"));
		orderMap.put("STATUS", "DV0101");//未审核
		orderMap.put("INVOICE_STATUS", EfapiaoConstant.InvoiceStatus.NO_INVOICE);//未开票
		orderMap.put("INVOICE_TYPE", EfapiaoConstant.InvoiceType.DEFAULT);//蓝票
		orderMap.put("IS_DELETE", "0");
		orderMap.put("IS_MANUAL", EfapiaoConstant.DefaultKey.TRUE);//是否手工开票
		orderMap.put("ADD_USER", userId);
		orderMap.put("ADD_TIME", StringUtil.getNowTime());
		orderMap.put("UPDATE_USER", userId);
		orderMap.put("UPDATE_TIME", StringUtil.getNowTime());
		orderMap.put("REMARK", salesMap.get("remark"));
		orderMap.put("PURCHASER_NAME", objectToStr(buyerMap.get("buyername")));
		orderMap.put("PURCHASER_EMAIL", buyerMap.get("buyermail"));
		orderMap.put("PURCHASER_ID", objectToStr(buyerMap.get("buyernum")));
		orderMap.put("PURCHASER_MOBILE", objectToStr(buyerMap.get("buyerphone")));
		orderMap.put("PURCHASER_ADDRESS", objectToStr(buyerMap.get("buyeraddress")));
		orderMap.put("PURCHASER_BANK_ACCOUNT", objectToStr(buyerMap.get("buyerbank")));
		//调整为纳税人识别号
		orderMap.put("TAXPAYER_ID", salesMap.get("salesnum"));
		orderMap.put("INVOICE_DETAIL_TYPE",objectToStr(buyerMap.get("invoicetype")));
		manualInvoiceInfoMapper.saveOrder(orderMap);
		//保存订单和交易流水号的关联关系
		List<Map<String,Object>> orderRelations = null;
		Map<String,Object> orderRelationMap = null;
		String orderId = dataMap.get("orderId");
		CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
		try{
			Map<String,Map<String,Object>> order_x_trans = (Map<String, Map<String,Object>>) cacheClient.get(orderId);
			if(order_x_trans != null){
				orderRelations = new ArrayList<Map<String, Object>>();
				Map<String,Object> tranMap = order_x_trans.get(orderId);
				List<Map<String,Object>> transList = (ArrayList<Map<String,Object>>)tranMap.get("transList");
				if(transList != null && transList.size()>0){
					for(Map<String,Object> tran:transList){
						orderRelationMap = new HashMap<String,Object>();
						orderRelationMap.put("ID",StringUtil.getUuid32());
						orderRelationMap.put("ORDER_ID",orderId);
						orderRelationMap.put("TRANSACTION_NUMBER",tran.get("transactionnum"));
						orderRelationMap.put("ADD_USER",userId);
						orderRelationMap.put("ADD_TIME",StringUtil.getNowTime());
						orderRelationMap.put("UPDATE_USER",userId);
						orderRelationMap.put("UPDATE_TIME",StringUtil.getNowTime());
						orderRelationMap.put("IS_DELETE","0");
						orderRelations.add(orderRelationMap);
					}
					manualInvoiceInfoMapper.saveRelation(orderRelations);
				}
			}
		}
		catch (MemcachedException e){
			e.printStackTrace();
		}

	}

	public void updateOrderStatus(Map<String, Object> params) {
		params.put("NOWTIME",StringUtil.getNowTime());
		manualInvoiceInfoMapper.updateOrderStatus(params);
	}

	private FPKJXX_FPTXXContent dealManualInvoiceContent(Map<String, Object> orderMap) {
		//发票内容
		FPKJXX_FPTXXContent invoiceContent = new FPKJXX_FPTXXContent();
		//发票明细
		List<FPKJXX_XMXXContent> invoiceDetailList = new ArrayList<FPKJXX_XMXXContent>();
		//放入销方和购方的信息
		//发票限额
		invoiceContent.setINVOICE_LIMIT_AMOUNT(StringUtil.safeToString(orderMap.get("INVOICE_LIMIT_AMOUNT")));
		//平台编码
		invoiceContent.setDSPTBM(StringUtil.safeToString(orderMap.get("PLATFORM_CODE")));
		invoiceContent.setREGISTRATION_CODE(StringUtil.safeToString(orderMap.get("REGISTRATION_CODE")));
		invoiceContent.setAUTHORIZATION_CODE(StringUtil.safeToString(orderMap.get("AUTHORIZATION_CODE")));
		//开票方识别号
		invoiceContent.setNSRSBH(StringUtil.safeToString(orderMap.get("TAXPAYER_IDENTIFY_NO")));
		//开票方名称
		invoiceContent.setNSRMC(StringUtil.safeToString(orderMap.get("TAXPAYER_NAME_CN")));
		//销货方识别号
		invoiceContent.setXHF_NSRSBH(StringUtil.safeToString(orderMap.get("TAXPAYER_IDENTIFY_NO")));
		//销货方名称
		invoiceContent.setXHFMC(StringUtil.safeToString(orderMap.get("TAXPAYER_NAME_CN")));
		//销货方地址
		invoiceContent.setXHF_DZ(StringUtil.safeToString(orderMap.get("TAXPAYER_ADDRESS")));
		//销货方电话
		invoiceContent.setXHF_DH(StringUtil.safeToString(orderMap.get("TAXPAYER_PHONE")));
		//销货方银行账号
		invoiceContent.setXHF_YHZH(StringUtil.safeToString(orderMap.get("TAXPAYER_BANK")) + StringUtil.safeToString(orderMap.get("TAXPAYER_ACCOUNT")));
		//购货方名称
		invoiceContent.setGHFMC(StringUtil.safeToString(orderMap.get("PURCHASER_NAME")));
		//购货方纳税人识别号
		invoiceContent.setGHF_NSRSBH(StringUtil.safeToString(orderMap.get("PURCHASER_ID")));
		//购货方纳税人地址
		invoiceContent.setGHF_DZ(StringUtil.safeToString(orderMap.get("PURCHASER_ADDRESS")));
		//购货方纳税人固定电话
		invoiceContent.setGHF_GDDH(StringUtil.safeToString(orderMap.get("PURCHASER_TEL")));
		//购货方手机
		invoiceContent.setGHF_SJ(StringUtil.safeToString(orderMap.get("PURCHASER_MOBILE")));
		//购货方邮箱
		invoiceContent.setGHF_EMAIL(StringUtil.safeToString(orderMap.get("PURCHASER_EMAIL")));
		//购货方纳税人银行账号
		invoiceContent.setGHF_YHZH(StringUtil.safeToString(orderMap.get("PURCHASER_BANK_ACCOUNT")));
		//备注
		invoiceContent.setBZ(StringUtil.safeToString(orderMap.get("REMARK")));
		//手工开票开票员是从t_invoice_info_handler表里取的
		invoiceContent.setKPY(StringUtil.safeToString(orderMap.get("ISSUER")));
		//复核人
		invoiceContent.setFHR(StringUtil.safeToString(orderMap.get("REVIEW_CLERK")));
		//收款人
		invoiceContent.setSKY(StringUtil.safeToString(orderMap.get("PAYEE")));
		//订单状态
		invoiceContent.setORDER_ID(StringUtil.safeToString(orderMap.get("ORDER_ID")));
		//开票明细
		List<Map<String, Object>> invoiceDetails = (List<Map<String, Object>>)orderMap.get("invoiceDetails");
		//获取基础数据
		Map<String, Object> baseData = new HashMap<String,Object>();
		baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
		// 获取开票内容(初始化数据)
		Map<String, Object> defaultInvoiceContents = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.INVOICE_CONTENT);
		// 获取支付方式(初始化数据)
		Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
		// 获取货品信息(初始化数据)
		Map<String, Object> defaultGoodsInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GOODS_INFO);
		//获取分组和开票内容之间的关系(初始化数据)
		Map<String, Object> groupInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GROUP_INFO);
		//计算总的开票金额和不可开票金额
		BigDecimal totalAmount = new BigDecimal(0);//总开票金额
		BigDecimal totalDisAmount = new BigDecimal(0);//折扣金额
		/** 计算可开票总金额和不可开票总金额 **/
		/** 组织明细信息，确认可开票金额和不可开票金额 **/
		LinkedHashMap<String,Object> invoiceDetailMap = new LinkedHashMap<String,Object>();
		for (int i=0;i<invoiceDetails.size();i++) {
			Map<String, Object> invoiceDetail = invoiceDetails.get(i);
			//商品金额
			BigDecimal itemAmount = new BigDecimal(StringUtil.safeToString(invoiceDetail.get("ITEM_AMOUNT")));
        	//单品金额需要大于0才可放入开票内容
        	if (itemAmount.compareTo(BigDecimal.ZERO) == 1){
        		//纳税人类型
        		String taxpayerType = StringUtil.safeToString(orderMap.get("TAXPAYER_TYPE"));
        		//商品编码
        		String commodityCode = StringUtil.safeToString(invoiceDetail.get("COMMODITY_CODE"));
        		//根据商品编码获取t_invoice_content
        		Map<String, Object> defaultContent = manualInvoiceInfoMapper.getInvoiceContentByCode(commodityCode);
        		//判断是否小额纳税人
        		Double taxRatio = new Double(0);
        		if ("E00201".equals(taxpayerType)) {//普通纳税人
        			taxRatio = Double.parseDouble(defaultContent.get("TAXRATE")+"");
        		} else {//小额纳税人
        			taxRatio = Double.parseDouble(defaultContent.get("SMALL_TAXRATE")+"");
        		}
        		//开票内容
        		String itemTitle = StringUtil.safeToString(invoiceDetail.get("ITEM_TITLE"));
        		//折扣金额
        		BigDecimal disAmount = new BigDecimal(0);
        		String discount = StringUtil.safeToString(invoiceDetail.get("DISCOUNT_AMOUNT"));
        		if(StringUtil.isNotEmpty(discount)){
        			disAmount = new BigDecimal(StringUtil.safeToString(invoiceDetail.get("DISCOUNT_AMOUNT")));
        			
        		}
        		FPKJXX_XMXXContent itemDetail = new FPKJXX_XMXXContent();
        		itemDetail.setXMMC(itemTitle);//项目名称
        		itemDetail.setHSBZ("1");//含税标志
        		//非折扣行
        		int r = disAmount.compareTo(BigDecimal.ZERO);
        		if (r == 1) {
        			itemDetail.setFPHXZ("2");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
        		} else {
        			itemDetail.setFPHXZ("0");//发票行性质 （是否折扣行）
        		}
        		itemDetail.setXMSL("1");//项目数量
        		itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(itemAmount));//项目单价
        		itemDetail.setSPBM(commodityCode);//商品编码
        		itemDetail.setXMJE(ShinHoDataUtil.objectToStr(itemAmount));//项目金额
        		itemDetail.setSL(ShinHoDataUtil.objectToStr(taxRatio));//税率
        		
        		if (taxRatio < 0) {
        			// 不征税项目
        			itemDetail.setSL("0");
        			itemDetail.setYHZCBS("1");
        			itemDetail.setLSLBS("2");// 非零税率
        			itemDetail.setZZSTSGL("不征税");// 优惠政策标识
        		} else if (taxRatio > 0) {
        			itemDetail.setLSLBS("");// 非零税率
        		} else {
        			itemDetail.setLSLBS("3");// 非零税率
        		}
        		//如果存在该条明细信息，则将此次计算的结果进行汇总
        		if(invoiceDetailMap.containsKey(itemTitle+commodityCode)){
        			FPKJXX_XMXXContent itemDetailPre = (FPKJXX_XMXXContent)invoiceDetailMap.get(itemTitle+commodityCode);
        			//项目单价
        			BigDecimal itemUnitAmountPrev = new BigDecimal(itemDetailPre.getXMDJ());
        			//项目金额
        			BigDecimal itemAmountPrev =  new BigDecimal(itemDetailPre.getXMJE());
        			
        			itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(itemUnitAmountPrev.add(itemAmount)));
        			itemDetail.setXMJE(ShinHoDataUtil.objectToStr(itemAmountPrev.add(itemAmount)));
        			//折扣行存在则为折扣
        			String fphxzPre = itemDetailPre.getFPHXZ();
        			String fphxz = itemDetail.getFPHXZ();
        			if("2".equals(fphxzPre)||"2".equals(fphxz)){
        				itemDetail.setFPHXZ("2");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
        			}
        			//将调整后的对象再set到map中去
        			invoiceDetailMap.put(itemTitle+commodityCode, itemDetail);
        		}else{
        			//没有则新增
        			invoiceDetailMap.put(itemTitle+commodityCode, itemDetail);
        		}
        		
        		//折扣行
        		if (r == 1) {
        			itemDetail = new FPKJXX_XMXXContent();
        			itemDetail.setXMMC(itemTitle);//项目名称
        			itemDetail.setHSBZ("1");//含税标志
        			itemDetail.setFPHXZ("1");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
        			itemDetail.setXMSL("-1");//项目数量
        			itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(disAmount));//项目单价
        			itemDetail.setXMJE(ShinHoDataUtil.objectToStr(disAmount.multiply(new BigDecimal(-1))));//项目金额
        			itemDetail.setSPBM(commodityCode);//商品编码
        			itemDetail.setSL(ShinHoDataUtil.objectToStr(taxRatio));//税率
        			if (taxRatio < 0) {
        				// 不征税项目
        				itemDetail.setSL("0");
        				itemDetail.setYHZCBS("1");
        				itemDetail.setLSLBS("2");// 非零税率
        				itemDetail.setZZSTSGL("不征税");// 优惠政策标识
        			} else if (taxRatio > 0) {
        				itemDetail.setLSLBS("");// 非零税率
        			} else {
        				itemDetail.setLSLBS("3");// 非零税率
        			}
        			if(invoiceDetailMap.containsKey(itemTitle+commodityCode+"zhekou")){
        				FPKJXX_XMXXContent itemDetailPre = (FPKJXX_XMXXContent)invoiceDetailMap.get(itemTitle+commodityCode+"zhekou");
        				//项目单价
        				BigDecimal itemUnitAmountPrev = new BigDecimal(itemDetailPre.getXMDJ());
        				BigDecimal itemUnitAmountNow = new BigDecimal(itemDetail.getXMDJ());
        				//项目金额
        				BigDecimal itemAmountPrev = new BigDecimal(itemDetailPre.getXMJE());
        				BigDecimal itemAmountNow = new BigDecimal(itemDetail.getXMJE());
        				
        				itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(itemUnitAmountPrev.add(itemUnitAmountNow)));
        				itemDetail.setXMJE(ShinHoDataUtil.objectToStr(itemAmountPrev.add(itemAmountNow)));
        				invoiceDetailMap.put(itemTitle+commodityCode+"zhekou", itemDetail);
        			}else{
        				invoiceDetailMap.put(itemTitle+commodityCode+"zhekou", itemDetail);
        			}
        		}
        	}
		}
		for (Map.Entry<String, Object> entry : invoiceDetailMap.entrySet()) {
			invoiceDetailList.add((FPKJXX_XMXXContent) entry.getValue());
		}
		//处理开票
		invoiceContent.setFpmxList(invoiceDetailList);
		//开票大类
		invoiceContent.setKPXM(invoiceDetailList.get(0).getXMMC());
		return invoiceContent;
	}


	/**
	 * 导入开票信息
	 * @param path
	 * @param fileName
	 * @param userId
	 * @return
	 * @throws java.io.IOException
	 * @throws java.text.ParseException
	 */
	public String readExcel(String path, String fileName, String userId) throws IOException, ParseException {
		Workbook wb;
		Sheet sheet;
		Row row;
		InputStream is = null;
		StringBuffer resultBuffer = new StringBuffer();
		try {
			is = new FileInputStream(path);
			if (fileName.endsWith("xls")) {
				wb = new HSSFWorkbook(is);
			} else if (fileName.endsWith("xlsx")) {
				wb = new XSSFWorkbook(is);
			} else {
				resultBuffer.append("读取的文件不是excel文件，请导入正确的文件格式");
				return resultBuffer.toString();
			}
			// 得到总行数,不大于1的时候无数据要导入
			sheet = wb.getSheetAt(0);
			int rowNum = sheet.getLastRowNum();
			if (rowNum < 1) {
				resultBuffer.append("excel中没有要导入的数据");
			} else {
				//订单单条Map<订单编号，订单>
				Map<String, Map<String,String>> orderMap = new HashMap<String, Map<String,String>>();
				Map<String,String> order = null;

				//发票主信息Map<订单编号，发票主信息>
				Map<String, Map<String,String>> invoiceInfoMap = new HashMap<String, Map<String,String>>();
				Map<String,String> invoiceInfo = null;

				//发票明细信息Map<订单编号，发票明细信息集合>
				Map<String,List<Map<String,String>>> invoiceDetailMap = new HashMap<String,List<Map<String,String>>>();
				List<Map<String,String>> invoiceDetailList = new ArrayList<Map<String,String>>();
				Map<String,String> invoiceDetail = null;

				//遍历EXCEL中记录，如果数据库中包含记录则做更新，如果不包含则做新增（根据发票代码）
				String  contentCode = null;
				for (int i = 1; i <= rowNum; i++) {
					row = sheet.getRow(i);
					//订单编号
					Cell cell0 = row.getCell(0);
					//客户名称
					Cell cell1 = row.getCell(1);
					//客户邮箱
					Cell cell2 = row.getCell(2);
					//客户税号
					Cell cell3 = row.getCell(3);
					//客户地址
					Cell cell4 = row.getCell(4);
					//客户电话
					Cell cell5 = row.getCell(5);
					//开户行
					Cell cell6 = row.getCell(6);
					//开户行帐号
					Cell cell7 = row.getCell(7);
					//销方税号
					Cell cell8 = row.getCell(8);
					//销方名称
					Cell cell9 = row.getCell(9);
					//开票人
					Cell cell10 = row.getCell(10);
					//收款人
					Cell cell11 = row.getCell(11);
					//复核人
					Cell cell12 = row.getCell(12);
					//开票内容
					Cell cell13 = row.getCell(13);
					//商品编码
					Cell cell14 = row.getCell(14);
					//金额
					Cell cell15 = row.getCell(15);
					//折扣金额
					Cell cell16 = row.getCell(16);

					//订单编号 客户名称 客户邮箱 销方税号 销方名称 开票人 收款人 复核人	 开票内容 商品编码 金额 折扣金额不能为空
					if (StringUtil.isEmpty(cell0)
							|| StringUtil.isEmpty(cell1)
							|| StringUtil.isEmpty(cell2)
							|| StringUtil.isEmpty(cell8)
							|| StringUtil.isEmpty(cell9)
							|| StringUtil.isEmpty(cell10)
							|| StringUtil.isEmpty(cell11)
							|| StringUtil.isEmpty(cell12)
							|| StringUtil.isEmpty(cell13)
							|| StringUtil.isEmpty(cell14)
							|| StringUtil.isEmpty(cell15)
							|| StringUtil.isEmpty(cell16)
							) {
						resultBuffer.append("第" + i + "行信息不完全；\r\n");
						continue;
					}
					//如果发现excel中有不完整信息，都查找出来给予用户提示
					if(StringUtil.isNotEmpty(resultBuffer.toString())){
						continue;
					}

					cell0.setCellType(Cell.CELL_TYPE_STRING);
					cell1.setCellType(Cell.CELL_TYPE_STRING);
					cell2.setCellType(Cell.CELL_TYPE_STRING);
					if(cell3 != null) {
						cell3.setCellType(Cell.CELL_TYPE_STRING);
					}
					if(cell4 != null) {
						cell4.setCellType(Cell.CELL_TYPE_STRING);
					}
					if(cell5 != null) {
						cell5.setCellType(Cell.CELL_TYPE_STRING);
					}
					if(cell6 != null) {
						cell6.setCellType(Cell.CELL_TYPE_STRING);
					}
					if(cell7 != null) {
						cell7.setCellType(Cell.CELL_TYPE_STRING);
					}
					cell8.setCellType(Cell.CELL_TYPE_STRING);
					cell9.setCellType(Cell.CELL_TYPE_STRING);
					cell10.setCellType(Cell.CELL_TYPE_STRING);
					cell11.setCellType(Cell.CELL_TYPE_STRING);
					cell12.setCellType(Cell.CELL_TYPE_STRING);
					cell13.setCellType(Cell.CELL_TYPE_STRING);
					cell14.setCellType(Cell.CELL_TYPE_STRING);
					cell15.setCellType(Cell.CELL_TYPE_STRING);
					cell16.setCellType(Cell.CELL_TYPE_STRING);

					/**
					 * 如果行信息完整，则进行如下判断
					 * 1.订单编号在t_invoice_order表里是否存在
					 * 2.字段长度校验
					 * 3.销方税号检验（1.检验税号是否存在 2.如果税号存在，检验平台编码，注册码，授权码是否不为空）
					 * 4.商品编码在t_invoice_content表里是否存在
					 * 5.金额、折扣金额大于0
					 */
					String orderId = cell0.getStringCellValue();
					//客户名称
					String purchaserName = cell1.getStringCellValue();
					//客户邮箱
					String purchaserEmail = cell2.getStringCellValue();
					//客户税号
					String purchaserId = "";
					if(cell3 != null) {
						purchaserId = cell3.getStringCellValue();
					}
					//客户地址
					String purchaserAddress = "";
					if(cell4 != null) {
						purchaserAddress = cell4.getStringCellValue();
					}
					//客户电话
					String purchaserMobile = "";
					if(cell5 != null) {
						purchaserMobile = cell5.getStringCellValue();
					}
					//开户行
					String purchaserBank = "";
					if(cell6 != null) {
						purchaserBank = cell6.getStringCellValue();
					}
					//开户行帐号
					String purchaserAccount = "";
					if(cell6 != null) {
						purchaserAccount = cell7.getStringCellValue();
					}
					//销方税号
					String taxpayerId = cell8.getStringCellValue();
					//销方名称
					String taxpayerNameCn = cell9.getStringCellValue();
					//开票人
					String issuer = cell10.getStringCellValue();
					//收款人
					String payee = cell11.getStringCellValue();
					//复核人
					String reviewClerk = cell12.getStringCellValue();
					//开票内容
					String itemTitle = cell13.getStringCellValue();
					//商品编码
					String commodityCode = cell14.getStringCellValue();
					//金额
					String itemAmount = cell15.getStringCellValue();
					//折扣金额
					String discountAmount = cell16.getStringCellValue();

					//订单编号在t_invoice_order表里是否存在
					Map<String,Object> map = manualInvoiceInfoMapper.getOrderById(orderId);
					if(CollectionUtil.isNotEmpty(map)){
						resultBuffer.append("第" + i + "行订单编号"+orderId+"已经存在；"+"\r\n");
						continue;
					}

					//字段长度校验
					if(StringLength(orderId)>32){
						resultBuffer.append("第" + i + "行订单编号长度不能大于32；"+"\r\n");
					}
					if(StringLength(purchaserName)>200){
						resultBuffer.append("第" + i + "行客户名称长度不能大于200；"+"\r\n");
					}
					if(StringLength(purchaserEmail)>50){
						resultBuffer.append("第" + i + "行邮箱长度不能大于50；"+"\r\n");
					}
					if(StringLength(purchaserId)>20){
						resultBuffer.append("第" + i + "行客户识别号长度不能大于20；"+"\r\n");
					}
					if(StringLength(purchaserAddress)>200){
						resultBuffer.append("第" + i + "行客户地址长度不能大于200；"+"\r\n");
					}
					if(StringLength(purchaserMobile)>20){
						resultBuffer.append("第" + i + "行客户电话长度不能大于20；"+"\r\n");
					}
					if((StringLength(purchaserBank)+StringLength(purchaserAccount)+1)>200){
						resultBuffer.append("第" + i + "行客户开户行和开户行总长度不能大于200；"+"\r\n");
					}
					if(StringLength(taxpayerId)>20){
						resultBuffer.append("第" + i + "行销方识别号长度不能大于20；"+"\r\n");
					}
					if(StringLength(taxpayerNameCn)>200){
						resultBuffer.append("第" + i + "行销方名称长度不能大于200；"+"\r\n");
					}
					if(StringLength(issuer)>8){
						resultBuffer.append("第" + i + "行开票人长度不能大于8；"+"\r\n");
					}
					if(StringLength(payee)>8){
						resultBuffer.append("第" + i + "行收款人长度不能大于8；"+"\r\n");
					}
					if(StringLength(reviewClerk)>8){
						resultBuffer.append("第" + i + "行复核人长度不能大于8；"+"\r\n");
					}
					if(StringLength(itemTitle)>200){
						resultBuffer.append("第" + i + "行开票内容长度不能大于200；"+"\r\n");
					}
					if(StringLength(commodityCode)>19){
						resultBuffer.append("第" + i + "行商品编码不能大于19；"+"\r\n");
					}
					if(StringLength(itemAmount)>20){
						resultBuffer.append("第" + i + "行金额长度不能大于20；"+"\r\n");
					}
					if(StringLength(discountAmount)>20){
						resultBuffer.append("第" + i + "行折扣金额长度不能大于20；"+"\r\n");
					}

					//销方税号检验（1.检验税号是否存在 2.如果税号存在，检验平台编码，注册码，权限码是否不为空）
					Map<String,String> taxpayerMap = taxPayerMapper.getTaxpayerByNsrsbh(taxpayerId);
					if(CollectionUtil.isEmpty(taxpayerMap)){
						resultBuffer.append("第" + i + "行销方税号"+taxpayerId+"在系统里不存在；"+"\r\n");
					}else{
						String platformCode = taxpayerMap.get("PLATFORM_CODE");
						String registrationCode = taxpayerMap.get("REGISTRATION_CODE");
						String authorizationCode = taxpayerMap.get("AUTHORIZATION_CODE");
						if(StringUtil.isEmpty(platformCode)
								|| StringUtil.isEmpty(registrationCode)
								|| StringUtil.isEmpty(authorizationCode)){
							resultBuffer.append("第" + i + "行销方税号"+taxpayerId+"平台码或注册码或授权码信息不完整；"+"\r\n");
						}
					}

					//商品编码在t_invoice_content表里是否存在
					Map<String,String> contentMap = invoiceContentMapper.getContentByCode(commodityCode);
					if(CollectionUtil.isEmpty(contentMap)){
						resultBuffer.append("第" + i + "行商品编码"+commodityCode+"在系统里不存在；"+"\r\n");
					}

					//金额、折扣金额大于0
					BigDecimal itemAmountDecimal = new BigDecimal(itemAmount);
					BigDecimal discountAmountDecimal = new BigDecimal(discountAmount);
					BigDecimal zeroDecimal = new BigDecimal(0);
					if(itemAmountDecimal.compareTo(zeroDecimal)<=0){
						resultBuffer.append("第" + i + "行金额不大于0；"+"\r\n");
					}
					if(discountAmountDecimal.compareTo(zeroDecimal)<0){
						resultBuffer.append("第" + i + "行折扣金额不大于0；"+"\r\n");
					}

					if(StringUtil.isNotEmpty(resultBuffer.toString())){
						continue;
					}

					//如果校验通过，则生成订单，发票主信息，发票明细信息
					//生成订单
					//如果订单不存在，则存
					if(orderMap.get(orderId) == null) {
						order = new HashMap<String,String>();
						order.put("ORDER_ID",orderId);
						order.put("INVOICE_STATUS","E00501");
						order.put("INVOICE_TYPE","E00601");
						order.put("IS_MANUAL","E00102");
						order.put("PURCHASER_NAME",cell1.getStringCellValue());
						order.put("PURCHASER_EMAIL",cell2.getStringCellValue());
						order.put("PURCHASER_ID",purchaserId);
						order.put("PURCHASER_MOBILE",purchaserMobile);
						order.put("PURCHASER_ADDRESS",purchaserAddress);
						order.put("PURCHASER_BANK_ACCOUNT",purchaserBank+" "+purchaserAccount);
						order.put("ADD_USER",userId);
						order.put("ADD_TIME",StringUtil.getNowTime());
						order.put("UPDATE_USER",userId);
						order.put("UPDATE_TIME",StringUtil.getNowTime());
						order.put("IS_DELETE","0");
						order.put("TAXPAYER_ID",cell8.getStringCellValue());
						order.put("INVOICE_DETAIL_TYPE","E00903");
						order.put("STATUS","DV0101");
						orderMap.put(orderId, order);
					}

					//生成发票主信息
					if(invoiceInfoMap.get(orderId) == null){
						invoiceInfo = new HashMap<String,String>();
						RandomGUID randomGUID = new RandomGUID();
						String invoiceId = StringUtil.getUuid32().substring(22) + randomGUID.valueAfterMD5.substring(22);
						invoiceInfo.put("INVOICE_ID",invoiceId);
						invoiceInfo.put("ORDER_ID",orderId);
						invoiceInfo.put("ISSUER",issuer);
						invoiceInfo.put("PAYEE",payee);
						invoiceInfo.put("REVIEW_CLERK",reviewClerk);
						invoiceInfo.put("IS_DELETE","0");
						invoiceInfo.put("ADD_USER",userId);
						invoiceInfo.put("ADD_TIME",StringUtil.getNowTime());
						invoiceInfo.put("UPDATE_USER",userId);
						invoiceInfo.put("UPDATE_TIME",StringUtil.getNowTime());
						invoiceInfoMap.put(orderId,invoiceInfo);
					}

					//生成发票明细信息
					if(invoiceInfoMap.get(orderId) != null){
						String invoiceId = invoiceInfoMap.get(orderId).get("INVOICE_ID");
						invoiceDetail = new HashMap<String,String>();
						invoiceDetail.put("INVOICE_DETAIL_ID",StringUtil.getUuid32());
						invoiceDetail.put("INVOICE_ID",invoiceId);
						invoiceDetail.put("ITEM_TITLE",itemTitle);
						invoiceDetail.put("ITEM_AMOUNT",itemAmount);
						invoiceDetail.put("DISCOUNT_AMOUNT",discountAmount);
						invoiceDetail.put("COMMODITY_CODE",commodityCode);
						invoiceDetail.put("ADD_USER",userId);
						invoiceDetail.put("ADD_TIME",StringUtil.getNowTime());
						invoiceDetail.put("UPDATE_USER",userId);
						invoiceDetail.put("UPDATE_TIME",StringUtil.getNowTime());
						invoiceDetail.put("IS_DELETE","0");
						invoiceDetailList.add(invoiceDetail);
						invoiceDetailMap.put(orderId,invoiceDetailList);
					}
				}
				if (CollectionUtil.isEmpty(orderMap)) {
					resultBuffer.append("没有需要导入的数据，请检查文件！");
				}else{
					//将组织好的数据插入数据库  orderMapList  invoiceInfoMapList  invoiceDetailMapList
					List<Map<String,String>> orders = new ArrayList<Map<String,String>>();
					for(Iterator<Map.Entry<String, Map<String, String>>> it = orderMap.entrySet().iterator(); it.hasNext();){
						Map.Entry<String, Map<String, String>> orderMapEntryTemp = it.next();
						Map<String, String> orderMapTemp = orderMapEntryTemp.getValue();
						orders.add(orderMapTemp);
						if(orders.size()%100 == 0 || !it.hasNext()){
							manualInvoiceInfoMapper.insertInvoiceOrder(orders);
							orders.clear();
						}
					}

					List<Map<String,String>> invoiceInfos = new ArrayList<Map<String,String>>();
					for(Iterator<Map.Entry<String, Map<String, String>>> it = invoiceInfoMap.entrySet().iterator(); it.hasNext();){
						Map.Entry<String, Map<String, String>> infoMapEntryTemp = it.next();
						Map<String, String> infoMapTemp = infoMapEntryTemp.getValue();
						invoiceInfos.add(infoMapTemp);
						if(invoiceInfos.size()%100 == 0 || !it.hasNext()){
							manualInvoiceInfoMapper.insertInvoiceInfo(invoiceInfos);
							invoiceInfos.clear();
						}
					}

					List<Map<String,String>> details = new ArrayList<Map<String,String>>();
					for(Iterator<Map.Entry<String, List<Map<String, String>>>> it = invoiceDetailMap.entrySet().iterator(); it.hasNext();){
						Map.Entry<String, List<Map<String, String>>> detailsEntryTemp = it.next();
						List<Map<String,String>> detailsTemp = detailsEntryTemp.getValue();
						details.addAll(detailsTemp);
						if(details.size() >= 100 || !it.hasNext()){
							manualInvoiceInfoMapper.insertInvoiceDetail(details);
							details.clear();
						}
					}
				}
			}
		} catch (Exception e) {
			resultBuffer.append("文件读取异常，请联系管理员！");
			e.printStackTrace();
		} finally {
			if (is != null) is.close();
		}
		return resultBuffer.toString();
	}


	//批量审核订单
	public Map<String, Object> orderCommit2Check(Map<String, Object> params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap.put("RESULT_CODE","10001");
		String order_ids = MapUtils.getString(params, "ORDER_IDS");
		String[] order_id = order_ids.split("_");
		final Map<String, Object> param = new HashMap<String, Object>();
		for (String id : order_id) {
			final String idTemp = id;
			class Task extends TimerTask {
				private Timer timer;

				public Task(Timer timer) {
					this.timer = timer;
				}
				@Override
				public void run() {
					param.clear();
					param.put("STATUS","DV0102");
					param.put("ORDER_ID",idTemp);
					manualInvoiceApproval(param);
					timer.cancel();
				}
			}
			Timer timer = new Timer();
			Task task = new Task(timer);
			timer.schedule(task, new Long(1000), new Long(5000));
		}
		return returnMap;
	}

	/**
	 * 取消开票
	 * 1.根据order_id删除表中的记录 t_invoice_order   t_invoice_info  t_invoice_detail  t_invoice_transaction_relation
	 * 2.清空缓存中order_id对应的开票记录
	 * @param
	 * @return
	 */
	public Map<String,Object> cancelInvoice(Map<String, Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap.put("result","取消开票成功");
		try {
			String orderId = StringUtil.safeToString(params.get("orderId"));
			if(StringUtil.isNotEmpty(orderId)) {
				//1.根据order_id删除表中数据
				manualInvoiceInfoMapper.cancelInvoice(params);
				//2.根据order_id清空缓存
				CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
				cacheClient.delete(StringUtil.safeToString(params.get("orderId")));

				//根据order_id查询出交易流水号，清除交易流水号对应的缓存信息
				List<Map<String,Object>> relationShips = invoiceMapper.getTransRelation(orderId);//获取订单关联关系
				if(CollectionUtil.isNotEmpty(relationShips)) {
					for (int i = 0; i < relationShips.size(); i++) {
						Map<String, Object> mapTemp = relationShips.get(i);
						String pickCode = StringUtil.safeToString(mapTemp.get("TRANSACTION_NUMBER"));
						cacheClient.delete(pickCode);
					}
				}
			}
		}catch (Exception e){
			returnMap.put("result","取消开票失败");
		}
		return returnMap;
	}

	public  int StringLength(String value) {
		if(value == null) return 0;
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

	public Map<String, Object> getTransInfoByTransNum(Map<String,Object> param) {
		return manualInvoiceInfoMapper.getTransInfoByTransNum(param);
	}

	/**
	 * 校验提取码是否能被禁用
	 * 根据交易信息表里是否有交易数据来判定的
	 * @param param
	 * @return
	 */
	public Map<String,Object> checkExtractedCodeCanForbidden(Map<String,Object> param){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap.put("suc",true);
		String extractedCode = StringUtil.safeToString(param.get("extractedCode"));
		//如果提取码不为空，则根据提取码获取提取码对应的交易信息
		if(StringUtil.isNotEmpty(extractedCode)) {
			returnMap = RDPUtil.execBaseBizService("invoiceService", "checkPickupCodeIsForbidden", param);
			Boolean suc = (Boolean) returnMap.get("suc");
			if(!suc){
				return returnMap;
			}
			String transNum = ShinHoDataUtil.deCode(extractedCode);
			//内部获取交易信息
			Map<String, Object> paramMap = new HashMap<String,Object>();
			//内部获取交易信息
			paramMap.put("PICKUPCODE", transNum);
			paramMap.put("method", "getTransaction");
			Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", paramMap);
			if(tempResult.get("transactionData") == null){
				//交易信息不存在,异步调用业务代码,获取交易信息
				paramMap.put("method", "getTransDataFromBw");
				paramMap.put("pickupCode", transNum);
				RDPUtil.execBaseBizService("requestRemoteService", paramMap);
			}
			final String finalPickCode = transNum;
			class Task extends TimerTask {
				private Timer timer;

				public Task(Timer timer) {
					this.timer = timer;
				}

				int i = 0;
				Boolean isActive = true;
				@Override
				public void run() {
					Map<String, Object> paramMap = new HashMap<String,Object>();
					//内部获取交易信息
					paramMap.put("PICKUPCODE", finalPickCode);
					paramMap.put("method", "getTransaction");
					Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", paramMap);

					Transaction transaction = (Transaction)tempResult.get("transactionData");
					if(transaction != null){
						isActive = false;
						timer.cancel();
					}
					if(i++>2){
						isActive = false;
						timer.cancel();
					}
					System.out.println("心跳获取数据执行中,提取码:"+finalPickCode);
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
			Transaction transaction = (Transaction) getCache(transNum);
			if(transaction == null){
				returnMap.put("suc",false);
				return returnMap;
			}
			Map<String,Object> transMap = new HashMap<String,Object>();
			transMap.put("TRANSACTION_NUMBER",transaction.getTRANSACTION_NUMBER());
			transMap.put("TRANSACTION_DATETIME",transaction.getTRANSACTION_DATETIME());
			transMap.put("STORE_NUMBER",transaction.getSTORE_NUMBER());
			transMap.put("TRANSACTION_AMOUNT",transaction.getTRANSACTION_AMOUNT());
			returnMap.put("data",transMap);
		}else{
			returnMap.put("suc",false);
		}
		return returnMap;
	}

	public Map<String, Object> forbiddenExtractedCode(Map<String, Object> param) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap.put("suc",true);
		String extractedCode = StringUtil.safeToString(param.get("extractedCode"));
		if(StringUtil.isEmpty(extractedCode)){
			returnMap.put("suc",false);
			returnMap.put("msg","提取码为空");
			return returnMap;
		}
		//根据提取码查询表里是否存在记录，如果存在直接更新is_delete标志，如果不存在则插入
		Map<String,Object> map = manualInvoiceInfoMapper.getForbiddenByExtractedCode(param);
		int num = 0;
		if(CollectionUtil.isEmpty(map)){
			num = manualInvoiceInfoMapper.forbiddenExtractedCode(param);
		}else{
			num = manualInvoiceInfoMapper.updateForbiddenByExtractedCode(param);
		}
		if(num != 1){
			returnMap.put("suc",false);
		}
		return returnMap;
	}
}
