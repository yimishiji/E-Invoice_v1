package com.rongzer.efapiao.util;

import com.rongzer.rdp.notification.domain.MailServer;
import com.rongzer.rdp.notification.service.utils.MailSendUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import net.sf.json.JSONArray;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;

/**
 * Created by Administrator on 2018/3/6.
 */
public class SendEmailUtil {


    private static String account;    //登录用户名
    private static String pass;        //登录密码
    private static String host;        //服务器地址（邮件服务器）
    private static String port;        //端口
    private static String protocol; //协议

    static {
        Properties prop = new Properties();
//        InputStream instream = ClassLoader.getSystemResourceAsStream("email.properties");//测试环境
        try {
//            prop.load(instream);//测试环境

            prop = PropertiesLoaderUtils.loadAllProperties("email.properties");//生产环境
        } catch (IOException e) {
            System.out.println("加载属性文件失败");
        }
        account = prop.getProperty("e.account");
        pass = prop.getProperty("e.pass");
        host = prop.getProperty("e.host");
        port = prop.getProperty("e.port");
        protocol = prop.getProperty("e.protocol");
    }

    static class MyAuthenricator extends Authenticator {
        String u = null;
        String p = null;

        public MyAuthenricator(String u, String p) {
            this.u = u;
            this.p = p;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(u, p);
        }
    }

    private String to;    //收件人
    private String subject;    //主题
    private String content;    //内容
    private String fileStr;    //附件路径

    public SendEmailUtil(String to, String subject, String content, String fileStr) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.fileStr = fileStr;
    }


   void send2(){

       MailServer mailServer=new MailServer();
       mailServer.setMail_host(host);
       mailServer.setMail_authpassword(pass);
       mailServer.setMail_authuser(account);
       mailServer.setMail_port(Integer.parseInt(port));
       mailServer.setMail_brand("xinhe");
       MailSendUtil mailSendUtil=new MailSendUtil(mailServer);
       try {
           new HashMap();
           JSONArray jsonArray = null;
           if(fileStr != null && !"".equals(fileStr)) {
              // JSONObject mailMap = JSONObject.fromObject(fileStr);
               //jsonArray = mailMap.getJSONArray("fileList");
           }
           mailSendUtil.sendMail(subject,to,"emailFpkysl","shinho",null,true);
       } catch (Exception e) {
           System.out.println(e);
           e.printStackTrace();
       }
   }


    public void send() {
        Properties prop = new Properties();
        //协议
        prop.setProperty("mail.transport.protocol", protocol);
        //服务器
        prop.setProperty("mail.smtp.host", host);
        //端口
        prop.setProperty("mail.smtp.port", port);
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(prop, new MyAuthenricator(account, pass));
        session.setDebug(true);
        try {
           // InputStream in= new FileInputStream(fileStr);

            MimeMessage mimeMessage = new MimeMessage(session);

            //发件人
            mimeMessage.setFrom(new InternetAddress(account, "XXX"));        //可以设置发件人的别名
            //mimeMessage.setFrom(new InternetAddress(account));    //如果不需要就省略
            //收件人
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //主题
            mimeMessage.setSubject(subject);
            //时间
            mimeMessage.setSentDate(new Date());
            //容器类，可以包含多个MimeBodyPart对象
            Multipart mp = new MimeMultipart();

            //MimeBodyPart可以包装文本，图片，附件
            MimeBodyPart body = new MimeBodyPart();
            //HTML正文
            body.setContent(content, "text/html; charset=UTF-8");
            mp.addBodyPart(body);

            //添加图片&附件
            body = new MimeBodyPart();
            if(fileStr!=null){
                body.setFileName(fileStr);
            }

            mp.addBodyPart(body);

            //设置邮件内容
            mimeMessage.setContent(mp);
            //仅仅发送文本
            //mimeMessage.setText(content);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
