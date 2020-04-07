package com.rongzer.efapiao.controller;

import com.rongzer.efapiao.service.EfapiaoInvoiceService;
import com.rongzer.efapiao.service.ManualInvoiceInfoService;
import com.rongzer.efapiao.util.ResultsUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/6.
 */
@Controller
@RequestMapping("/ticket")
public class YiMiController {

    Logger log = Logger.getLogger(YiMiController.class);

    @Autowired
    private EfapiaoInvoiceService efapiaoInvoiceService;

    @Autowired
    private ManualInvoiceInfoService manualInvoiceInfoService;


    /**
     * 一米开票接口
     * @param params
     * @return
     */
    @RequestMapping(value = "/makeOutAnInvoice", method = {RequestMethod.POST},produces = {"application/json"})
    @ResponseBody
    public Object MakeOutAnInvoice(@RequestBody JSONObject params){
        if(1==1){
            return ResultsUtil.success("该接口尚未开放！");
        }
        Map maps= null;
        try {
            maps = efapiaoInvoiceService.pushAPPOrderDetail(null);
        } catch (Exception e) {
            log.info(e);
            return ResultsUtil.error(e.getMessage());
        }
        return ResultsUtil.success(maps);

    }


    @RequestMapping(value = "/Test001",method = RequestMethod.GET)
    @ResponseBody
    public Object Test001(){
       JSONObject jsonObject=new JSONObject();
        Map<String,Object> map=new HashMap<>();
        Map<String,Object> transaction=new HashMap<>();

        Map<String,Object> details=new HashMap<>();
        Map<String,Object> fapiao_info=new HashMap<>();
        Map<String,Object> payments=new HashMap<>();


        fapiao_info.put("purchaser_name","个人");//发票抬头
        fapiao_info.put("detail_type","1");//开票内容类型 0：明细；1：分类汇总；3：预付卡
        fapiao_info.put("email","1204728512@qq.com");	//消费者邮箱 ,非必填
        fapiao_info.put("taxpayer_no","");//纳税人识别号,发票抬头为"个人"时，非必填，为企业时，必填
        fapiao_info.put("mobile","15026705343");//电话,非必填
        fapiao_info.put("address","上海嘉定区0221");//纳税人地址,非必填
        fapiao_info.put("tel","021-5836757");//电话,非必填
        fapiao_info.put("bank","招商银行");//开户行,非必填
        fapiao_info.put("account","62267559854412638");//开户行账号,非必填



        JSONArray jsonArray_details=new JSONArray();
        details.put("item_code","2001040016");	//商品编码
        details.put("item_quantity","10");//商品销售数量
        details.put("item_amount","1000");	//商品原价（汇总金额）
        details.put("item_disamount","800");//商品优惠价（汇总金额）
        details.put("item_orderWeight","0");//重量
        jsonArray_details.add(details);



        JSONArray jsonArray_payments=new JSONArray();
        payments.put("payment_code","HGZF2");//支付码
        payments.put("payment_quantity","121");
        payments.put("payment_amount","800");//支付金额
        jsonArray_payments.add(payments);



        jsonObject.put("transaction_num","A0011712200881");	//交易流水号
        jsonObject.put("transaction_time","2018-03-06 12:00:00");//交易时间
        jsonObject.put("store_no","1");//门店号
        jsonObject.put("total_amount","1000");//交易总金额

        jsonObject.put("fapiao_info",fapiao_info);
        jsonObject.put("details",jsonArray_details);
        jsonObject.put("payments",jsonArray_payments);

        map.put("transaction",jsonObject.toString());

        return ResultsUtil.success("垃圾");
    }


    /**
     * 申请红冲，确认红冲接口
     * @param
     * @return 返回处理信息
     */
    @RequestMapping(value="/invoiceRed")
    public @ResponseBody Map<String,Object> invoiceRed(String invoice_order_id,String red_status,HttpServletRequest request){
        if(1==1){
            return ResultsUtil.success("该接口尚未开放！");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", invoice_order_id);
        params.put("RED_STATUS", red_status);
        Map<String, Object> returnMap = manualInvoiceInfoService.redBufferApply(params);
        return returnMap;
    }

    public static void main(String[] args) {
        JSONObject jsonObject=new JSONObject();
        Map<String,Object> map=new HashMap<>();
        Map<String,Object> transaction=new HashMap<>();

        Map<String,Object> details=new HashMap<>();
        Map<String,Object> fapiao_info=new HashMap<>();
        Map<String,Object> payments=new HashMap<>();


        fapiao_info.put("purchaser_name","个人");//发票抬头
        fapiao_info.put("detail_type","1");//开票内容类型 0：明细；1：分类汇总；3：预付卡
        fapiao_info.put("email","1204728512@qq.com");	//消费者邮箱 ,非必填
        fapiao_info.put("taxpayer_no","");//纳税人识别号,发票抬头为"个人"时，非必填，为企业时，必填
        fapiao_info.put("mobile","15026705343");//电话,非必填
        fapiao_info.put("address","上海嘉定区0221");//纳税人地址,非必填
        fapiao_info.put("tel","021-5836757");//电话,非必填
        fapiao_info.put("bank","招商银行");//开户行,非必填
        fapiao_info.put("account","62267559854412638");//开户行账号,非必填



        JSONArray jsonArray_details=new JSONArray();
        details.put("item_code","2001040016");	//商品编码
        details.put("item_quantity","10");//商品销售数量
        details.put("item_amount","1000");	//商品原价（汇总金额）
        details.put("item_disamount","800");//商品优惠价（汇总金额）
        details.put("item_orderWeight","0");//重量
        jsonArray_details.add(details);



        JSONArray jsonArray_payments=new JSONArray();
        payments.put("payment_code","HGZF2");//支付码
        payments.put("payment_quantity","121");
        payments.put("payment_amount","800");//支付金额
        jsonArray_payments.add(payments);



        jsonObject.put("transaction_num","A0011712200881");	//交易流水号
        jsonObject.put("transaction_time","2018-03-06 12:00:00");//交易时间
        jsonObject.put("store_no","1");//门店号
        jsonObject.put("total_amount","1000");//交易总金额

        jsonObject.put("fapiao_info",fapiao_info);
        jsonObject.put("details",jsonArray_details);
        jsonObject.put("payments",jsonArray_payments);
        map.put("transaction",jsonObject.toString());
        System.out.println(jsonObject.toString());
    }



}
