package com.rongzer.efapiao.constant;

/**
 * Created by he.bing on 2017/1/24.
 */
public interface EfapiaoConstant {
	class efapiaoEnvi{
		public static final String PRD = "PRD";
		public static final String STG = "STG";
	}
	
	class PageStatus {
		// 扫码页面接收交易数据成功
		public static final String TRANSACTION_SUCCESS = "E01101";
		// 扫码页面接收交易数据失败
		public static final String TRANSACTION_ERROR = "E01102";
		// 扫码页面交易数据为空
		public static final String TRANSACTION_EMPTY = "E01103";
		// appInfo校验成功
		public static final String APP_SUCCESS = "E01201";
		// appInfo校验失败
		public static final String APP_ERROR = "E01202";

	}

	class CacheKey {
		// 订单缓存键值
		public static final String ORDER_KEY = "_ORDER_KEY";
		
		public static final String INVOICE_CONTENT = "INVOICE_CONTENT";
		
		public static final String PAYMENT = "PAYMENT";
		
		public static final String DISCOUNT = "DISCOUNT";
		
		public static final String GOODS_INFO = "GOODS_INFO";

		public static final String TAXPAYER_INFO = "TAXPAYER_INFO";
		
		public static final String STORE_INFO = "STORE_INFO";

		public static final String GROUP_INFO = "GROUP_INFO";

		public static final String RELATION_KEY = "_RELATION_KEY";

		public static final String SESSION_PICKUPCODE_LIST = "_SESSION_PICKUPCODE_LIST";

		public static final String SESSION_LATEST_PICKUP = "SESSION_LATEST_PICKUP";
	}

	class InvoiceStatus {
		// 未开票
		public static final String NO_INVOICE = "E00501";
		// 开票中
		public static final String IN_INVOICE = "E00502";
		// 下载中
		public static final String DOWNLOADING_INVOICE = "E00503";
		// 开票失败
		public static final String ERROR_INVOICE = "E00504";
		// 开票成功
		public static final String SUCESS_INVOICE = "E00505";
		//0元交易,可开票金额为0
		public static final String ZERO_INVOICE = "E00506";
	}

	class LyfenWsdlInfo {
		// 用户名
		public static final String USER_NAME = "rfc_user";
		// 密码
		public static final String PASS_WORD = "password";
		// 线下店
		public static final String PHYSICAL_STORE = "E00702";
		// 线上店
		public static final String VIRTUAL_STORE = "E00701";
	}

	class DefaultKey {
		// 是
		public static final String TRUE = "E00102";
		// 否
		public static final String FALSE = "E00101";
		// 默认开票元
		public static final String ISSUE_DEFAULT = "0001";
		//门店允许开票
		public static final String STORE_ALLOWED_INVOICE = "E01101";
		//官网店号
		public static final String STORE_NO_OFFICIAL = "IF10";
		//第三方电商店号
		public static final String STORE_NO_THIRD = "IF11";
	}

	class InvoiceType {
		// 正票
		public static final String DEFAULT = "E00601";
		// 红票
		public static final String OFF_SET = "E00602";
		// 个人
		public static final String INVOICE_PERSONAL = "E00801";
		// 企业
		public static final String INVOICE_ENTERPRISE = "E00802";
		// 食品
		public static final String INVOICE_CONTENT = "E00901";
		// 分类
		public static final String INVOICE_GROUP = "E00902";
		// 明细
		public static final String INVOICE_DETAIL = "E00903";
		//预付卡
		public static final String INVOICE_CARD = "E00904";
		// 交易类型线上订单(官网)
		public static final String INVOICE_ONLINE = "E01001";
		// 交易类型线上订单(电商)
		public static final String INVOICE_ONLINE_THIRDPART = "E01003";
		// 交易类型线下订单
		public static final String INVOICE_OFFLINE = "E01002";
	}
}
