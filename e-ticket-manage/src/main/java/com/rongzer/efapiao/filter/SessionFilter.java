package com.rongzer.efapiao.filter;


import com.rongzer.rdp.auth.session.CookieHelper;
import com.rongzer.rdp.auth.session.HttpServletRequestWrapper;
import com.rongzer.rdp.common.util.StringUtil;
import com.rongzer.rdp.web.domain.system.LoginUser;
import com.rongzer.rdp.web.domain.system.OnlineUserMap;
import com.wabacus.config.Config;
import org.apache.log4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter extends OncePerRequestFilter {
    public SessionFilter() {
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String[] notFilter = new String[]{Config.webroot + "login.htm", Config.webroot + "index.htm",Config.webroot + "ticket/makeOutAnInvoice.htm",
                Config.webroot + "ticket/invoiceRed.htm",Config.webroot + "test/Test001.htm"};
        String cookieId = "__RDP_sessionId";
        String cookieValue = CookieHelper.getCookieValue(request, cookieId);
        if(StringUtil.isEmpty(cookieValue)) {
            cookieValue = StringUtil.getUuid32();
            CookieHelper.setCookie(request, response, cookieId, cookieValue, -1);
        }

        if(request.getRequestURL().indexOf("/cms/") <= 0 && request.getRequestURL().indexOf("/ec/") <= 0 && request.getRequestURL().indexOf("Pay-Notify") <= 0 && request.getRequestURL().indexOf("Pay-Return") <= 0 && request.getRequestURL().indexOf("Pay-RefundNotify") <= 0 && request.getRequestURL().indexOf("/goods/") <= 0 && request.getRequestURL().indexOf("/store/") <= 0 && request.getRequestURL().indexOf("/showFile.htm") <= 0 && request.getRequestURL().indexOf("/showStaticFile.htm") <= 0) {
            HttpServletRequestWrapper var15 = new HttpServletRequestWrapper(cookieValue, request);
            String uri = var15.getRequestURI();
            String param = var15.getParameter("NOSESSION_PASS");
            if(uri.startsWith("rdp/business/webservice") || uri.contains("rdp/business/strWebservice")) {
                param = "1";
            }

            if(param == null || !"1".equals(param)) {
                String doFilter = var15.getHeader("Referer");
                if(doFilter != null && (doFilter.contains("?NOSESSION_PASS=1") || doFilter.contains("&NOSESSION_PASS=1"))) {
                    param = "1";
                }
            }

            boolean var16 = true;

            try {
                if(var15.getSession() != null && var15.getSession().getAttribute("__rdp_lang") != null) {
                    MDC.put("__rdp_lang", var15.getSession().getAttribute("__rdp_lang"));
                } else {
                    MDC.put("__rdp_lang", "");
                }

                if(param != null && param.equals("1")) {
                    var15.setAttribute("NOSESSION_PASS", "1");
                    filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                } else if(uri.indexOf("resource") == -1 && uri.indexOf("/wxtmpfiles/js/") == -1) {
                    String[] e = notFilter;
                    int user = notFilter.length;

                    for(int loginType = 0; loginType < user; ++loginType) {
                        String s = e[loginType];
                        if(uri.equals(s)) {
                            var16 = false;
                            break;
                        }
                    }

                    if(var16) {
                        if(var15.getSession().getAttribute("LoginUser") == null) {
                            response.sendRedirect(var15.getContextPath() + "/index.htm");
                            MDC.put("USER_ID", "");
                            MDC.put("USER_NAME", "");
                        } else {
                            HttpSession var17 = var15.getSession();
                            LoginUser var18 = (LoginUser)var17.getAttribute("LoginUser");
                            if(var18 != null) {
                                MDC.put("USER_ID", var18.getLoginUserId());
                                MDC.put("USER_NAME", var18.getLoginUserName());
                            } else {
                                var18 = new LoginUser();
                            }

                            String var19 = (String)var17.getAttribute("rdp.login.type");
                            if("single".equals(var19)) {
                                if(OnlineUserMap.isLogin(var18.getLoginUserId())) {
                                    if(var17.getId().equals(OnlineUserMap.getSessionId(var18.getLoginUserId()))) {
                                        filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                                    } else {
                                        response.sendRedirect((new HttpServletRequestWrapper(cookieValue, var15)).getContextPath() + "/index.htm");
                                    }
                                } else {
                                    filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                                }
                            } else {
                                filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                            }
                        }
                    } else {
                        filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                    }
                } else {
                    filterChain.doFilter(new HttpServletRequestWrapper(cookieValue, var15), response);
                }
            } catch (Exception var14) {
                var14.printStackTrace();
            }

            MDC.put("__rdp_lang", "");
            MDC.put("USER_ID", "");
            MDC.put("USER_NAME", "");
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

