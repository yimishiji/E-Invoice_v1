package com.rongzer.efapiao.service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.ServiceAbbreviations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.FapiaoFileMapper;
import com.rongzer.efapiao.dao.InvoiceMapper;
import com.rongzer.efapiao.model.*;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.common.util.StringUtil;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;
import sajt.shdzfp.sl.model.Interface;
import sajt.shdzfp.sl.service.Client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by he.bing on 2017/2/7.
 */
@Service("orderRemoteService")
public class OrderRemoteService extends BaseBusinessService {
	@Autowired
    private InvoiceMapper invoiceMapper;
	@Autowired
	private FapiaoFileMapper fapiaoFileMapper;
	private static Logger logger = Logger.getLogger(OrderRemoteService.class);
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
     *处理前台传递
     * @param paramMap
     * @return
     */
    public Map<String, Object> dealOrder(Map<String, Object> paramMap){
    	Map<String,Object> responseMap = new HashMap<String,Object>();
    	InvoiceOrder orderMap = new InvoiceOrder();
        String CookieValue = (String) paramMap.get("COOKIE_VALUE");//获取cookieValue
		String cookieMergeKey = CookieValue + "_MERGECODELIST";//合并开票的提取码在缓存中的key值cookieValue+_MERGECODELIST
		String extrCodesStr = StringUtil.toStringWithEmpty(this.getCache(cookieMergeKey));//获取缓存中的提取码
		String orderId = (String) paramMap.get("ORDER_ID");//获取订单ID
		//校验Transaction 信息
		boolean checkFlag = false;
		List<TransactionRelation> relations = new ArrayList<TransactionRelation>();
		List<TransactionItem> items = new ArrayList<TransactionItem>();
		if(StringUtil.isNotEmpty(extrCodesStr)){
			JSONArray extrCodeArray = JSONUtil.getJSONArrayFromStr(extrCodesStr);//提取码转为list
			String extrCode = "";//提取码
			for (int i = 0; i < extrCodeArray.size(); i++) {
				extrCode = StringUtil.toStringWithEmpty(extrCodeArray.get(i));
				if(StringUtil.isNotEmpty(extrCode)){
					Transaction transaction = (Transaction) getCache(extrCode);//获取缓存中的交易数据
					if(transaction!=null){
						if(EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(transaction.getINVOICE_STATUS())){
							TransactionRelation relation = new TransactionRelation();
							relation.setORDER_ID(orderId);
							relation.setTRANSACTION_NUMBER(extrCode);
							relations.add(relation);
							items.addAll(transaction.getTransactionItemList());
						}else{
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		if(checkFlag){
			//检查未通过
			responseMap.put("SUC", false);
			responseMap.put("MSG", "STATUS_ERROR");
			return responseMap;
		}
		orderMap.setORDER_ID(orderId);
		orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.NO_INVOICE);//订单状态 (开票中)
		orderMap.setINVOICE_TYPE(EfapiaoConstant.InvoiceType.DEFAULT);//订单类型
        orderMap.setIS_MANUAL(EfapiaoConstant.DefaultKey.FALSE);//是否手工票
        orderMap.setADD_TIME(StringUtil.getNowTime());
        orderMap.setUPDATE_TIME(StringUtil.getNowTime());
        orderMap.setIS_DELETE("0");
        String cookieTradeTypeKey = CookieValue + "_TRADETYPE";//交易类型的key值
        orderMap.setINVOICE_TRADE_TYPE((String) getCache(cookieTradeTypeKey));
        orderMap.setINVOICE_WRITE_TYPE((String) paramMap.get("INVOICE_WRITE_TYPE"));
        orderMap.setINVOICE_DETAIL_TYPE((String) paramMap.get("INVOICE_DETAIL_TYPE"));
        orderMap.setRelationShip(relations);
        //开具发票
        String INVOICE_WRITE_TYPE =  (String) paramMap.get("INVOICE_WRITE_TYPE");
        Map<String,Object> invoiceMap = new HashMap<String,Object>();
        
        
        invoiceMap.put("method", "dealInvoice");
        invoiceMap.put("ORDER_ID", orderId);
        invoiceMap.put("INVOICE_TRADE_TYPE",(String) getCache(cookieTradeTypeKey));
        invoiceMap.put("INVOICE_WRITE_TYPE",(String) paramMap.get("INVOICE_WRITE_TYPE"));
        invoiceMap.put("INVOICE_DETAIL_TYPE",(String) paramMap.get("INVOICE_DETAIL_TYPE"));
        if("E00801".equals(INVOICE_WRITE_TYPE)){
        	invoiceMap.put("PURCHASER_NAME",StringUtil.safeToString(paramMap.get("INVOICETITLE")));
        	invoiceMap.put("PURCHASER_EMAIL", StringUtil.safeToString(paramMap.get("MAILACCOUNT")));
        	invoiceMap.put("PURCHASER_TEL", StringUtil.safeToString(paramMap.get("INVOICEPHONE")));
        	orderMap.setINVOICE_WRITE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_PERSONAL);
        }else if("E00802".equals(INVOICE_WRITE_TYPE)){
        	invoiceMap.put("PURCHASER_NAME", StringUtil.safeToString(paramMap.get("INVOICETITLE")));
        	invoiceMap.put("PURCHASER_EMAIL", StringUtil.safeToString(paramMap.get("MAILACCOUNT")));
        	invoiceMap.put("PURCHASER_ID", StringUtil.safeToString(paramMap.get("TAXPAYERNUMBER")).replaceAll(" ", ""));
        	invoiceMap.put("PURCHASER_ADDRESS", StringUtil.safeToString(paramMap.get("ADDRESS")).replaceAll(" ", ""));
        	invoiceMap.put("PURCHASER_MOBILE", StringUtil.safeToString(paramMap.get("TELEPHONENUMBER")));
        	invoiceMap.put("PURCHASER_TEL", StringUtil.safeToString(paramMap.get("INVOICEPHONE")));
        	invoiceMap.put("PURCHASER_BANK_ACCOUNT",StringUtil.safeToString(paramMap.get("DEPOSITBANK"))+StringUtil.safeToString(paramMap.get("BANKACCOUNT")));
        	orderMap.setINVOICE_WRITE_TYPE(EfapiaoConstant.InvoiceType.INVOICE_ENTERPRISE);
        }
        //创建订单
        saveOrder(orderMap);
        //保存transaction与order之间关联关系
        saveRelations(relations);
        //处理开票
        try{
        	dealInvoice(invoiceMap);
        }catch(Exception e){
            orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.ERROR_INVOICE);//保存开票状态，开票异常
            updateOrder(orderMap);//更新订单信息
        }
        //返回订单ID
        responseMap.put("ORDER_ID",orderId);
        responseMap.put("SUC", true);
        return responseMap;
    }
    
    /**
     * 处理发票信息
     * @param paramMap
     * @return
     */
	public Map<String, Object> dealInvoice(Map<String, Object> paramMap) {
		//获取order对象 开具发票
		String orderId = (String) paramMap.get("ORDER_ID");//获取Cookie
		InvoiceOrder orderMap = (InvoiceOrder) getCache(orderId);
		String invoiceStatus = StringUtil.safeToString(orderMap.getINVOICE_STATUS());
		//判断是否已开过票,未开票的才可以执行开票动作
		if (EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(invoiceStatus) ||
				EfapiaoConstant.InvoiceStatus.ERROR_INVOICE.equals(invoiceStatus)) {
			String purchaserName = StringUtil.safeToString(paramMap.get("PURCHASER_NAME"));
			String purchaserEmail = StringUtil.safeToString(paramMap.get("PURCHASER_EMAIL"));
			String purchaserMobile = StringUtil.safeToString(paramMap.get("PURCHASER_MOBILE"));
			String purchaserId = StringUtil.safeToString(paramMap.get("PURCHASER_ID"));
			String purchaserAddress = StringUtil.safeToString(paramMap.get("PURCHASER_ADDRESS"));
			String purchaserTel = StringUtil.safeToString(paramMap.get("PURCHASER_TEL"));
			String purchaserBankAccount = StringUtil.safeToString(paramMap.get("PURCHASER_BANK_ACCOUNT"));
			String INVOICE_TRADE_TYPE = StringUtil.safeToString(paramMap.get("INVOICE_TRADE_TYPE"));
			String INVOICE_WRITE_TYPE = StringUtil.safeToString(paramMap.get("INVOICE_WRITE_TYPE"));
			String INVOICE_DETAIL_TYPE = StringUtil.safeToString(paramMap.get("INVOICE_DETAIL_TYPE"));
			orderMap.setPURCHASER_NAME(purchaserName);
			orderMap.setPURCHASER_EMAIL(purchaserEmail);
			orderMap.setPURCHASER_MOBILE(purchaserMobile);
			orderMap.setPURCHASER_ID(purchaserId);
			orderMap.setPURCHASER_ADDRESS(purchaserAddress);
			orderMap.setPURCHASER_TEL(purchaserTel);
			orderMap.setPURCHASER_BANK_ACCOUNT(purchaserBankAccount);
			orderMap.setINVOICE_TRADE_TYPE(INVOICE_TRADE_TYPE);
			orderMap.setINVOICE_DETAIL_TYPE(INVOICE_DETAIL_TYPE);
			orderMap.setINVOICE_WRITE_TYPE(INVOICE_WRITE_TYPE);
			//拼接开票内容
			FPKJXX_FPTXXContent invoiceContent = dealInvoiceContent(orderMap);
			if(invoiceContent!=null){
				//根据支付信息计算折扣行
				invoiceContent = dealInvoiceContentWithPaymentInfo(orderMap,invoiceContent);
				//
				List<FPKJXX_FPTXXContent> invoiceList = ShinHoDataUtil.splitInvoice(invoiceContent);
				//缓存发票信息 1：N
				orderMap.setInvoiceList(invoiceList);
				//设置纳税人信息
				orderMap.setTAXPAYER_ID(invoiceContent.getNSRSBH());
				//保存开票状态，开票中E00502
				orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.IN_INVOICE);
				//更新订单信息
				saveInvoice(invoiceList);
				updateOrder(orderMap);
				Boolean invoiceFlag = true;// 发票是否传输成功成功
				//批量调用开票请求接口
				String returnCode = null;
				for (FPKJXX_FPTXXContent invoice : invoiceList) {
					paramMap.put("INVOICE_CONTENT", invoice);
					Map<String, Object> returnMap = RDPUtil.execBaseBizService("aisinoService", "invoiceIssued", paramMap);
					returnCode = StringUtil.toStringWithEmpty(returnMap.get("RETURN_CODE"));
					Interface result = (Interface)returnMap.get("RESULT_OBJ");
					if (!"0000".equals(returnCode)) {
						invoiceFlag = false;
						break;
					}
				}
				//下载发票信息
				if (invoiceFlag) {
					orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.DOWNLOADING_INVOICE);
					updateOrder(orderMap);//更新订单信息


					final List<FPKJXX_FPTXXContent> fapiaoApplyList =  invoiceList;
					final InvoiceOrder orderMapTemp = orderMap;
					class Task extends TimerTask {
						private Timer timer;
						public Task(Timer timer) {
							this.timer = timer;
						}
						int i = 0;
						@Override
						public void run() {
							//将最后一次心跳下载发票标志放入参数，如果在5次下载发票还没有成功，
							boolean suc = true;
							try {
								for (FPKJXX_FPTXXContent invoiceTmp : fapiaoApplyList) {
									//发票信息
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
										String pdfile = StringUtil.toStringWithEmpty(returnMap.get("PDF_FILE"));
										String kprq = StringUtil.toStringWithEmpty(returnMap.get("KPRQ"));
										invoiceTmp.setINVOICE_NUMBER(invoiceNumber);
										invoiceTmp.setINVOICE_CODE(invoiceCode);
										invoiceTmp.setINVOICE_URL(pdfUrl);
										invoiceTmp.setHJBHSJE(totalAmountWithoutTax);
										invoiceTmp.setHJSE(totalTaxAmount);
										invoiceTmp.setPDF_FILE(pdfile);
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
									}else{
										suc = false;
									}
								}
								if (suc) {
									for (FPKJXX_FPTXXContent invoice : fapiaoApplyList) {
										String invoiceNumber = StringUtil.toStringWithEmpty(invoice.getINVOICE_NUMBER());
										orderMapTemp.setTAXPAYER_ID(invoice.getNSRSBH());
										if (StringUtil.isNotEmpty(invoiceNumber)) {
											//保存pdf文件，并关联到对应的发票数据
											String pdfFile = invoice.getPDF_FILE();
											if(StringUtil.isNotEmpty(pdfFile)){
												//存储发票信息
												dealFapiaoPdf(invoice.getINVOICE_ID(),pdfFile);
											}
											updateInvoiceInfo(invoice);
										} else {
											suc=false;
										}
									}
									if(suc){
										//调整开票状态
										orderMapTemp.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE);
										orderMapTemp.setInvoiceList(fapiaoApplyList);
										updateOrder(orderMapTemp);
									}
									//结束定时任务
									timer.cancel();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}finally{
								if (i++ > 5) {
									timer.cancel();
								}
							}
						}
					}
					Timer timer = new Timer();
					Task task = new Task(timer);
					timer.schedule(task, new Long(0), new Long(2000));
				}else{
					orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.ERROR_INVOICE);
					updateOrder(orderMap);
				}
			}else{
				orderMap.setINVOICE_STATUS(EfapiaoConstant.InvoiceStatus.ERROR_INVOICE);//保存开票状态，开票异常
				updateOrder(orderMap);//更新订单信息
			}
		}
		return null;
	}


	/**
     * 保存发票文件，并配置关联关系
     * @param invoice_ID
     * @param pdfFile
     */
	private void dealFapiaoPdf(String bizId, String pdfFile) {
    	if(StringUtil.isNotEmpty(bizId)&&StringUtil.isNotEmpty(pdfFile)){
    		//更新数据状态，并保存文件和数据关系
    		Map<String,Object> fileInfo = new HashMap<String,Object>();
    		//获取存储系统基本参数
    		String bucketName = RDPUtil.getSysConfig("efapiao.shinho.bucket");
    		String keyId = RDPUtil.getSysConfig("efapiao.shinho.keyid");
    		String keyValue = RDPUtil.getSysConfig("efapiao.shinho.keyValue");
    		AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(keyId, keyValue));
    		try{
    			Region region = Region.getRegion(Regions.CN_NORTH_1);
    			s3Client.setRegion(region);
    			final String serviceEndpoint = region.getServiceEndpoint(ServiceAbbreviations.S3);
    			s3Client.setEndpoint(serviceEndpoint);
    			byte[] getData = Client.getDecodeBase64(pdfFile);
    			InputStream inWithCode =  new ByteArrayInputStream(getData);
    			ObjectMetadata metaData= new ObjectMetadata();
    			metaData.setContentType("pdf");
    			s3Client.putObject(bucketName, bizId, inWithCode, metaData);
    			//保存文件的key
    			fileInfo.put("UPDATE_TIME",StringUtil.getNowTime());
    			fileInfo.put("FILE_ID",StringUtil.getUuid32());
    			fileInfo.put("RELATIVE_PATH",bizId);
    			fileInfo.put("REAL_FILE_NAME",bizId+".pdf");
    			fileInfo.put("FILE_NAME",bizId+".pdf");
    			fileInfo.put("FILE_TYPE","pdf");
    			fileInfo.put("DESCRIPTION","发票文件备份");
    			fileInfo.put("UPLOAD_TIME",StringUtil.getNowTime());
    			fileInfo.put("OPER_ID","sysadmin");
    			fileInfo.put("BIZ_ID",bizId);
    			fapiaoFileMapper.addFileInfo(fileInfo);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
	}
    
    private void updateOrderTradesCache(InvoiceOrder orderMap) {
    	List<TransactionRelation> relationShips = orderMap.getRelationShip();
    	for(TransactionRelation relationShip:relationShips){
    		String pickupdode = relationShip.getTRANSACTION_NUMBER();
    		Map<String, Object> invoiceServiceParamMap = new HashMap<String, Object>();
			invoiceServiceParamMap.put("PICKUPCODE", pickupdode);
			invoiceServiceParamMap.put("WITHOUT_CACHE", "true");
			invoiceServiceParamMap.put("method", "getTransaction");
			RDPUtil.execBaseBizService("invoiceService", invoiceServiceParamMap);
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
     * 更新订单状态
     * @param order
     */
    public void updateOrder(InvoiceOrder order) {
        String orderId = StringUtil.safeToString(order.getORDER_ID());
        invoiceMapper.updateOrder(order);
        saveCache(orderId, order);
        updateOrderTradesCache(order);
    }
    
    /**
     * 初始化发票信息
     * @param orderMap
     * @return FPKJXX_FPTXXContent
     */
    public FPKJXX_FPTXXContent dealInvoiceContent(InvoiceOrder orderMap) {
    	//交易关联关系
    	List<TransactionRelation> relationShip = orderMap.getRelationShip();
    	if(CollectionUtil.isNotEmpty(relationShip)){
	    	//获取基础数据
	        Map<String, Object> baseData = new HashMap<String, Object>();
	        baseData = RDPUtil.execBaseBizService("eFapiaoBaseService", "getBaseData", baseData);
	        // 获取开票内容(初始化数据)
	        Map<String, Object> defaultInvoiceContents = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.INVOICE_CONTENT);
	        // 获取支付方式(初始化数据)
	        Map<String, Object> defaultPayments = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.PAYMENT);
	        // 获取货品信息(初始化数据)
	        Map<String, Object> defaultGoodsInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GOODS_INFO);
	        //获取门店信息(初始化数据)
	        Map<String, Object> storeInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.STORE_INFO);
	        //获取分组和开票内容之间的关系(初始化数据)
	        Map<String, Object> groupInfos = (Map<String, Object>) baseData.get(EfapiaoConstant.CacheKey.GROUP_INFO);
	        //发票内容
	        FPKJXX_FPTXXContent invoiceContent = new FPKJXX_FPTXXContent();
	        //发票明细
	        List<FPKJXX_XMXXContent> invoiceDetailList = new ArrayList<FPKJXX_XMXXContent>();
	        //明细Map
	        List<TransactionItem> transactionItemList = new ArrayList<TransactionItem>();
	        //抓取缓存中所有的交易明细
	        Map<String,Object> params = new HashMap<String,Object>();
	        //获取门店号
	        String storeNo ="";
	        for (int i = 0; i < relationShip.size(); i++) {
	        	TransactionRelation relationMap = relationShip.get(i);
	        	String pickUpCode = relationMap.getTRANSACTION_NUMBER();
	        	Map<String, Object> paramsTemp = new HashMap<String, Object>();
	    		paramsTemp.put("method", "getTransaction");
	    		paramsTemp.put("PICKUPCODE", pickUpCode);
	    		Map<String, Object> transResultMap = RDPUtil.execBaseBizService("invoiceService", paramsTemp);
	    		Transaction transaction = (Transaction)transResultMap.get("transactionData");
	    		transactionItemList.addAll(transaction.getTransactionItemList());
	        	if(i==0){
	        		storeNo = transaction.getSTORE_NUMBER();
	        	}
			}
	        Map<String, Object> storeInfo = new HashMap<String, Object>();
	        if (storeInfos.containsKey(storeNo)) {
	            storeInfo = (Map<String, Object>) storeInfos.get(storeNo);
	        }
	        String taxpayerType = StringUtil.safeToString(storeInfo.get("TAXPAYER_TYPE"));
	        LinkedHashMap<String,Object> invoiceDetailMap = new LinkedHashMap<String,Object>();
	        for (TransactionItem transactionItem : transactionItemList) {
	        	BigDecimal itemAmountDiscount = new BigDecimal(ShinHoDataUtil.objectToStr(transactionItem.getITEM_AMOUNT_AFTER_DISCOUNT()));
	        	//单品金额需要大于0才可放入开票内容
	        	if (defaultGoodsInfos.containsKey(transactionItem.getITEM_CODE()) && itemAmountDiscount.compareTo(BigDecimal.ZERO) == 1){
	        		 Map<String, Object> defaultGoodsInfo = (Map<String, Object>) defaultGoodsInfos.get(transactionItem.getITEM_CODE());
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
	                 if(EfapiaoConstant.InvoiceType.INVOICE_DETAIL.equals(orderMap.getINVOICE_DETAIL_TYPE())){
	                	 itemTitle = (String) defaultGoodsInfo.get("GOODS_NAME_CN");
	                 }
	                 //原价
	                 BigDecimal itemAmount = new BigDecimal(ShinHoDataUtil.objectToStr(transactionItem.getITEM_AMOUNT_AFTER_DISCOUNT()));
	                 //折后价
	                 BigDecimal itemAmountAfterDiscount = new BigDecimal(ShinHoDataUtil.objectToStr(transactionItem.getITEM_AMOUNT_AFTER_DISCOUNT()));
	                 //折扣价
	                 BigDecimal disAmount = itemAmount.add(itemAmountAfterDiscount.multiply(new BigDecimal(-1)));//原价减去折后价
	                 
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
	                 itemDetail.setXMDW(UNIT_CN);  //单位
	                 itemDetail.setHSBZ("1");//含税标志
	                 int r = disAmount.compareTo(BigDecimal.ZERO);
	                 if (r == 1) {
	                     itemDetail.setFPHXZ("2");//发票行性质 （是否折扣行）0正常行 1折扣行 2被折扣行
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
	                    itemDetail.setXMDJ(ShinHoDataUtil.objectToStr(disAmount));//项目单价
	                     
	                     
	                     itemDetail.setSPBM(commodityCode);//商品编码
	                     itemDetail.setXMJE(ShinHoDataUtil.objectToStr(disAmount.multiply(new BigDecimal(-1))));//项目金额
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
	        
	
	        for (Map.Entry<String, Object> entry : invoiceDetailMap.entrySet()) {
	        	invoiceDetailList.add((FPKJXX_XMXXContent) entry.getValue());
	        }

        	if(CollectionUtil.isNotEmpty(invoiceDetailList)){
		        //发票限额
		        invoiceContent.setINVOICE_LIMIT_AMOUNT(StringUtil.safeToString(storeInfo.get("INVOICE_LIMIT_AMOUNT")));
		        invoiceContent.setFpmxList(invoiceDetailList);
		        //平台编码
		        invoiceContent.setDSPTBM(StringUtil.safeToString(storeInfo.get("PLATFORM_CODE")));
		        invoiceContent.setREGISTRATION_CODE(StringUtil.safeToString(storeInfo.get("REGISTRATION_CODE")));
		        invoiceContent.setAUTHORIZATION_CODE(StringUtil.safeToString(storeInfo.get("AUTHORIZATION_CODE")));
		        //开票方识别号
		        invoiceContent.setNSRSBH(StringUtil.safeToString(storeInfo.get("TAXPAYER_IDENTIFY_NO")));
		        //开票方名称
		        invoiceContent.setNSRMC(StringUtil.safeToString(storeInfo.get("TAXPAYER_NAME_CN")));
		        //主要开票项目
		        invoiceContent.setKPXM(invoiceDetailList.get(0).getXMMC());
		        //销货方识别号
		        invoiceContent.setXHF_NSRSBH(StringUtil.safeToString(storeInfo.get("TAXPAYER_IDENTIFY_NO")));
		        //销货方名称
		        invoiceContent.setXHFMC(StringUtil.safeToString(storeInfo.get("TAXPAYER_NAME_CN")));
		        //销货方地址
		        invoiceContent.setXHF_DZ(StringUtil.safeToString(storeInfo.get("TAXPAYER_ADDRESS")));
		        //销货方电话
		        invoiceContent.setXHF_DH(StringUtil.safeToString(storeInfo.get("TAXPAYER_PHONE")));
		        //销货方银行账号
		        invoiceContent.setXHF_YHZH(StringUtil.safeToString(storeInfo.get("TAXPAYER_BANK")) + StringUtil.safeToString(storeInfo.get("TAXPAYER_ACCOUNT")));
		        //购货方名称
		        invoiceContent.setGHFMC(orderMap.getPURCHASER_NAME());
		        //购货方纳税人识别号
		        invoiceContent.setGHF_NSRSBH(orderMap.getPURCHASER_ID());
		        //购货方纳税人地址
		        invoiceContent.setGHF_DZ(orderMap.getPURCHASER_ADDRESS());
		        //购货方纳税人固定电话
		        invoiceContent.setGHF_GDDH(orderMap.getPURCHASER_TEL());
		        //购货方手机
		//        invoiceContent.setGHF_SJ();
		        //购货方邮箱
		        invoiceContent.setGHF_EMAIL(orderMap.getPURCHASER_EMAIL());
		        //购货方纳税人银行账号
		        invoiceContent.setGHF_YHZH(orderMap.getPURCHASER_BANK_ACCOUNT());
		        //开票员
		        invoiceContent.setKPY(StringUtil.safeToString(storeInfo.get("ISSUE")));
		        //收款员
		        invoiceContent.setSKY(StringUtil.safeToString(storeInfo.get("SKY")));
		        //复核人
		        invoiceContent.setFHR(StringUtil.safeToString(storeInfo.get("FHR")));
		
		        //开票类型 (红票2 正票1)TODO
		        invoiceContent.setKPLX("1");
		        //操作代码
		        invoiceContent.setCZDM("10");
		
		        //特殊冲红标志
		        invoiceContent.setTSCHBZ("");
		
		        //价税合计金额
		        invoiceContent.setKPHJJE(orderMap.getTAXPAYER_ID());
		        //合计不含税金额
		        invoiceContent.setHJBHSJE("0");
		        //合计税额
		        invoiceContent.setHJSE("0");
		        //订单号
		        invoiceContent.setORDER_ID(orderMap.getORDER_ID());
		        return invoiceContent;
        	}else{
        		return null;
        	}
    	}else{
        	return null;
        }
    }
    
    
    private void saveRelations(List<TransactionRelation> relations) {
		for(TransactionRelation relation : relations ){
			saveRelation(relation);
		}
	}

	public void saveOrder(InvoiceOrder orderMap) {
        String orderId = StringUtil.safeToString(orderMap.getORDER_ID());
        //保存order信息入数据库和缓存
        invoiceMapper.saveOrder(orderMap);
        saveCache(orderId, orderMap);
    }
    
    public Map<String, Object> getOrder(String orderId) {
        Map<String, Object> order = (Map<String, Object>) getCache(orderId);
        if (CollectionUtil.isEmpty(order)) {
            //通过数据库查询对应order
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ORDER_ID", orderId);
            order = invoiceMapper.getOrder(params);
            //拼接对应transaction


            //拼接对应invoice
            //TODO

            if (CollectionUtil.isNotEmpty(order)) {
                saveCache(orderId, order);
            }
        }
        return order;
    }
    
    public Map<String, Object> getRelation(Map<String, Object> paramMap) {
    	String transactionNumber = (String) paramMap.get("TRANSACTION_NUMBER");
        String key = transactionNumber + EfapiaoConstant.CacheKey.RELATION_KEY;
        Map<String, Object> relation = (Map<String, Object>) getCache(key);
        if (CollectionUtil.isEmpty(relation)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("TRANSACTION_NUMBER", transactionNumber);
            relation = invoiceMapper.getRelation(params);
        }
        return relation;
    }
    
    public void saveRelation(TransactionRelation relation) {
        relation.setID(StringUtil.getUuid32());
        relation.setADD_TIME(StringUtil.getNowTime());
        relation.setUPDATE_TIME(StringUtil.getNowTime());
        String transactionNumber = StringUtil.safeToString(relation.getTRANSACTION_NUMBER());
        String key = transactionNumber + EfapiaoConstant.CacheKey.RELATION_KEY;
        //保存入库 TODO
        invoiceMapper.saveRelation(relation);
        //保存入缓存
        saveCache(key, relation);
    }

    /**
     * 根据不可开票的支付方式,开具折扣行,并且分摊到各个发票明细上
     * @param orderMap
     * @return FPKJXX_FPTXXContent
     */
    public FPKJXX_FPTXXContent dealInvoiceContentWithPaymentInfo(InvoiceOrder orderMap,FPKJXX_FPTXXContent content) {
    	//获取基础数据
        Map<String, Object> defaultPayments = new HashMap<String, Object>();
        defaultPayments = RDPUtil.execBaseBizService("eFapiaoBaseService", "getPaymentMap",defaultPayments);
        //交易关联关系
        List<TransactionRelation> relationShip = orderMap.getRelationShip();
        
        //发票明细
        List<FPKJXX_XMXXContent> invoiceDetailList = content.getFpmxList();
        //新的发票明细
        List<FPKJXX_XMXXContent> invoiceDetailListNew = new ArrayList<FPKJXX_XMXXContent>();
        
        //不可开票金额
        BigDecimal bkkpje = BigDecimal.ZERO;
        for (int i = 0; i < relationShip.size(); i++) {
        	TransactionRelation relationMap = relationShip.get(i);
        	String pickUpCode = relationMap.getTRANSACTION_NUMBER();
        	Transaction transaction = (Transaction)getCache(pickUpCode);
        	 //交易支付信息列表
            List<TransactionPayment> transactionPaymentList = transaction.getTransactionPaymentList();
            for(TransactionPayment payment : transactionPaymentList){
            	String PAYMENT_CODE = payment.getPAYMENT_CODE();
				Map<String, Object> paymentInfo = (Map<String, Object>) defaultPayments.get(PAYMENT_CODE);
				if(CollectionUtil.isNotEmpty(paymentInfo)){
					String CAN_INVOICE = (String) paymentInfo.get("CAN_INVOICE");
					if(StringUtil.isNotEmpty(CAN_INVOICE) &&EfapiaoConstant.DefaultKey.TRUE.equals(CAN_INVOICE)){
					}else{
						//计算总金额
						try {
							bkkpje = bkkpje.add(StringUtil.toBigDecimal(payment.getPAYMENT_AMOUNT()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}else{
					//计算总金额
					try {
						bkkpje = bkkpje.add(StringUtil.toBigDecimal(payment.getPAYMENT_AMOUNT()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            }
		}
        //如果有禁止开票,需要计算折扣项
        if(bkkpje.compareTo(BigDecimal.ZERO) == 1){
        	try {
        		//开票合计金额
				BigDecimal kphjje = BigDecimal.ZERO;
				for(int i=0;i<invoiceDetailList.size();i++){
					FPKJXX_XMXXContent detailContent = invoiceDetailList.get(i);
					kphjje = kphjje.add(StringUtil.toBigDecimal(detailContent.getXMJE()));
				}
				//已开折扣合计
				BigDecimal ykzkhj = BigDecimal.ZERO;
				//折扣金额
				BigDecimal zkje = BigDecimal.ZERO;
				for(int i=0;i<invoiceDetailList.size();i++){
					FPKJXX_XMXXContent detailContent = invoiceDetailList.get(i);
					if(i == invoiceDetailList.size()-1){
						//计算该项目的折扣金额
						zkje = bkkpje.subtract(ykzkhj);
					}else{
						//项目金额
						BigDecimal xmje = StringUtil.toBigDecimal(detailContent.getXMJE());
						//计算该项目的折扣金额
						zkje = xmje.divide(kphjje,6,BigDecimal.ROUND_HALF_DOWN).multiply(bkkpje).setScale(2,BigDecimal.ROUND_HALF_DOWN);						
						ykzkhj = ykzkhj.add(zkje);
						
					}
					BigDecimal num = new BigDecimal("0.01");																			
					  if(zkje.compareTo(num)==-1){
						  invoiceDetailListNew.add(detailContent);
						  continue;
					  }					  
					//折扣项
					FPKJXX_XMXXContent zkx = (FPKJXX_XMXXContent) detailContent.clone();
					zkx.setXMJE("-"+zkje.toString());
					zkx.setXMDJ(zkje.toString());
					zkx.setXMSL("-1");
					zkx.setFPHXZ("1");
					detailContent.setFPHXZ("2");
					invoiceDetailListNew.add(detailContent);
					invoiceDetailListNew.add(zkx);
				}
				content.setFpmxList(invoiceDetailListNew);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
    	return content;
    }
    
    /**
     * 根据订单号获取发票信息列表
     * @param paramMap
     * @return
     */
    public Map<String, Object> selectInvoiceInfoByOrderId(Map<String, Object> paramMap) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();;
    	String orderId = (String) paramMap.get("orderId");
    	List<Map<String, String>> invoiceInfoList = invoiceMapper.selectInvoiceInfoByOrderId(orderId);
    	resultMap.put("invoiceInfoList", invoiceInfoList);
    	return resultMap;
    	
    }
    /**
   	 * 抓取的数据进行保存
   	 * @param getData
   	 * @param savePath
   	 * @param fileName
   	 */
   	public static void saveFile(byte[] getData,String savePath,String fileName){
   		try {
   			// 文件保存位置
   			File saveDir = new File(savePath);
   			if (!saveDir.exists()) {
   				saveDir.mkdirs();
   			}
   			File file = new File(saveDir + File.separator + fileName);
   			FileOutputStream fos = new FileOutputStream(file);
   			fos.write(getData);
   			if (fos != null) {
   				fos.close();
   			}
   			System.out.println("fapiao file save");
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   	}
   	
    /**
     * 处理发票信息
     * @param paramMap
     * @return
     */
    public Map<String, Object> reSendEmail(Map<String, Object> params) {
    	Map<String, Object> returnMap = new HashMap<String, Object>();
    	String invoiceId = (String) params.get("invoiceId");
		String newEmail = (String) params.get("newEmail");
		if(StringUtil.isNotEmpty(invoiceId)&&StringUtil.isNotEmpty(invoiceId)){
			Map<String,Object> invoiceInfo = invoiceMapper.getInvoiceInfoByInvoiceId(invoiceId);
			List<Map<String, Object>> invoiceList = new ArrayList<Map<String,Object>>();// 发票信息获取
			invoiceList.add(invoiceInfo);
			// 获取邮箱页面填写的邮箱
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("method", "invoicePush");
			paramsMap.put("EMAIL", params.get("newEmail"));
			paramsMap.put("invoiceList", invoiceList);
			returnMap = RDPUtil.execBaseBizService("aisinoService", paramsMap);	
		}
		return null;
    }
}