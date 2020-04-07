package com.rongzer.efapiao.model;

import java.io.Serializable;

public class TransactionItem  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5872733489149461270L;
	private String TRANSACTION_ID="";
	private String ITEM_ID="";
	private String ITEM_CODE="";
	private String ITEM_QUANTITY="";
	private String ITEM_PRICE="";
	private String ITEM_AMOUNT="";
	private String ITEM_AMOUNT_AFTER_DISCOUNT="";
	private String DISCOUNT_CODE="";
	private String PRISE_CODE="";
	private String DISCOUNT_MONEY="";
	private String ORDER_WEIGHT="";
	private String SPECIFICATION="";
	private String UNIT_CN="";
	private String UNIT_EN="";
	
	
	public String getUNIT_CN() {
		return UNIT_CN;
	}
	public void setUNIT_CN(String uNIT_CN) {
		UNIT_CN = uNIT_CN;
	}
	public String getUNIT_EN() {
		return UNIT_EN;
	}
	public void setUNIT_EN(String uNIT_EN) {
		UNIT_EN = uNIT_EN;
	}
	public String getSPECIFICATION() {
		return SPECIFICATION;
	}
	public void setSPECIFICATION(String sPECIFICATION) {
		SPECIFICATION = sPECIFICATION;
	}
	public String getITEM_PRICE() {
		return ITEM_PRICE;
	}
	public void setITEM_PRICE(String iTEM_PRICE) {
		ITEM_PRICE = iTEM_PRICE;
	}
	public String getDISCOUNT_CODE() {
		return DISCOUNT_CODE;
	}
	public void setDISCOUNT_CODE(String dISCOUNT_CODE) {
		DISCOUNT_CODE = dISCOUNT_CODE;
	}
	public String getPRISE_CODE() {
		return PRISE_CODE;
	}
	public void setPRISE_CODE(String pRISE_CODE) {
		PRISE_CODE = pRISE_CODE;
	}
	public String getDISCOUNT_MONEY() {
		return DISCOUNT_MONEY;
	}
	public void setDISCOUNT_MONEY(String dISCOUNT_MONEY) {
		DISCOUNT_MONEY = dISCOUNT_MONEY;
	}
	public String getORDER_WEIGHT() {
		return ORDER_WEIGHT;
	}
	public void setORDER_WEIGHT(String oRDER_WEIGHT) {
		ORDER_WEIGHT = oRDER_WEIGHT;
	}
	public String getTRANSACTION_ID() {
		return TRANSACTION_ID;
	}
	public void setTRANSACTION_ID(String tRANSACTION_ID) {
		TRANSACTION_ID = tRANSACTION_ID;
	}
	public String getITEM_ID() {
		return ITEM_ID;
	}
	public void setITEM_ID(String iTEM_ID) {
		ITEM_ID = iTEM_ID;
	}
	public String getITEM_CODE() {
		return ITEM_CODE;
	}
	public void setITEM_CODE(String iTEM_CODE) {
		ITEM_CODE = iTEM_CODE;
	}
	public String getITEM_QUANTITY() {
		return ITEM_QUANTITY;
	}
	public void setITEM_QUANTITY(String iTEM_QUANTITY) {
		ITEM_QUANTITY = iTEM_QUANTITY;
	}
	public String getITEM_AMOUNT() {
		return ITEM_AMOUNT;
	}
	public void setITEM_AMOUNT(String iTEM_AMOUNT) {
		ITEM_AMOUNT = iTEM_AMOUNT;
	}
	public String getITEM_AMOUNT_AFTER_DISCOUNT() {
		return ITEM_AMOUNT_AFTER_DISCOUNT;
	}
	public void setITEM_AMOUNT_AFTER_DISCOUNT(String iTEM_AMOUNT_AFTER_DISCOUNT) {
		ITEM_AMOUNT_AFTER_DISCOUNT = iTEM_AMOUNT_AFTER_DISCOUNT;
	}

}
