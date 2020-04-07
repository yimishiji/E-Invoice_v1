package com.rongzer.efapiao.task;

import com.rongzer.efapiao.dao.TaxPayerMapper;
import com.rongzer.efapiao.service.AisinoService;
import com.rongzer.efapiao.util.SendEmailUtil;
import com.rongzer.efapiao.util.XmlParser;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPBaseService;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.notification.service.utils.MailServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sajt.shdzfp.sl.model.Interface;
import sajt.shdzfp.sl.model.ZData;
import sajt.shdzfp.sl.service.Client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 * 纳税人发票库存同步
 * @author
 * @create 2017-07-12 14:36
 **/
@Service("taxPayerInvoiceInventorySynchService")
public class TaxPayerInvoiceInventorySynchService implements RDPBaseService{
    private static Logger logger = Logger.getLogger(TaxPayerInvoiceInventorySynchService.class);

    //发票库存
    private static String FPKC = "ECXML.QY.KYFPSL";

    @Autowired
    private TaxPayerMapper taxPayerMapper;

    @Autowired
    private MailServiceImpl emailService;

    @Override
    public String execute(String s) {
        List<Map<String,Object>> taxPayerList = getTaxPayerList();
        String resultlog = "taxpayerinvoiceinventory infomation update succeed.";
        if(CollectionUtil.isNotEmpty(taxPayerList)){
            for(Map<String,Object> map:taxPayerList){
                String platformNumber = StringUtil.safeToString(map.get("PLATFORM_CODE"));
                String registrationCode = StringUtil.safeToString(map.get("REGISTRATION_CODE"));
                String authorizationCode = StringUtil.safeToString(map.get("AUTHORIZATION_CODE"));
                String taxpayerIdentifyNo = StringUtil.safeToString(map.get("TAXPAYER_IDENTIFY_NO"));
                //发票阀值
                String inventoryThreshold = StringUtil.safeToString(map.get("INVENTORY_THRESHOLD"));
                String taxpayerNameCn = StringUtil.safeToString(map.get("TAXPAYER_NAME_CN"));
                String email = StringUtil.safeToString(map.get("EMAIL"));
                if("".equals(inventoryThreshold)){
                    inventoryThreshold = "0";
                }
                Map<String,String> platFormInfo = new HashMap<String,String>();
                platFormInfo.put("PLATFORM_NUMBER",platformNumber);
                platFormInfo.put("REGISTRATION_CODE",registrationCode);
                platFormInfo.put("AUTHORIZATION_CODE",authorizationCode);
                platFormInfo.put("INVOICINGPART_ID",taxpayerIdentifyNo);
                StringBuffer requestStr = new StringBuffer("<REQUEST_KYFPSL class=\"REQUEST_KYFPSL\">");
                requestStr.append("<NSRSBH>");
                requestStr.append(taxpayerIdentifyNo);
                requestStr.append("</NSRSBH>");
                requestStr.append("</REQUEST_KYFPSL>");
                AisinoService aisinoService = (AisinoService) RDPContext.getContext().getBean("aisinoService");
                Map<String,Object> responseMap = null;
                try{
                    responseMap = aisinoService.commonAisinoMessageSend(requestStr.toString(),FPKC,platFormInfo, "fapiao remain","");
                }catch (Exception e){
                    e.printStackTrace();
                    resultlog = "taxpayerinvoiceinventory infomation update failed. aisinoService.commonAisinoMessageSend failed";
                    return resultlog;
                }
                if(CollectionUtil.isNotEmpty(responseMap)) {
                    Interface result = (Interface)responseMap.get("RESULT_OBJ");
                    String resultCode = result.getReturnStateInfo().getReturnCode();
                    ZData data = result.getzData();
                    if ("0000".equals(resultCode)) {
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
                            Map<String,Object> contentMap = XmlParser.stringXml2Map(resultContent);
                            //可用发票数量
                            String kyfpsl = StringUtil.toStringWithEmpty(contentMap.get("KYFPSL"));
                            //更新tax_payer_info中的可用发票数量
                            Map<String,String> param = new HashMap<String,String>();
                            param.put("taxpayerIdentifyNo",taxpayerIdentifyNo);
                            param.put("kyfpsl",kyfpsl);
                            updateTaxPayerInvoiceInventory(param);
                            BigDecimal inventoryThresholdNum = new BigDecimal(inventoryThreshold);
                            BigDecimal kyfpslNum = new BigDecimal(kyfpsl);
                            if(kyfpslNum.compareTo(inventoryThresholdNum)!=1) {
                                //发送邮件
                                try {
                                    StringBuffer contentBuffer = new StringBuffer();
                                    contentBuffer.append("{'KYFPSL':'").append(kyfpsl).append("',");
                                    contentBuffer.append("'INVENTORY_THRESHOLD':'").append(inventoryThreshold).append("',");
                                    contentBuffer.append("'TAXPAYER_NAME_CN':'").append(taxpayerNameCn).append("'}");
                                    if(StringUtil.isNotEmpty(email)){
                                        String[] emailArr = email.split(";");
                                        for(String emailtemp:emailArr) {
                                            sendEmail(emailtemp, contentBuffer.toString());
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    resultlog = "taxpayerinvoiceinventory infomation update failed.failed taxpayerId:"+taxpayerIdentifyNo;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return resultlog.toString();
    }

    private List<Map<String, Object>> getTaxPayerList() {
        return taxPayerMapper.getTaxPayerList();
    }

    private void updateTaxPayerInvoiceInventory(Map<String,String> param) {
         taxPayerMapper.updateTaxPayerInvoiceInventory(param);
    }

    public boolean sendEmail(String toAddress, String content) {
        String tid="emailFpkysl";//邮件模板ID
        String brand="rongzer";//邮件品牌
        try {
            logger.info("开始发送邮件");
            int flag = emailService.sendMail(toAddress,"", tid, brand, content,"","true");
            if(flag!=0){
                logger.error("邮件发送失败!");
                return false;
            }
            logger.info("邮件已成功发送到   " + toAddress);
        } catch (Exception e) {
            logger.error("邮件发送失败!");
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }



}
