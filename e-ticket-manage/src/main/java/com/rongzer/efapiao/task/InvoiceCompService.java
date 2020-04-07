package com.rongzer.efapiao.task;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;
import sajt.shdzfp.sl.service.Client;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.ServiceAbbreviations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.dao.FapiaoFileMapper;
import com.rongzer.efapiao.dao.InvoiceMapper;
import com.rongzer.efapiao.dao.ManualInvoiceInfoMapper;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPBaseService;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;

/**
 * 发票反补
 * @author qrl
 *
 */
@SuppressWarnings("deprecation")
@Service("invoiceCompService")
public class InvoiceCompService implements RDPBaseService{
	@Autowired
	private InvoiceMapper invoiceMapper;
	
	@Autowired
	private FapiaoFileMapper fapiaoFileMapper;

	@Autowired
	private ManualInvoiceInfoMapper manualInvoiceInfoMapper;
	
	private static Logger logger = Logger.getLogger(InvoiceCompService.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute(String strParam) {
		logger.info("************task start************");
		StringBuffer executeStr = new StringBuffer();
		//get unhandle fapiao info 
		int applyDate = StringUtil.toInt(RDPUtil.getSysConfig("efapiao.invoice.download"),7);//获取需要反补的异常发票，只反补一周以内的，一周前的通过修改新增日期实现反补功能
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = StringUtil.getNowDate();
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdfNew.parse(nowDate));
			cal.add(Calendar.DATE,applyDate*-1);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String downLoadDate = sdfNew.format(cal.getTime());
		List<Map<String,Object>> invoiceAppList = invoiceMapper.getInvoiceApply(downLoadDate);
		int sucNum = 0;
		int errNum = 0;
		int empNum = 0;
		if(CollectionUtil.isNotEmpty(invoiceAppList)){
			List<FPKJXX_FPTXXContent> inoiceInfoList = new ArrayList<FPKJXX_FPTXXContent>();
			for (Map<String, Object> applyInfo : invoiceAppList) {
				try {
					
					String invoiceAppNo = StringUtil.safeToString(applyInfo.get("INVOICE_REQUEST_SERIALNUMBER"));//发票请求流水号
					String orderPk = StringUtil.safeToString(applyInfo.get("ORDER_ID"));//订单ID
					if(StringUtil.isEmpty(orderPk)){//如果没有订单信息则跳过
						empNum++;
						continue;
					}
					FPKJXX_FPTXXContent invoiceContent = new FPKJXX_FPTXXContent();
					String invoiceId =  StringUtil.safeToString(applyInfo.get("INVOICE_ID"));
					invoiceContent.setFPQQLSH(invoiceAppNo);//发票请求流水号
					invoiceContent.setORDER_ID(orderPk);//订单ID
					invoiceContent.setINVOICE_ID(invoiceId);//订单号
					invoiceContent.setDSPTBM(StringUtil.safeToString(applyInfo.get("PLATFORM_CODE")));//平台编码
					invoiceContent.setNSRSBH(StringUtil.safeToString(applyInfo.get("TAXPAYER_IDENTIFY_NO")));//纳税人识别号
			        invoiceContent.setREGISTRATION_CODE(StringUtil.safeToString(applyInfo.get("REGISTRATION_CODE")));//注册码
			        invoiceContent.setAUTHORIZATION_CODE(StringUtil.safeToString(applyInfo.get("AUTHORIZATION_CODE")));//授权码
			        invoiceContent.setYFP_DM(StringUtil.safeToString(applyInfo.get("ORIGINAL_INVOICE_CODE")));//元发票代码
			        invoiceContent.setYFP_HM(StringUtil.safeToString(applyInfo.get("ORIGINAL_INVOICE_NUMBER")));//元发票号码
			        invoiceContent.setGHF_EMAIL(StringUtil.safeToString(applyInfo.get("PURCHASER_EMAIL")));//购方邮箱
			        applyInfo.put("INVOICE_CONTENT",invoiceContent);
			        //发票下载
					Map<String, Object> responseMap = RDPUtil.execBaseBizService("aisinoService", "invoiceDetail", applyInfo);
					if ("true".equals(responseMap.get("suc"))&& responseMap.containsKey("FP_DM")) {//下载成
						String pdfUrl = StringUtil.safeToString(responseMap.get("PDF_URL"));//pdf下载地址
                        String invoiceNumber = StringUtil.safeToString(responseMap.get("FP_HM"));//发票号码
                        String invoiceCode = StringUtil.safeToString(responseMap.get("FP_DM"));//发票代码
                        String totalAmountWithoutTax = StringUtil.safeToString(responseMap.get("HJBHSJE"));//合计不含税金额
                        String totalTaxAmount = StringUtil.safeToString(responseMap.get("KPHJSE"));//开票合计税额
                        String pdfile = StringUtil.toStringWithEmpty(responseMap.get("PDF_FILE"));//pdf文件内容
						String kprq = StringUtil.toStringWithEmpty(responseMap.get("KPRQ"));//开票日期
                        invoiceContent.setINVOICE_NUMBER(invoiceNumber);//发票号码
                        invoiceContent.setINVOICE_CODE(invoiceCode);//发票代码
                        invoiceContent.setINVOICE_URL(pdfUrl);//pdf下载地址
                        invoiceContent.setHJBHSJE(totalAmountWithoutTax);//合计不含税金额
                        invoiceContent.setHJSE(totalTaxAmount);//开票合计税额
                        invoiceContent.setPDF_FILE(pdfile);//pdf文件内容
						invoiceContent.setKPRQ(kprq);//开票日期
                        //获取明细
						List<Map<String, Object>> detaiList = (ArrayList<Map<String, Object>>) responseMap.get("FPMXXZ_XMXXS");//订单明细信息
                        //重新组织明细信息
                        List<FPKJXX_XMXXContent> fpmxList = new ArrayList<FPKJXX_XMXXContent>();
                        for (Map<String, Object> detailMap: detaiList) {
                        	FPKJXX_XMXXContent detailMapTemp = new FPKJXX_XMXXContent();
                        	detailMapTemp.setINVOICE_ID(invoiceId);//订单ID
                        	detailMapTemp.setXMMC(StringUtil.safeToString(detailMap.get("XMMC")));//开票名称
                        	detailMapTemp.setSPBM(StringUtil.safeToString(detailMap.get("SWBM")));//税务编码
                        	detailMapTemp.setXMDJ(StringUtil.safeToString(detailMap.get("XMDJ")));//项目单价
                        	detailMapTemp.setXMSL(StringUtil.safeToString(detailMap.get("XMSL")));//项目数量
                        	detailMapTemp.setXMJE(StringUtil.safeToString(detailMap.get("XMJE")));//项目金额
                        	detailMapTemp.setSE(StringUtil.safeToString(detailMap.get("SE")));//税额
                        	fpmxList.add(detailMapTemp);
						}
                        invoiceContent.setFpmxList(fpmxList);
						inoiceInfoList.add(invoiceContent);
						sucNum++;
					}else{
						errNum++;//error num add one
					}
				} catch (Exception e) {
					errNum++;
				}
			}
			if(CollectionUtil.isNotEmpty(inoiceInfoList)){
				updateInvoiceInfo(inoiceInfoList);//update fapiao info
			}
			executeStr.append("task end,success:"+sucNum+",error:"+errNum+",empty:"+empNum);
		}else{
			executeStr.append("task end,no data to download");
		}
		logger.info("*****************task end**************");
		return executeStr.toString();
	}
	
	/**
	 * save fapiao
	 * @param invoiceMap
	 * @param pickCode
	 * @param invoiceStatus
	 */
	public void updateInvoiceInfo(List<FPKJXX_FPTXXContent> invoiceContentList) {
		try {
			// FAPIAO INFO NOT EMPTY
			CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
			if (CollectionUtil.isNotEmpty(invoiceContentList)) {
				for (FPKJXX_FPTXXContent invoice : invoiceContentList) {
					updateInvoiceInfo(invoice);
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("ORDER_ID",invoice.getORDER_ID());
					params.put("INVOICE_STATUS",EfapiaoConstant.InvoiceStatus.SUCESS_INVOICE);
					//调整开票状态
					updateOrder(params,cacheClient);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
     * 更新订单状态
     * @param order
	 * @throws MemcachedException 
     */
    public void updateOrder(Map<String,Object> order,CacheClient cacheClient) throws MemcachedException {
        String orderId = StringUtil.safeToString(order.get("ORDER_ID"));
        invoiceMapper.updateOrderById(order);
    	List<Map<String,Object>> relationShips = invoiceMapper.getTransRelation(orderId);
    	for (Map<String, Object> map : relationShips) {
    		String pickupdode = StringUtil.safeToString(map.get("TRANSACTION_NUMBER"));
    		Object object = cacheClient.get(pickupdode);
    		if(object!=null){
    			cacheClient.delete(pickupdode);
    		}
    		map.put("PICKUPCODE", pickupdode);
    		map.put("method", "getTransDataFromBw");
			RDPUtil.execBaseBizService("requestRemoteService", map);
		}
        cacheClient.save(orderId, order);
    }
	
    /**
     * 更新发票数据
     * @param invoiceInfo
     */
    public void updateInvoiceInfo(FPKJXX_FPTXXContent invoiceInfo) {
    	invoiceMapper.updateInvoice(invoiceInfo);
        List<FPKJXX_XMXXContent> fpmxList = invoiceInfo.getFpmxList();
        invoiceMapper.updateInvoiceDetail(fpmxList);
        String pdfFile = invoiceInfo.getPDF_FILE();
        if(StringUtil.isNotEmpty(pdfFile)){
        	dealFapiaoPdf(invoiceInfo.getINVOICE_ID(),pdfFile);
        }
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
   	
}