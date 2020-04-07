//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rongzer.ecservice.common;

import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPUtil;
import freemarker.template.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public class ECContext extends RDPContext {
    private static String runtype = null;

    public ECContext() {
    }

    public static Configuration getFtlCfg() {
      ApplicationContext context= getContext();
        System.out.println(context);
        FreeMarkerConfigurer freeMarkerConfigurer = (FreeMarkerConfigurer)getContext().getBean("ECFtlConfigurer");
        Configuration ftlCfg = freeMarkerConfigurer.getConfiguration();
        return ftlCfg;
    }

    public static boolean isDevelop() {
        return RDPUtil.isDevelop();
    }
}
