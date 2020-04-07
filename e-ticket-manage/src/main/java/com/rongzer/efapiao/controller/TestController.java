package com.rongzer.efapiao.controller;

import com.rongzer.rdp.notification.service.utils.MailServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/4/10.
 */
@Controller
@RequestMapping("/test")
public class TestController {
    Logger log = Logger.getLogger(TestController.class);

    @Autowired
    private MailServiceImpl emailService;

    @RequestMapping(value = "/Test001",method = RequestMethod.GET)
    @ResponseBody
    public Object Test001(){
        /**
         * e.account=invoice@xsycloud.com.cn
         e.pass=IUpz1PW1Qcs960IL
         e.host=smtp.xsycloud.com.cn
         e.port=465
         e.protocol=smtp
         e.nikeName=shinho
         */
//        MailServer mailServer=new MailServer();
//        mailServer.setMail_host("smtp.xsycloud.com.cn");
//        mailServer.setMail_brand("asda");
//        mailServer.setMail_port(465);
//        mailServer.setMail_authuser("invoice@xsycloud.com.cn");
//        mailServer.setMail_authpassword("IUpz1PW1Qcs960IL");
//        De de=new De(mailServer);
//        try {
//            de.sendMail("asdas","15827145591@163.com","sadasdasdasd",null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
       sendEmail("1204728512@qq.com","{}");
        return null;

    }

    public boolean sendEmail(String toAddress, String content) {
        String tid="emailFpkysl";//邮件模板ID
        String brand="rongzer";//邮件品牌
        try {
            log.info("开始发送邮件");
            int flag = emailService.sendMail(toAddress,"",tid, brand, content,"");
            if(flag!=0){
                log.error("邮件发送失败!");
                return false;
            }
            log.info("邮件已成功发送到   " + toAddress);
        } catch (Exception e) {
            log.error("邮件发送失败!");
            log.error(e.getMessage(),e);
            return false;
        }
        return true;
    }


}
