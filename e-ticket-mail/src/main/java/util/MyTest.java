package util;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**png
 * Created by Administrator on 2018/3/6.
 */
public class MyTest {
    private static final String ENCODING = "utf-8";
    private static Template template = null;
    private static List<String> list=new ArrayList<String>();

    static{
        list.add("1204728512@qq.com");
        list.add("gauseen@163.com");
    }

    public static void main(String[] args) {
        SendingPool pool = SendingPool.getInstance();
        for(String to:list){
            pool.addThread(new Sending(to, "invoice@xsycloud.com.cn", createEmail().toString(), "D:/apiclient_cert.p12"));
        }
        pool.shutDown();
    }

    private static String createEmail() {
        template = FreemarkerUtil.getTemplate("mailTemplate.ftl");
        template.setEncoding(ENCODING);
        try {
            Map map = new HashMap();
            map.put("img_logo", "http://sit.behuntergatherer.com/mdm2/images/img/logo.png");
            map.put("img_back", "http://sit.behuntergatherer.com/mdm2/images/img/bg.jpg");
            map.put("img_fp",   "http://sit.behuntergatherer.com/mdm2/images/img/fp2.png");
            map.put("href_fp",  "http://sit.behuntergatherer.com/mdm2/images/img/abd.pdf");
            String    content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            return content;
        } catch (TemplateException e) {
          e.getMessage();
        } catch (IOException e) {
            System.out.println(e);
        }
        return "";

    }
}
