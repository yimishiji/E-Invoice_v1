package com.rongzer.efapiao.model;

import java.io.Serializable;

public class TransactionRelation  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2142509371590758276L;
	private String ID="";
	private String ORDER_ID="";
	private String TRANSACTION_NUMBER="";
	private String ADD_USER="";
	private String ADD_TIME="";
	private String UPDATE_USER="";
	private String UPDATE_TIME="";
	private String IS_DELETE="";
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getORDER_ID() {
		return ORDER_ID;
	}
	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}
	public String getTRANSACTION_NUMBER() {
		return TRANSACTION_NUMBER;
	}
	public void setTRANSACTION_NUMBER(String tRANSACTION_NUMBER) {
		TRANSACTION_NUMBER = tRANSACTION_NUMBER;
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
}
