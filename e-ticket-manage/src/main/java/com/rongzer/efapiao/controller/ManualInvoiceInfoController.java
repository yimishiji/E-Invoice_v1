package com.rongzer.efapiao.controller;

import com.rongzer.efapiao.constant.EfapiaoConstant;
import com.rongzer.efapiao.model.Transaction;
import com.rongzer.efapiao.service.ManualInvoiceInfoService;
import com.rongzer.efapiao.util.ShinHoDataUtil;
import com.rongzer.rdp.auth.system.util.Logger;
import com.rongzer.rdp.common.context.RDPContext;
import com.rongzer.rdp.common.service.RDPUtil;
import com.rongzer.rdp.common.util.CollectionUtil;
import com.rongzer.rdp.common.util.JSONUtil;
import com.rongzer.rdp.memcached.CacheClient;
import com.rongzer.rdp.memcached.MemcachedException;
import com.rongzer.rdp.web.domain.system.LoginUser;
import com.rongzer.rdp.web.service.common.DictService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.rongzer.rdp.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 手工开票页面Controller
 *
 */
@Controller
@RequestMapping("manualInvoice")
public class ManualInvoiceInfoController {
    @Autowired
    private ManualInvoiceInfoService manualInvoiceInfoService;
    private static final Logger log =  Logger.getLogger(ManualInvoiceInfoController.class);
	/**
	 * 汉语中数字大写
	 */
	private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆",
			"伍", "陆", "柒", "捌", "玖" };
	/**
	 * 汉语中货币单位大写，这样的设计类似于占位符
	 */
	private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元",
			"拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾",
			"佰", "仟" };
	/**
	 * 特殊字符：整
	 */
	private static final String CN_FULL = "整";
	/**
	 * 特殊字符：负
	 */
	private static final String CN_NEGATIVE = "负";
	/**
	 * 金额的精度，默认值为2
	 */
	private static final int MONEY_PRECISION = 2;
	/**
	 * 特殊字符：零元整
	 */
	private static final String CN_ZEOR_FULL = "零元" + CN_FULL;
	/**
	 * 跳转到手工开票页面
	 * @return 返回手工开票页面jsp
	 */
	@RequestMapping(value="manualBilling")
	public ModelAndView manualBilling(){
		String orderId = StringUtil.getUuid32();
		//开票类型
		List invoice_types = ((DictService)RDPContext.getContext().getBean("DictService")).getDictItemInfoByDictCode("E009");
		ModelAndView modelView=new ModelAndView("/rdp/invoice/manualBilling");
		modelView.addObject("orderId",orderId);
		modelView.addObject("invoice_types",invoice_types);
		return modelView;
	}

	/**
	 * 根据提取码获取交易信息
	 * @param
	 * @return
	 */
	@RequestMapping(value="getBillingInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getBillingInfo(final String pickupCode, String orderId, String isAdd){
		Map<String, Object> mapReturn = new HashMap<String, Object>();
		String pickCode = null;//解密后的提取码
		String store_no = null;//门店编号
		String transaction_num = null;//交易流水号
		if(StringUtil.isNotEmpty(pickupCode)){
			pickCode = ShinHoDataUtil.deCode(pickupCode);//解密后的提取码
			//判断该提取码是不是可以开票
			if("0".equals(isAdd)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("PICKUPCODE", pickCode);//解密后的提取码
				params.put("extractedCode", pickupCode);//提取码
				params.put("method", "checkPickupCodeCanInvoice");
				Map<String, Object> checkMap = RDPUtil.execBaseBizService("invoiceService", params, false);
				if (CollectionUtil.isNotEmpty(checkMap)) {
					Boolean suc = (Boolean) checkMap.get("suc");
					if (!suc) {
						return checkMap;
					}
				}
			}

			//缓存获取
			CacheClient cacheClient = (CacheClient) RDPContext.getContext().getBean("cacheClient");
			Map<String, Object> transactionMap = new HashMap<String,Object>();
			Transaction transaction = null;
			try {
				transaction = (Transaction) cacheClient.get(pickCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(transaction == null){
				Map<String, Object> paramMap = new HashMap<String,Object>();
				//内部获取交易信息
				paramMap.put("PICKUPCODE", pickCode);
				paramMap.put("method", "getTransaction");
				Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", paramMap);
				if(tempResult.get("transactionData") == null){
					//交易信息不存在,异步调用业务代码,获取交易信息
					paramMap.put("method", "getTransDataFromBw");
					paramMap.put("pickupCode", pickCode);
					RDPUtil.execBaseBizService("requestRemoteService", paramMap);
				}
				final String finalPickCode = pickCode;
				class Task extends TimerTask {
					private Timer timer;

					public Task(Timer timer) {
						this.timer = timer;
					}

					int i = 0;
					Boolean isActive = true;
					@Override
					public void run() {
						Map<String, Object> paramMap = new HashMap<String,Object>();
						//内部获取交易信息
						paramMap.put("PICKUPCODE", finalPickCode);
						paramMap.put("method", "getTransaction");
						Map<String, Object>  tempResult = RDPUtil.execBaseBizService("invoiceService", paramMap);
						
						Transaction transaction = (Transaction)tempResult.get("transactionData");
						if(transaction != null){
							isActive = false;
							timer.cancel();
						}
						if(i++>2){
							isActive = false;
							timer.cancel();
						}
						System.out.println("心跳获取数据执行中,提取码:"+pickupCode);
					}
				}
				Timer timer= new Timer();
				Task task = new Task(timer);
				timer.schedule(task, new Long(1000), new Long(5000));

				while(task.isActive) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				transaction = (Transaction) cacheClient.get(pickCode);
				if(transaction != null) {
					PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(transaction.getClass()).getPropertyDescriptors();
					for(int i=0;i<propertyDescriptors.length;i++){
						PropertyDescriptor pro = propertyDescriptors[i];
						Method method = pro.getReadMethod();
						Object object = method.invoke(transaction);
						transactionMap.put(pro.getName(),object);
					}
				}

				if(CollectionUtil.isNotEmpty(transactionMap)){
					//获取纳税人代码，存入缓存，用于判断是不是同一纳税人
					String taxpayerId =  (String) transactionMap.get("TAXPAYER_ID");
					//获取缓存中的该订单的纳税人
					String cachetaxpayerId = (String) cacheClient.get(orderId + "TAXPAYER_ID");
					//比较缓存中的纳税人和新加入的提取号对应的纳税人是否一致，如果不一致则说明该提取号有问题
					if(cachetaxpayerId != null && !taxpayerId.equals(cachetaxpayerId)){
						mapReturn.put("suc", false);
						mapReturn.put("msg", "inequalityNsrsbh");//当前交易数据不在同一个纳税识别号下
						return mapReturn;
					}
					cacheClient.save(orderId+"TAXPAYER_ID",taxpayerId);

					String invoiceStatus = (String) transactionMap.get("INVOICE_STATUS");
					if(!EfapiaoConstant.InvoiceStatus.NO_INVOICE.equals(invoiceStatus)){
						mapReturn.put("suc", false);
						mapReturn.put("msg", "errorInvoiceStatus");//当前提取码已开发票
						return mapReturn;
					}


					//如果是新增，将新的提取号对应的交易数据加入缓存，如果是删除，则将提取号对应的交易数据从缓存中移除
					//订单中所有的交易记录,键值：订单号
					Map<String,Map<String,Object>> order_x_trans = (Map<String, Map<String,Object>>) cacheClient.get(orderId);
					if(order_x_trans == null){
						order_x_trans = new HashMap<String,Map<String,Object>>();
					}
					Map<String,Object> tranMap = order_x_trans.get(orderId);
					if(tranMap == null){
						tranMap = new HashMap<String,Object>();
					}

					List<Map<String,Object>> transList = null;

					//0表示新增
					if("0".equals(isAdd)){
						//将当前交易记录加入订单
						tranMap.putAll(transactionMap);
						transList = (ArrayList<Map<String,Object>>)tranMap.get("transList");
						if(transList == null){
							transList = new ArrayList<Map<String,Object>>();
						}
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("pickupcpode",pickupCode);
						map.put("transactionnum",pickCode);
						map.put("amount",transactionMap.get("TRANSACTION_AMOUNT"));
						transList.add(map);
						tranMap.put("transList",transList);

						//计算订单总金额
						if(transList != null && transList.size()>0){
							BigDecimal totalAmount = new BigDecimal(0);
							for(Map<String,Object> tran:transList){
								String tranAmount = (String)tran.get("amount");
								try {
									totalAmount = totalAmount.add(StringUtil.toBigDecimal(tranAmount));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							tranMap.put("total_amout", totalAmount);
						}

						//获取销方纳税人信息
						Map<String, Object> paramMap = new HashMap<String,Object>();
						paramMap.put("TAXPAYER_ID", transaction.getTAXPAYER_ID());
						paramMap.put("method", "getTaxpayerById");
						Map<String,Object> taxpayerMap = RDPUtil.execBaseBizService("taxPayerService", paramMap,false);
						tranMap.putAll(taxpayerMap);

					}else{
						if(tranMap != null && tranMap.size()>0){
							transList = (ArrayList<Map<String,Object>>)tranMap.get("transList");
							if(transList != null && transList.size()>0) {
								for (Map<String, Object> tran : transList) {
									String trannum = (String) tran.get("transactionnum");
									if (pickCode.equals(trannum)) {
										transList.remove(tran);
										break;
									}
								}
							}
						}
					}
					//计算订单总金额
					tranMap.put("total_amout", "");
					if(transList != null && transList.size()>0){
						BigDecimal totalAmount = new BigDecimal(0);
						for(Map<String,Object> tran:transList){
							String tranAmount = (String)tran.get("amount");
							try {
								totalAmount = totalAmount.add(StringUtil.toBigDecimal(tranAmount));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						tranMap.put("total_amout", totalAmount);
					}
					order_x_trans.put(orderId,tranMap);
					cacheClient.save(orderId,order_x_trans);

					mapReturn.put("data", tranMap);
					mapReturn.put("suc", true);
				}else{
					mapReturn.put("suc", false);
					mapReturn.put("msg", "noPosData");//未获取到交易数据
					return mapReturn;
				}
			} catch (MemcachedException e) {
				e.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
			}
		}else{
			mapReturn.put("suc", false);
			mapReturn.put("msg", "pickCodeEmpty");// 提取码错误
		}
		return mapReturn;
	}

	/**
	 * 处理手工开票填写信息
	 * @param data 前端拼接的页面所有数据信息
	 * @return 返回处理信息
	 */
	@RequestMapping(value="submitManualBilling")
	public @ResponseBody Map<String,Object> submitManualBilling(String data,HttpServletRequest request){
		//解析-封装
		Map<String, String> dataMap = JSONUtil.json2Map(data);
		if(CollectionUtil.isNotEmpty(dataMap)){
			String userId = ((LoginUser) request.getSession().getAttribute("LoginUser")).getLoginUserId();
			ManualInvoiceInfoService manualInvoiceInfoService = (ManualInvoiceInfoService) RDPContext.getContext().getBean("manualInvoiceInfoService");
			manualInvoiceInfoService.saveManualInvoice(dataMap,userId);
		}
		return null;
	}
	
	
	/**
	 * 处理手工开票填写信息
	 * @param 前端拼接的页面所有数据信息
	 * @return 返回处理信息
	 */
	@RequestMapping(value="submitInvoiceApproval")
	public @ResponseBody Map<String,Object> submitInvoiceApproval(String ORDER_ID,String RED_STATUS,HttpServletRequest request){
		String userId = ((LoginUser) request.getSession().getAttribute("LoginUser")).getLoginUserId();
		//解析-封装
		Map<String, Object> parmams = new HashMap<String,Object>();
		parmams.put("ORDER_ID", ORDER_ID);
		parmams.put("RED_STATUS", RED_STATUS);
		parmams.put("USER_ID", userId);
		ManualInvoiceInfoService manualInvoiceInfoService = (ManualInvoiceInfoService) RDPContext.getContext().getBean("manualInvoiceInfoService");
		Map<String, Object> returnMap = manualInvoiceInfoService.redBufferApply(parmams);
		return returnMap;
	}
	/**
	 * 审核手工开票填写信息
	 * @param
	 * @return 返回处理信息
	 */
	@RequestMapping(value="manualInvoiceApproval")
	@ResponseBody
	public String manualInvoiceApproval(String orderId,String status,HttpServletRequest request){
		String resultStr = "操作成功!";
		try {
			ManualInvoiceInfoService manualInvoiceInfoService = (ManualInvoiceInfoService) RDPContext.getContext().getBean("manualInvoiceInfoService");
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("ORDER_ID", orderId);
			params.put("STATUS", status);
			//如果审核不通过，则修改订单状态
			if("DV0103".equals(status)){
				manualInvoiceInfoService.updateOrderStatus(params);
			}else{//审核通过，则开发票
				Map<String,Object> orderMap =manualInvoiceInfoService.manualInvoiceApproval(params);
				if(orderMap!=null && !(Boolean)orderMap.get("success")){
					params.put("STATUS", "DV0103");
					manualInvoiceInfoService.updateOrderStatus(params);
					resultStr = orderMap.get("message").toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultStr = "操作失败!";
		}
		return resultStr;
	}


    /**
     * 批量导入发票数据
     * @param file
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "insertInvoiceData", method = { RequestMethod.POST,
            RequestMethod.GET })
    public String importData(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, ModelMap model) {
        String fileName = file.getOriginalFilename();//本次上传的文件名称
        String path1 = request.getSession().getServletContext().getRealPath("/jsp");
        File targetFile = new File(path1, fileName);
        String errorInfo ="";
        String userId = ((LoginUser) request.getSession().getAttribute("LoginUser")).getLoginUserId();
        try {
            file.transferTo(targetFile);
            String path = targetFile.getPath();
            fileName = StringUtil.getUuid32()+fileName;
            errorInfo=manualInvoiceInfoService.readExcel(path,fileName,userId);

            model.put("file", fileName);
            if (StringUtil.isEmpty(errorInfo)){
                targetFile.delete();
                errorInfo = " 数据读取成功 ！请关闭弹出层";
                model.put("flag", errorInfo);
                log.info(errorInfo);
            } else {
                targetFile.delete();
                model.put("wrong", errorInfo);
                log.info(errorInfo);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            errorInfo = "请检查导入文件否正确  ？！";
            model.put("error", errorInfo);
            log.error(errorInfo);
        }catch (Exception e) {
            e.printStackTrace();
            errorInfo = " 请检查导入文件是否正确 ！";
            model.put("error", errorInfo);
            log.error(errorInfo);
        }
        model.put("resultinfo",errorInfo);
        return "rdp/invoice/invoice-manual-import";
    }

	/**
	 * 把输入的金额转换为汉语中人民币的大写
	 * @param numberOfMoney 输入的金额
	 * @return 对应的汉语大写
	 */
	public static String number2CNMontrayUnit(BigDecimal numberOfMoney) {
		StringBuffer sb = new StringBuffer();
		// -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
		// positive.
		int signum = numberOfMoney.signum();
		// 零元整的情况
		if (signum == 0) {
			return CN_ZEOR_FULL;
		}
		//这里会进行金额的四舍五入
		long number = numberOfMoney.movePointRight(MONEY_PRECISION)
				.setScale(0, 4).abs().longValue();
		// 得到小数点后两位值
		long scale = number % 100;
		int numUnit = 0;
		int numIndex = 0;
		boolean getZero = false;
		// 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
		if (!(scale > 0)) {
			numIndex = 2;
			number = number / 100;
			getZero = true;
		}
		if ((scale > 0) && (!(scale % 10 > 0))) {
			numIndex = 1;
			number = number / 10;
			getZero = true;
		}
		int zeroSize = 0;
		while (true) {
			if (number <= 0) {
				break;
			}
			// 每次获取到最后一个数
			numUnit = (int) (number % 10);
			if (numUnit > 0) {
				if ((numIndex == 9) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
				}
				if ((numIndex == 13) && (zeroSize >= 3)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
				}
				sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				getZero = false;
				zeroSize = 0;
			} else {
				++zeroSize;
				if (!(getZero)) {
					sb.insert(0, CN_UPPER_NUMBER[numUnit]);
				}
				if (numIndex == 2) {
					if (number > 0) {
						sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
					}
				} else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
					sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
				}
				getZero = true;
			}
			// 让number每次都去掉最后一个数
			number = number / 10;
			++numIndex;
		}
		// 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
		if (signum == -1) {
			sb.insert(0, CN_NEGATIVE);
		}
		// 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
		if (!(scale > 0)) {
			sb.append(CN_FULL);
		}
		return sb.toString();
	}

	/**
	 * 新增或者编辑提取码禁止
	 * @param request
	 * @return
	 */
	@RequestMapping(value="showExtractedForbiddenEdit")
	public ModelAndView showExtractedForbiddenEdit(HttpServletRequest request){
		String extractedCode = request.getParameter("EXTRACTED_CODE");
		//如果提取码不为空，则根据提取码获取提取码对应的交易信息
		if(StringUtil.isNotEmpty(extractedCode)){
			String transNum = ShinHoDataUtil.deCode(extractedCode);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("transNum",transNum);
			Map<String,Object> map = manualInvoiceInfoService.getTransInfoByTransNum(param);
			map.put("EXTRACTED_CODE",extractedCode);
			return new ModelAndView("/rdp/invoice/extractedCode-forbidden",map);
		}
		return new ModelAndView("/rdp/invoice/extractedCode-forbidden");
	}

	/**
	 * 校验提取码是否能被禁用
	 * 根据交易信息表里是否有交易数据来判定的
	 * @return
	 */
	@RequestMapping(value="checkExtractedCodeCanForbidden")
	@ResponseBody
	public Map<String,Object> checkExtractedCodeCanForbidden(HttpServletRequest request){
		Map<String,Object> param = new HashMap<String,Object>();
		String extractedCode = StringUtil.safeToString(request.getParameter("extractedCode"));
		param.put("extractedCode",extractedCode);
		//如果提取码不为空，则根据提取码获取提取码对应的交易信息
		return	manualInvoiceInfoService.checkExtractedCodeCanForbidden(param);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value="forbiddenExtractedCode")
	@ResponseBody
	public Map<String,Object> forbiddenExtractedCode(HttpServletRequest request){
		Map<String,Object> param = new HashMap<String,Object>();
		String extractedCode = StringUtil.safeToString(request.getParameter("extractedCode"));
		param.put("extractedCode",extractedCode);
		param.put("NOWTIME", StringUtil.getNowTime());
		LoginUser user = (LoginUser)request.getSession().getAttribute("LoginUser");
		param.put("userId",user.getLoginUserId());
		return	manualInvoiceInfoService.forbiddenExtractedCode(param);
	}
}
