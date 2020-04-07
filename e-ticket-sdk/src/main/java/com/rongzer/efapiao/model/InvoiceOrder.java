package com.rongzer.efapiao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sajt.shdzfp.sl.model.FPKJXX_FPTXXContent;

public class InvoiceOrder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3350583007049040156L;
	private String ORDER_ID = "";
	private String INVOICE_STATUS = "";
	private String INVOICE_TYPE = "";
	private String PARENT_ORDER_ID = "";
	private String IS_MANUAL = "";
	private String PURCHASER_NAME = "";
	private String PURCHASER_EMAIL = "";
	private String PURCHASER_ID = "";
	private String PURCHASER_MOBILE = "";
	private String PURCHASER_ADDRESS = "";
	private String PURCHASER_TEL = "";
	private String PURCHASER_BANK_ACCOUNT = "";
	private String ADD_USER = "";
	private String ADD_TIME = "";
	private String UPDATE_USER = "";
	private String UPDATE_TIME = "";
	private String IS_DELETE = "";
	private String TAXPAYER_ID = "";
	private String INVOICE_DETAIL_TYPE = "";
	private String INVOICE_WRITE_TYPE = "";
	private String INVOICE_TRADE_TYPE = "";
	private String TOTAL_AMOUNT = "";
	private String STATUS = "";//审核状态
	private List<TransactionRelation> relationShip = new ArrayList<TransactionRelation>();
	private List<FPKJXX_FPTXXContent> invoiceList = new ArrayList<FPKJXX_FPTXXContent>();
	//交易内容是否为预付卡,该字段不做数据库存储,只用于画面逻辑判断
	private String ISCARD = "flase";
	
	public List<FPKJXX_FPTXXContent> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<FPKJXX_FPTXXContent> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public String getTOTAL_AMOUNT() {
		return TOTAL_AMOUNT;
	}

	public void setTOTAL_AMOUNT(String tOTAL_AMOUNT) {
		TOTAL_AMOUNT = tOTAL_AMOUNT;
	}

	public List<TransactionRelation> getRelationShip() {
		return relationShip;
	}

	public void setRelationShip(List<TransactionRelation> relationShip) {
		this.relationShip = relationShip;
	}

	public String getORDER_ID() {
		return ORDER_ID;
	}

	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}

	public String getINVOICE_STATUS() {
		return INVOICE_STATUS;
	}

	public void setINVOICE_STATUS(String iNVOICE_STATUS) {
		INVOICE_STATUS = iNVOICE_STATUS;
	}

	public String getINVOICE_TYPE() {
		return INVOICE_TYPE;
	}

	public void setINVOICE_TYPE(String iNVOICE_TYPE) {
		INVOICE_TYPE = iNVOICE_TYPE;
	}

	public String getPARENT_ORDER_ID() {
		return PARENT_ORDER_ID;
	}

	public void setPARENT_ORDER_ID(String pARENT_ORDER_ID) {
		PARENT_ORDER_ID = pARENT_ORDER_ID;
	}

	public String getIS_MANUAL() {
		return IS_MANUAL;
	}

	public void setIS_MANUAL(String iS_MANUAL) {
		IS_MANUAL = iS_MANUAL;
	}

	public String getPURCHASER_NAME() {
		return PURCHASER_NAME;
	}

	public void setPURCHASER_NAME(String pURCHASER_NAME) {
		PURCHASER_NAME = pURCHASER_NAME;
	}

	public String getPURCHASER_EMAIL() {
		return PURCHASER_EMAIL;
	}

	public void setPURCHASER_EMAIL(String pURCHASER_EMAIL) {
		PURCHASER_EMAIL = pURCHASER_EMAIL;
	}

	public String getPURCHASER_ID() {
		return PURCHASER_ID;
	}

	public void setPURCHASER_ID(String pURCHASER_ID) {
		PURCHASER_ID = pURCHASER_ID;
	}

	public String getPURCHASER_MOBILE() {
		return PURCHASER_MOBILE;
	}

	public void setPURCHASER_MOBILE(String pURCHASER_MOBILE) {
		PURCHASER_MOBILE = pURCHASER_MOBILE;
	}

	public String getPURCHASER_ADDRESS() {
		return PURCHASER_ADDRESS;
	}

	public void setPURCHASER_ADDRESS(String pURCHASER_ADDRESS) {
		PURCHASER_ADDRESS = pURCHASER_ADDRESS;
	}

	public String getPURCHASER_TEL() {
		return PURCHASER_TEL;
	}

	public void setPURCHASER_TEL(String pURCHASER_TEL) {
		PURCHASER_TEL = pURCHASER_TEL;
	}

	public String getPURCHASER_BANK_ACCOUNT() {
		return PURCHASER_BANK_ACCOUNT;
	}

	public void setPURCHASER_BANK_ACCOUNT(String pURCHASER_BANK_ACCOUNT) {
		PURCHASER_BANK_ACCOUNT = pURCHASER_BANK_ACCOUNT;
	}

	public String getADD_USER() {
		return ADD_USER;
	}

	public void setADD_USER(String aDD_USER) {
		ADD_USER = aDD_USER;
	}

	public String getADD_TIME() {
		return ADD_TIME;
	}

	public void setADD_TIME(String aDD_TIME) {
		ADD_TIME = aDD_TIME;
	}

	public String getUPDATE_USER() {
		return UPDATE_USER;
	}

	public void setUPDATE_USER(String uPDATE_USER) {
		UPDATE_USER = uPDATE_USER;
	}

	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}

	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}

	public String getIS_DELETE() {
		return IS_DELETE;
	}

	public void setIS_DELETE(String iS_DELETE) {
		IS_DELETE = iS_DELETE;
	}

	public String getTAXPAYER_ID() {
		return TAXPAYER_ID;
	}

	public void setTAXPAYER_ID(String tAXPAYER_ID) {
		TAXPAYER_ID = tAXPAYER_ID;
	}

	public String getINVOICE_DETAIL_TYPE() {
		return INVOICE_DETAIL_TYPE;
	}

	public void setINVOICE_DETAIL_TYPE(String iNVOICE_DETAIL_TYPE) {
		INVOICE_DETAIL_TYPE = iNVOICE_DETAIL_TYPE;
	}

	public String getINVOICE_WRITE_TYPE() {
		return INVOICE_WRITE_TYPE;
	}

	public void setINVOICE_WRITE_TYPE(String iNVOICE_WRITE_TYPE) {
		INVOICE_WRITE_TYPE = iNVOICE_WRITE_TYPE;
	}

	public String getINVOICE_TRADE_TYPE() {
		return INVOICE_TRADE_TYPE;
	}

	public void setINVOICE_TRADE_TYPE(String iNVOICE_TRADE_TYPE) {
		INVOICE_TRADE_TYPE = iNVOICE_TRADE_TYPE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String STATUS) {
		this.STATUS = STATUS;
	}

	public String getISCARD() {
		for(TransactionRelation transactionRelation : relationShip){
			if(transactionRelation.getTRANSACTION_NUMBER().contentEquals("XS") || transactionRelation.getTRANSACTION_NUMBER().contentEquals("CZ")){
				ISCARD = "true";
				break;
			}
		}
		return ISCARD;
	}

	public void setISCARD(String iSCARD) {
		ISCARD = iSCARD;
	}
}
