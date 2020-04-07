package com.rongzer.efapiao.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import sajt.shdzfp.sl.model.COMMON_NODEContent;
import sajt.shdzfp.sl.model.FPKJXX_DDXXContent;
import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;
import sajt.shdzfp.sl.model.FPKJXX_XMXXContent;
import sajt.shdzfp.sl.model.FPMXXZContent;
import sajt.shdzfp.sl.model.GlobalInfo;
import sajt.shdzfp.sl.model.Interface;
import sajt.shdzfp.sl.model.ReturnStateInfo;
import sajt.shdzfp.sl.model.ZData;
import sajt.shdzfp.sl.service.Client;
import sun.misc.BASE64Encoder;

import com.rongzer.ecservice.common.service.BaseBusinessService;
import com.rongzer.efapiao.util.XmlParser;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;

/**
 * Created by he.bing on 2017/1/24.
 */
@Service("aisinoService")
public class AisinoService extends BaseBusinessService {

	private static Logger logger = Logger.getLogger(AisinoService.class);

    //发票开具serviceName
    private static String FPKJ = "ECXML.FPKJ.BC.E_INV";
    //发票推送serviceName
    private static String EMAILPHONEFPTS = "ECXML.EMAILPHONEFPTS.TS.E.INV";
    //发票明细下载serviceName
    private static String FPMXXZ = "ECXML.FPMXXZ.CX.E_INV";

    @Override
    protected Map<String, Object> process(Map<String, Object> paramMap) {
        if (!paramMap.containsKey("method")) {
        } else {
            String methodName = (String) paramMap.get("method");
            try {
                Method method = this.getClass()
                        .getMethod(methodName, Map.class);
                paramMap = (Map) method.invoke(this, paramMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paramMap;
    }

    /**
     * 开具发票接口
     *
     * @param paramMap
     * @return
     */
    public Map<String, Object> invoiceIssued(Map<String, Object> paramMap) {    
    	FPKJXX_FPTXXContent  invoiceContent= (FPKJXX_FPTXXContent)paramMap.get("INVOICE_CONTENT");
        String content = "";
        List<FPKJXX_XMXXContent> fpmxList = invoiceContent.getFpmxList();
        //发票内容 End
        //订单信息 Start
        FPKJXX_DDXXContent ddxxContent = new FPKJXX_DDXXContent();
        //订单号
        ddxxContent.setDDH(invoiceContent.getINVOICE_ID());

        content += "<REQUEST_FPKJXX class=\"REQUEST_FPKJXX\">" + invoiceContent.toString();
        String temp = "<FPKJXX_XMXXS class=\"FPKJXX_XMXX;\" size=\"" + fpmxList.size() + "\">";
        for (int i = 0; i < fpmxList.size(); i++) {
            temp += fpmxList.get(i).toString();
        }
        temp += "</FPKJXX_XMXXS>";
        content += temp;
        content += ddxxContent.toString();
        content += "</REQUEST_FPKJXX>";
        Map<String, String> platFormInfo = new HashMap<String, String>();
        platFormInfo.put("PLATFORM_NUMBER", invoiceContent.getDSPTBM());
        platFormInfo.put("INVOICINGPART_ID", invoiceContent.getNSRSBH());
        platFormInfo.put("REGISTRATION_CODE", invoiceContent.getREGISTRATION_CODE());
        platFormInfo.put("AUTHORIZATION_CODE", invoiceContent.getAUTHORIZATION_CODE());
        //申请开具发票
        Map<String, Object> commonAisinoMap = commonAisinoMessageSend(content, FPKJ, platFormInfo, "invoice apply",invoiceContent.getFPQQLSH());
        return commonAisinoMap;
    }


    public Map<String, Object> invoiceDetail(Map<String, Object> paramMap) {
    	FPKJXX_FPTXXContent  invoiceContent= (FPKJXX_FPTXXContent)paramMap.get("INVOICE_CONTENT");
        FPMXXZContent content = new FPMXXZContent();
        Map<String, Object> contentMap = new HashMap<String, Object>();
        content.setFPQQLSH(invoiceContent.getFPQQLSH());
        content.setDDH(invoiceContent.getINVOICE_ID());
        content.setDSPTBM(invoiceContent.getDSPTBM());
        content.setNSRSBH(invoiceContent.getNSRSBH());

        Map<String, String> platFormInfo = new HashMap<String, String>();
        platFormInfo.put("PLATFORM_NUMBER", invoiceContent.getDSPTBM());
        platFormInfo.put("INVOICINGPART_ID", invoiceContent.getNSRSBH());
        platFormInfo.put("REGISTRATION_CODE", invoiceContent.getREGISTRATION_CODE());
        platFormInfo.put("AUTHORIZATION_CODE", invoiceContent.getAUTHORIZATION_CODE());

        Map<String, Object> map = commonAisinoMessageSend(content.toString(), FPMXXZ, platFormInfo, "invoice download",invoiceContent.getFPQQLSH());
//		Map<String, Object> resultMap = new HashMap<String, Object>();
        Interface result = (Interface) map.get("RESULT_OBJ");
        ZData data = result.getzData();
        ReturnStateInfo returnStateInfo = result.getReturnStateInfo();
        if ("0000".equals(returnStateInfo.getReturnCode())) {
        	try {
				String resultContent = data.getContent();
				//进行base64转码
				byte[] compressed = Client.getDecodeBase64(resultContent);
				//如果返回值进行了压缩
				if(data.getDataDescription().getZipCode().equals("1")){
					compressed =  Client.gunzip(compressed);
				}
				//再解密
				if(data.getDataDescription().getEncryptCode().equals("1")){
					byte[] key = result.getGlobalInfo().getPassWord().substring(10).getBytes();
						resultContent = new String(Client.ees3DecodeECB(key,compressed),"UTF-8");
				}
				contentMap = XmlParser.stringXml2Map(resultContent);
				contentMap.put("suc", "true");
        	} catch (Exception e) {
        		e.printStackTrace();
        		contentMap.put("suc", "false");
        	}
		} else {
            contentMap.put("suc", "false");
        }
        return contentMap;
    }

    /**
     * 发票推送
     * @param paramMap
     * @return
     */
    public Map<String, Object> invoicePush(Map<String, Object> paramMap){
		List<Map<String,Object>> invoiceList = (ArrayList<Map<String,Object>>)paramMap.get("invoiceList");
		Map<String, Object> contentMap = new HashMap<String,Object>();
		String content = "<REQUEST_EMAILPHONEFPTS class=\"REQUEST_EMAILPHONEFPTS\"><TSFSXX class=\"TSFSXX\">";
		{
			ArrayList<COMMON_NODEContent> list1 = new ArrayList<COMMON_NODEContent>();
			{
				COMMON_NODEContent node = new COMMON_NODEContent();
				node.setNAME("TSFS");
				node.setVALUE("0");
				list1.add(node);
			}
			{
				COMMON_NODEContent node = new COMMON_NODEContent();
				node.setNAME("EMAIL");
				node.setVALUE(objectToStr(paramMap.get("EMAIL")));
				list1.add(node);
			}
			String temp = "<COMMON_NODES class=\"COMMON_NODE;\" size=\""+list1.size()+"\">";
			for(int i=0;i<list1.size();i++){
				temp += list1.get(i).toString();
			}
			temp += "</COMMON_NODES></TSFSXX>";
			content += temp;
			ArrayList<ArrayList<COMMON_NODEContent>> fpxxsList = new ArrayList<ArrayList<COMMON_NODEContent>>();
			{
				if(CollectionUtil.isNotEmpty(invoiceList)){
					for (Map<String, Object> fplsMap : invoiceList) {
						ArrayList<COMMON_NODEContent> list2 = new ArrayList<COMMON_NODEContent>();
						{
							COMMON_NODEContent node = new COMMON_NODEContent();
							node.setNAME("FPQQLSH");
							node.setVALUE(objectToStr(fplsMap.get("FPQQLSH")));
							list2.add(node);
						}					
						{
							COMMON_NODEContent node = new COMMON_NODEContent();
							node.setNAME("NSRSBH");
							node.setVALUE(objectToStr(fplsMap.get("NSRSBH")));
							list2.add(node);
						}					
						{
							COMMON_NODEContent node = new COMMON_NODEContent();
							node.setNAME("FP_DM");
							node.setVALUE(objectToStr(fplsMap.get("FP_DM")));
							list2.add(node);
						}					
						{
							COMMON_NODEContent node = new COMMON_NODEContent();
							node.setNAME("FP_HM");
							node.setVALUE(objectToStr(fplsMap.get("FP_HM")));
							list2.add(node);
						}	
						fpxxsList.add(list2);
					}
				}
			}
			content += "<FPXXS class=\"FPXX;\" size=\""+fpxxsList.size()+"\">";
			for(int j=0;j<fpxxsList.size();j++){
				ArrayList<COMMON_NODEContent> tempList =  fpxxsList.get(j);
				String temp3 = "<FPXX><COMMON_NODES class=\"COMMON_NODE;\" size=\""+tempList.size()+"\">";
				for(int i=0;i<tempList.size();i++){
					temp3 += tempList.get(i).toString();
				}
				temp3 += "</COMMON_NODES></FPXX>";
				content += temp3;
			}
			content += "</FPXXS></REQUEST_EMAILPHONEFPTS>";
		}
		Map<String,String> platFormInfo = new HashMap<String,String>();
		platFormInfo.put("PLATFORM_NUMBER", objectToStr(invoiceList.get(0).get("DSPTBM")));
		platFormInfo.put("INVOICINGPART_ID", objectToStr(invoiceList.get(0).get("NSRSBH")));
		platFormInfo.put("REGISTRATION_CODE", objectToStr(invoiceList.get(0).get("REGISTRATION_CODE")));
		platFormInfo.put("AUTHORIZATION_CODE", objectToStr(invoiceList.get(0).get("AUTHORIZATION_CODE")));
		
		Map<String, Object> map = commonAisinoMessageSend(content.toString(),EMAILPHONEFPTS,platFormInfo,"EMAIL_PUSH","invoice send");
		Interface result = (Interface) map.get("RESULT_OBJ");
		ReturnStateInfo returnStateInfo = result.getReturnStateInfo();
		if("0000".equals(returnStateInfo.getReturnCode())){
			contentMap.put("suc", "true");
		}else{
			contentMap.put("suc", "false");
		}
		return contentMap;
	}
    
	public Map<String, Object> commonAisinoMessageSend(String content, String serviceName, Map<String, String> platFormInfo, String remark,String invoiceAppNo) {
        Client client = new Client();
        GlobalInfo globalInfo = new GlobalInfo();

        //终端类型标识(0:B/S 请求来源;1:C/S 请求来源)
        globalInfo.setTerminalCode("0");
        //接口编码
        globalInfo.setInterfaceCode(serviceName);
        //平台编码
        globalInfo.setUserName(platFormInfo.get("PLATFORM_NUMBER"));
  		String pass = StringUtil.toStringWithEmpty(platFormInfo.get("REGISTRATION_CODE"));
  		try {
  			String rundomStr = StringUtil.getUuid32().substring(0, 10);
  			pass = rundomStr + EncoderByMd5(rundomStr + pass);
  		} catch (NoSuchAlgorithmException e) {
  			e.printStackTrace();
  		} catch (UnsupportedEncodingException e) {
  			e.printStackTrace();
  		}
  		globalInfo.setPassWord(pass);
        //数据交换请求发出方代码
        globalInfo.setRequestCode(platFormInfo.get("PLATFORM_NUMBER"));
        //纳税人识别号
        globalInfo.setTaxpayerId(platFormInfo.get("INVOICINGPART_ID"));
        //接入系统平台授权码(由平台提供)
        globalInfo.setAuthorizationCode(platFormInfo.get("AUTHORIZATION_CODE"));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SS");
        //数据交换请求发出时间
        globalInfo.setRequestTime(df.format(new Date()));
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        //数据交换流水号
        globalInfo.setDataExchangeId(platFormInfo.get("PLATFORM_NUMBER") + df2.format(new Date()) + "000000001");
        //添加报文保存功能，请求到航信的数据
        Map<String, Object> requestInfoSave = new HashMap<String, Object>();
        requestInfoSave.put("REQUEST_OBJECT", "Aisino");//获取报文请求对象
        requestInfoSave.put("NOWTIME", StringUtil.getNowTime());//获取当前时间
        requestInfoSave.put("CONTENT", content);//请求过去的报文
        Interface result = client.getFPService(globalInfo, content);
        String returnCode = result.getReturnStateInfo().getReturnCode();
        try {
            String returnMessage = result.getReturnStateInfo().getReturnMessage();
            if (StringUtil.isNotEmpty(returnMessage)) {
                byte[] returnMessageBytes = Base64.decodeBase64(returnMessage);
                returnMessage = new String(returnMessageBytes, "utf-8");
                logger.info("返回结果码值：" + returnCode + "，返回结果描述：" + returnMessage);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        requestInfoSave.put("RESPONSE", returnCode + result.getReturnStateInfo().getReturnMessage());//请求过去的报文
        requestInfoSave.put("UPDATETIME", StringUtil.getNowTime());//获取当前时间
        requestInfoSave.put("KEY_CODE",invoiceAppNo);
        if ("0000".equals(returnCode)) {
            requestInfoSave.put("REMARK", remark + " apply success.");//请求成功
        } else {
            requestInfoSave.put("REMARK", remark + " apply failed.");//请求失败
        }
        //调用保存报文的service
  		requestInfoSave.put("method", "noteRequestLog");
      	//调用保存报文的service
      	RDPUtil.execBaseBizService("noteRequstLogService",requestInfoSave);

        //如果发票请求流水号invoiceAppNo不为空的话，则将返回的接口信息更新t_invoice_order表中
        if(StringUtil.isNotEmpty(invoiceAppNo)){
            //如果接口返回信息不正确，将错误信息更新至t_invoice_order表中，方便在功能“开票异常查询”功能中展示
            if (!"0000".equals(returnCode)) {
                Map<String,Object> param = new HashMap<String,Object>();
                param.put("requestNum",invoiceAppNo);//发票请求流水号
                param.put("returnMsg",returnCode + result.getReturnStateInfo().getReturnMessage());//接口返回信息
                param.put("method", "updateOrderResponseContext");
                RDPUtil.execBaseBizService("invoiceService",param);
            }
        }
      		
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("RESULT_CODE", "1");
        resultMap.put("RESULT_MSG", "succes");
        resultMap.put("RESULT_OBJ", result);
        resultMap.put("RETURN_CODE", result.getReturnStateInfo().getReturnCode());
        return resultMap;
    }

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
        } else if (obj instanceof Integer) {
            returnStr = obj.toString();
        }
        return returnStr;
    }

    /**利用MD5进行加密
     * @param str  待加密的字符串
     * @return  加密后的字符串
     * @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException  
     */
    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }
    
 // BASE64解密  
    public byte[] getDecodeBase64(String str) {  
		byte[] decodeBase64 = new byte[]{};
		try {
			decodeBase64 = Base64.decodeBase64(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return decodeBase64;
    }
}
