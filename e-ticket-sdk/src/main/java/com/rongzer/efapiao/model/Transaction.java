package com.rongzer.efapiao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8739838437228346611L;
	private String TRANSACTION_ID="";
	private String TRANSACTION_NUMBER="";
	private String TRANSACTION_DATETIME="";
	private String STORE_NUMBER="";
	private String STORE_NAME_CN="";
	private String STORE_NAME_EN="";
	private String POS_NAME="";
	private String TRANSACTION_AMOUNT="";
	private String EMPLOYEE_ID="";
	private String EMPLOYEE_NAME="";
	private String SERVICE_TYPE="";
	private String ADD_USER="";
	private String ADD_TIME="";
	private String UPDATE_USER="";
	private String UPDATE_TIME="";
	private String IS_DELETE="";
	private String DEAL_AMOUNT="";
	private String OPEN_ID="";
	// 初始化的发票状态
	private String INVOICE_STATUS="";
	//发票关联的订单id
	private String ORDER_ID="";
	private String TAXPAYER_ID="";
	private String INVOICE_LIMIT_AMOUNT="";	
	private String IS_MERGE="E01001";
	private String isCard = "false";
	private String IS_FORBINDDEN = "";//是否禁止开票
	private String ALLOWED_INVOICE = "E01101";//门店是否允许开票
	private String TRANSACTION_MONTH="";//订单月份

	public String getOPEN_ID() {
		return OPEN_ID;
	}

	public void setOPEN_ID(String oPEN_ID) {
		OPEN_ID = oPEN_ID;
	}

	public String getIS_FORBINDDEN() {
		return IS_FORBINDDEN;
	}

	public void setIS_FORBINDDEN(String iS_FORBINDDEN) {
		IS_FORBINDDEN = iS_FORBINDDEN;
	}

	public String getINVOICE_LIMIT_AMOUNT() {
		return INVOICE_LIMIT_AMOUNT;
	}

	public void setINVOICE_LIMIT_AMOUNT(String iNVOICE_LIMIT_AMOUNT) {
		INVOICE_LIMIT_AMOUNT = iNVOICE_LIMIT_AMOUNT;
	}

	public String getSTORE_NAME_EN() {
		return STORE_NAME_EN;
	}

	public void setSTORE_NAME_EN(String sTORE_NAME_EN) {
		STORE_NAME_EN = sTORE_NAME_EN;
	}

	public String getSTORE_NAME_CN() {
		return STORE_NAME_CN;
	}

	public void setSTORE_NAME_CN(String sTORE_NAME_CN) {
		STORE_NAME_CN = sTORE_NAME_CN;
	}

	public String getTRANSACTION_MONTH() {
		return TRANSACTION_MONTH;
	}

	public void setTRANSACTION_MONTH(String tRANSACTION_MONTH) {
		TRANSACTION_MONTH = tRANSACTION_MONTH;
	}

	public String getIsCard() {
		if(TRANSACTION_NUMBER.indexOf("XS") > -1 || TRANSACTION_NUMBER.indexOf("CZ") > -1){
			isCard = "true";
		}
		return isCard;
	}

	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}

	private List<TransactionItem> transactionItemList = new ArrayList<TransactionItem>();

	private List<TransactionPayment> transactionPaymentList = new ArrayList<TransactionPayment>();

	public String getTAXPAYER_ID() {
		return TAXPAYER_ID;
	}

	public void setTAXPAYER_ID(String tAXPAYER_ID) {
		TAXPAYER_ID = tAXPAYER_ID;
	}

	public String getINVOICE_STATUS() {
		return INVOICE_STATUS;
	}

	public void setINVOICE_STATUS(String iNVOICE_STATUS) {
		INVOICE_STATUS = iNVOICE_STATUS;
	}

	public List<TransactionItem> getTransactionItemList() {
		return transactionItemList;
	}

	public void setTransactionItemList(List<TransactionItem> transactionItemList) {
		this.transactionItemList = transactionItemList;
	}

	public List<TransactionPayment> getTransactionPaymentList() {
		return transactionPaymentList;
	}

	public void setTransactionPaymentList(
			List<TransactionPayment> transactionPaymentList) {
		this.transactionPaymentList = transactionPaymentList;
	}

	public String getTRANSACTION_ID() {
		return TRANSACTION_ID;
	}

	public void setTRANSACTION_ID(String tRANSACTION_ID) {
		TRANSACTION_ID = tRANSACTION_ID;
	}

	public String getTRANSACTION_NUMBER() {
		return TRANSACTION_NUMBER;
	}

	public void setTRANSACTION_NUMBER(String tRANSACTION_NUMBER) {
		TRANSACTION_NUMBER = tRANSACTION_NUMBER;
	}

	public String getTRANSACTION_DATETIME() {
		return TRANSACTION_DATETIME;
	}

	public void setTRANSACTION_DATETIME(String tRANSACTION_DATETIME) {
		TRANSACTION_DATETIME = tRANSACTION_DATETIME;
	}

	public String getSTORE_NUMBER() {
		return STORE_NUMBER;
	}

	public void setSTORE_NUMBER(String sTORE_NUMBER) {
		STORE_NUMBER = sTORE_NUMBER;
	}

	public String getPOS_NAME() {
		return POS_NAME;
	}

	public void setPOS_NAME(String pOS_NAME) {
		POS_NAME = pOS_NAME;
	}

	public String getTRANSACTION_AMOUNT() {
		return TRANSACTION_AMOUNT;
	}

	public void setTRANSACTION_AMOUNT(String tRANSACTION_AMOUNT) {
		TRANSACTION_AMOUNT = tRANSACTION_AMOUNT;
	}

	public String getEMPLOYEE_ID() {
		return EMPLOYEE_ID;
	}

	public void setEMPLOYEE_ID(String eMPLOYEE_ID) {
		EMPLOYEE_ID = eMPLOYEE_ID;
	}

	public String getEMPLOYEE_NAME() {
		return EMPLOYEE_NAME;
	}

	public void setEMPLOYEE_NAME(String eMPLOYEE_NAME) {
		EMPLOYEE_NAME = eMPLOYEE_NAME;
	}

	public String getSERVICE_TYPE() {
		return SERVICE_TYPE;
	}

	public void setSERVICE_TYPE(String sERVICE_TYPE) {
		SERVICE_TYPE = sERVICE_TYPE;
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

	public String getDEAL_AMOUNT() {
		return DEAL_AMOUNT;
	}

	public void setDEAL_AMOUNT(String dEAL_AMOUNT) {
		DEAL_AMOUNT = dEAL_AMOUNT;
	}

	public String getORDER_ID() {
		return ORDER_ID;
	}

	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}

	public String getIS_MERGE() {
		return IS_MERGE;
	}

	public void setIS_MERGE(String iS_MERGE) {
		IS_MERGE = iS_MERGE;
	}

	public String getALLOWED_INVOICE() {
		return ALLOWED_INVOICE;
	}

	public void setALLOWED_INVOICE(String aLLOWED_INVOICE) {
		ALLOWED_INVOICE = aLLOWED_INVOICE;
	}
}
