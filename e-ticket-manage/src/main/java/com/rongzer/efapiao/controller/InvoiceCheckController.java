package com.rongzer.efapiao.controller;

import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.web.domain.system.LoginUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("invoiceCheck")
public class InvoiceCheckController {


    @RequestMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelView=new ModelAndView("/InvoiceCheck");
        return modelView;
    }

    @RequestMapping(value = "/getInvoices")
    @ResponseBody
    public Map<String,Object> getInvoices(HttpServletRequest request, HttpServletResponse response,String json){
        Map<String, Object> params = new HashMap<String, Object>();
        String userId = ((LoginUser) request.getSession().getAttribute("LoginUser")).getLoginUserId();
        params = JSONUtil.json2Map(json, params);
        params.put("userId",userId);
        Map<String,Object> returnMap = RDPUtil.execBaseBizService("invoiceCheckService","getInvoices",params);
        return returnMap;

    }




}
