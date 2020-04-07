package com.rongzer.efapiao.model;

import java.io.Serializable;

public class TransactionPayment  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7854830124610112659L;
	private String TRANSACTION_ID="";
	private String PAYMENT_ID="";
	private String PAYMENT_CODE="";
	private String PAYMENT_QUANTITY="";
	private String PAYMENT_AMOUNT="";
	public String getTRANSACTION_ID() {
		return TRANSACTION_ID;
	}
	public void setTRANSACTION_ID(String tRANSACTION_ID) {
		TRANSACTION_ID = tRANSACTION_ID;
	}
	public String getPAYMENT_ID() {
		return PAYMENT_ID;
	}
	public void setPAYMENT_ID(String pAYMENT_ID) {
		PAYMENT_ID = pAYMENT_ID;
	}
	public String getPAYMENT_CODE() {
		return PAYMENT_CODE;
	}
	public void setPAYMENT_CODE(String pAYMENT_CODE) {
		PAYMENT_CODE = pAYMENT_CODE;
	}
	public String getPAYMENT_QUANTITY() {
		return PAYMENT_QUANTITY;
	}
	public void setPAYMENT_QUANTITY(String pAYMENT_QUANTITY) {
		PAYMENT_QUANTITY = pAYMENT_QUANTITY;
	}
	public String getPAYMENT_AMOUNT() {
		return PAYMENT_AMOUNT;
	}
	public void setPAYMENT_AMOUNT(String pAYMENT_AMOUNT) {
		PAYMENT_AMOUNT = pAYMENT_AMOUNT;
	}
}
