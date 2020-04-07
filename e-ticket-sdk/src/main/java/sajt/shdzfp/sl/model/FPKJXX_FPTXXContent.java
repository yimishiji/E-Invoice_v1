package sajt.shdzfp.sl.model;

import com.rongzer.efapiao.util.ShinHoDataUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FPKJXX_FPTXXContent implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8066871214977138698L;
	private String FPQQLSH = "";
	private String DSPTBM = "";
	private String NSRSBH = "";
	private String NSRMC = "";
	private String NSRDZDAH = "";
	private String SWJG_DM = "";
	private String DKBZ = "0";
	private String PYDM = "";
	private String KPXM = "";
	private String BMB_BBH = "19.0";
	private String XHF_NSRSBH = "";
	private String XHFMC = "";
	private String XHF_DZ = "";
	private String XHF_DH = "";
	private String XHF_YHZH = "";
	private String GHFMC = "";
	private String GHF_NSRSBH = "";
	private String GHF_DH = "";
	private String GHF_SF = "";
	private String GHF_DZ = "";
	private String GHF_GDDH = "";
	private String GHF_SJ = "";
	private String GHF_EMAIL = "";
	private String GHFQYLX = "03";
	private String GHF_YHZH = "";
	private String HY_DM = "";
	private String HY_MC = "";
	private String KPY = "";
	private String SKY = "";
	private String FHR = "";
	private String KPRQ = "";
	private String KPLX = "1";
	private String YFP_DM = "";
	private String YFP_HM = "";
	private String CZDM = "10";
	private String QD_BZ = "0";
	private String QDXMMC = "";
	private String CHYY = "";
	private String TSCHBZ = "0";
	private String KPHJJE = "";
	private String HJBHSJE = "0";
	private String HJSE = "0";
	private String BZ = "";
	private String BYZD1 = "";
	private String BYZD2 = "";
	private String BYZD3 = "";
	private String BYZD4 = "";
	private String BYZD5 = "";
	private String INVOICE_LIMIT_AMOUNT = "";
	private String REGISTRATION_CODE = "";
	private String ORDER_ID = "";
	private String AUTHORIZATION_CODE = "";
	private String INVOICE_ID = "";
	private String NOWTIME = "";
	private String INVOICE_TIME = "";
	private String BILLING_DATE = "";
	private String INVOICE_NUMBER = "";
	private String INVOICE_CODE = "";
	private String INVOICE_URL = "";
	private String PDF_FILE = "";
	
	
	public String getBILLING_DATE() {
		return BILLING_DATE;
	}

	public void setBILLING_DATE(String bILLING_DATE) {
		BILLING_DATE = bILLING_DATE;
	}

	public String getGHF_DH() {
		return GHF_DH;
	}

	public void setGHF_DH(String gHF_DH) {
		GHF_DH = gHF_DH;
	}

	public String getPDF_FILE() {
		return PDF_FILE;
	}

	public void setPDF_FILE(String pDF_FILE) {
		PDF_FILE = pDF_FILE;
	}

	public String getINVOICE_NUMBER() {
		return INVOICE_NUMBER;
	}

	public void setINVOICE_NUMBER(String iNVOICE_NUMBER) {
		INVOICE_NUMBER = iNVOICE_NUMBER;
	}

	public String getINVOICE_CODE() {
		return INVOICE_CODE;
	}

	public void setINVOICE_CODE(String iNVOICE_CODE) {
		INVOICE_CODE = iNVOICE_CODE;
	}

	public String getINVOICE_URL() {
		return INVOICE_URL;
	}

	public void setINVOICE_URL(String iNVOICE_URL) {
		INVOICE_URL = iNVOICE_URL;
	}

	public String getINVOICE_ID() {
		return INVOICE_ID;
	}

	public void setINVOICE_ID(String iNVOICE_ID) {
		INVOICE_ID = iNVOICE_ID;
	}

	public String getINVOICE_TIME() {
		return INVOICE_TIME;
	}

	public void setINVOICE_TIME(String iNVOICE_TIME) {
		INVOICE_TIME = iNVOICE_TIME;
	}

	public String getNOWTIME() {
		return NOWTIME;
	}

	public void setNOWTIME(String nOWTIME) {
		NOWTIME = nOWTIME;
	}

	private List<FPKJXX_XMXXContent> fpmxList = new ArrayList<FPKJXX_XMXXContent>();

	public String getORDER_ID() {
		return ORDER_ID;
	}

	public void setORDER_ID(String oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}

	public String getREGISTRATION_CODE() {
		return REGISTRATION_CODE;
	}

	public void setREGISTRATION_CODE(String rEGISTRATION_CODE) {
		REGISTRATION_CODE = rEGISTRATION_CODE;
	}

	public String getAUTHORIZATION_CODE() {
		return AUTHORIZATION_CODE;
	}

	public void setAUTHORIZATION_CODE(String aUTHORIZATION_CODE) {
		AUTHORIZATION_CODE = aUTHORIZATION_CODE;
	}

	public String getINVOICE_LIMIT_AMOUNT() {
		return INVOICE_LIMIT_AMOUNT;
	}

	public void setINVOICE_LIMIT_AMOUNT(String iNVOICE_LIMIT_AMOUNT) {
		INVOICE_LIMIT_AMOUNT = iNVOICE_LIMIT_AMOUNT;
	}

	public String getFPQQLSH() {
		return FPQQLSH;
	}

	public void setFPQQLSH(String fPQQLSH) {
		FPQQLSH = fPQQLSH;
	}

	public List<FPKJXX_XMXXContent> getFpmxList() {
		return fpmxList;
	}

	public void setFpmxList(List<FPKJXX_XMXXContent> fpmxList) {
		this.fpmxList = fpmxList;
	}

	public String getDSPTBM() {
		return DSPTBM;
	}

	public void setDSPTBM(String dSPTBM) {
		DSPTBM = dSPTBM;
	}

	public String getNSRSBH() {
		return NSRSBH;
	}

	public void setNSRSBH(String nSRSBH) {
		NSRSBH = nSRSBH;
	}

	public String getNSRMC() {
		return NSRMC;
	}

	public void setNSRMC(String nSRMC) {
		NSRMC = nSRMC;
	}

	public String getNSRDZDAH() {
		return NSRDZDAH;
	}

	public void setNSRDZDAH(String nSRDZDAH) {
		NSRDZDAH = nSRDZDAH;
	}

	public String getSWJG_DM() {
		return SWJG_DM;
	}

	public void setSWJG_DM(String sWJG_DM) {
		SWJG_DM = sWJG_DM;
	}

	public String getDKBZ() {
		return DKBZ;
	}

	public void setDKBZ(String dKBZ) {
		DKBZ = dKBZ;
	}

	public String getPYDM() {
		return PYDM;
	}

	public void setPYDM(String pYDM) {
		PYDM = pYDM;
	}

	public String getKPXM() {
		return KPXM;
	}

	public void setKPXM(String kPXM) {
		KPXM = kPXM;
	}

	public String getBMB_BBH() {
		return BMB_BBH;
	}

	public void setBMB_BBH(String bMB_BBH) {
		BMB_BBH = bMB_BBH;
	}

	public String getXHF_NSRSBH() {
		return XHF_NSRSBH;
	}

	public void setXHF_NSRSBH(String xHF_NSRSBH) {
		XHF_NSRSBH = xHF_NSRSBH;
	}

	public String getXHFMC() {
		return XHFMC;
	}

	public void setXHFMC(String xHFMC) {
		XHFMC = xHFMC;
	}

	public String getXHF_DZ() {
		return XHF_DZ;
	}

	public void setXHF_DZ(String xHF_DZ) {
		XHF_DZ = xHF_DZ;
	}

	public String getXHF_DH() {
		return XHF_DH;
	}

	public void setXHF_DH(String xHF_DH) {
		XHF_DH = xHF_DH;
	}

	public String getXHF_YHZH() {
		return XHF_YHZH;
	}

	public void setXHF_YHZH(String xHF_YHZH) {
		XHF_YHZH = xHF_YHZH;
	}

	public String getGHFMC() {
		return GHFMC;
	}

	public void setGHFMC(String gHFMC) {
		GHFMC = gHFMC;
	}

	public String getGHF_NSRSBH() {
		return GHF_NSRSBH;
	}

	public void setGHF_NSRSBH(String gHF_NSRSBH) {
		GHF_NSRSBH = gHF_NSRSBH;
	}

	public String getGHF_SF() {
		return GHF_SF;
	}

	public void setGHF_SF(String gHF_SF) {
		GHF_SF = gHF_SF;
	}

	public String getGHF_DZ() {
		return GHF_DZ;
	}

	public void setGHF_DZ(String gHF_DZ) {
		GHF_DZ = gHF_DZ;
	}

	public String getGHF_GDDH() {
		return GHF_GDDH;
	}

	public void setGHF_GDDH(String gHF_GDDH) {
		GHF_GDDH = gHF_GDDH;
	}

	public String getGHF_SJ() {
		return GHF_SJ;
	}

	public void setGHF_SJ(String gHF_SJ) {
		GHF_SJ = gHF_SJ;
	}

	public String getGHF_EMAIL() {
		return GHF_EMAIL;
	}

	public void setGHF_EMAIL(String gHF_EMAIL) {
		GHF_EMAIL = gHF_EMAIL;
	}

	public String getGHFQYLX() {
		return GHFQYLX;
	}

	public void setGHFQYLX(String gHFQYLX) {
		GHFQYLX = gHFQYLX;
	}

	public String getGHF_YHZH() {
		return GHF_YHZH;
	}

	public void setGHF_YHZH(String gHF_YHZH) {
		GHF_YHZH = gHF_YHZH;
	}

	public String getHY_DM() {
		return HY_DM;
	}

	public void setHY_DM(String hY_DM) {
		HY_DM = hY_DM;
	}

	public String getHY_MC() {
		return HY_MC;
	}

	public void setHY_MC(String hY_MC) {
		HY_MC = hY_MC;
	}

	public String getKPY() {
		return KPY;
	}

	public void setKPY(String kPY) {
		KPY = kPY;
	}

	public String getSKY() {
		return SKY;
	}

	public void setSKY(String sKY) {
		SKY = sKY;
	}

	public String getFHR() {
		return FHR;
	}

	public void setFHR(String fHR) {
		FHR = fHR;
	}

	public String getKPRQ() {
		return KPRQ;
	}

	public void setKPRQ(String kPRQ) {
		KPRQ = kPRQ;
	}

	public String getKPLX() {
		return KPLX;
	}

	public void setKPLX(String kPLX) {
		KPLX = kPLX;
	}

	public String getYFP_DM() {
		return YFP_DM;
	}

	public void setYFP_DM(String yFP_DM) {
		YFP_DM = yFP_DM;
	}

	public String getYFP_HM() {
		return YFP_HM;
	}

	public void setYFP_HM(String yFP_HM) {
		YFP_HM = yFP_HM;
	}

	public String getCZDM() {
		return CZDM;
	}

	public void setCZDM(String cZDM) {
		CZDM = cZDM;
	}

	public String getQD_BZ() {
		return QD_BZ;
	}

	public void setQD_BZ(String qD_BZ) {
		QD_BZ = qD_BZ;
	}

	public String getQDXMMC() {
		return QDXMMC;
	}

	public void setQDXMMC(String qDXMMC) {
		QDXMMC = qDXMMC;
	}

	public String getCHYY() {
		return CHYY;
	}

	public void setCHYY(String cHYY) {
		CHYY = cHYY;
	}

	public String getTSCHBZ() {
		return TSCHBZ;
	}

	public void setTSCHBZ(String tSCHBZ) {
		TSCHBZ = tSCHBZ;
	}

	public String getKPHJJE() {
		return KPHJJE;
	}

	public void setKPHJJE(String kPHJJE) {
		KPHJJE = kPHJJE;
	}

	public String getHJBHSJE() {
		return HJBHSJE;
	}

	public void setHJBHSJE(String hJBHSJE) {
		HJBHSJE = hJBHSJE;
	}

	public String getHJSE() {
		return HJSE;
	}

	public void setHJSE(String hJSE) {
		HJSE = hJSE;
	}

	public String getBZ() {
		return BZ;
	}

	public void setBZ(String bZ) {
		BZ = bZ;
	}

	public String getBYZD1() {
		return BYZD1;
	}

	public void setBYZD1(String bYZD1) {
		BYZD1 = bYZD1;
	}

	public String getBYZD2() {
		return BYZD2;
	}

	public void setBYZD2(String bYZD2) {
		BYZD2 = bYZD2;
	}

	public String getBYZD3() {
		return BYZD3;
	}

	public void setBYZD3(String bYZD3) {
		BYZD3 = bYZD3;
	}

	public String getBYZD4() {
		return BYZD4;
	}

	public void setBYZD4(String bYZD4) {
		BYZD4 = bYZD4;
	}

	public String getBYZD5() {
		return BYZD5;
	}

	public void setBYZD5(String bYZD5) {
		BYZD5 = bYZD5;
	}

	/**
	 * 取消GHF的邮箱地址
	 */
	@Override
	public String toString() {
		return "<FPKJXX_FPTXX class=\"FPKJXX_FPTXX\"><FPQQLSH>" + getFPQQLSH()
				+ "</FPQQLSH><DSPTBM>" + getDSPTBM() + "</DSPTBM><NSRSBH>"
				+ getNSRSBH() + "</NSRSBH><NSRMC>" + getNSRMC()
				+ "</NSRMC><NSRDZDAH>" + getNSRDZDAH() + "</NSRDZDAH><SWJG_DM>"
				+ getSWJG_DM() + "</SWJG_DM><DKBZ>" + getDKBZ()
				+ "</DKBZ><PYDM>" + getPYDM() + "</PYDM><KPXM>" + getKPXM()
				+ "</KPXM><BMB_BBH>" + getBMB_BBH() + "</BMB_BBH><XHF_NSRSBH>"
				+ getXHF_NSRSBH()
				+ "</XHF_NSRSBH><XHFMC>"
				+ getXHFMC()
				+ "</XHFMC><XHF_DZ>"
				+ getXHF_DZ()
				+ "</XHF_DZ><XHF_DH>"
				+ getXHF_DH()
				+ "</XHF_DH><XHF_YHZH>"
				+ getXHF_YHZH()
				+ "</XHF_YHZH><GHFMC>"
				+ ShinHoDataUtil.getReplaceString(getGHFMC())
				+ "</GHFMC><GHF_NSRSBH>"
				+ ShinHoDataUtil.getReplaceString(getGHF_NSRSBH())
				+ "</GHF_NSRSBH><GHF_SF>"
				+ getGHF_SF()
				+ "</GHF_SF><GHF_DZ>"
				+ ShinHoDataUtil.getReplaceString(getGHF_DZ())
				+ "</GHF_DZ><GHF_GDDH>"
				+ ShinHoDataUtil.getReplaceString(getGHF_GDDH())
				+ "</GHF_GDDH><GHF_SJ>"
				+ ShinHoDataUtil.getReplaceString(getGHF_SJ())
				+ "</GHF_SJ><GHF_EMAIL>"
				 + getGHF_EMAIL()
				+ "</GHF_EMAIL><GHFQYLX>" + getGHFQYLX()
				+ "</GHFQYLX><GHF_YHZH>" + getGHF_YHZH() + "</GHF_YHZH><HY_DM>"
				+ getHY_DM() + "</HY_DM><HY_MC>" + getHY_MC() + "</HY_MC><KPY>"
				+ getKPY() + "</KPY><SKY>" + getSKY() + "</SKY><FHR>"
				+ getFHR() + "</FHR><KPRQ>" + getKPRQ() + "</KPRQ><KPLX>"
				+ getKPLX() + "</KPLX><YFP_DM>" + getYFP_DM()
				+ "</YFP_DM><YFP_HM>" + getYFP_HM() + "</YFP_HM><CZDM>"
				+ getCZDM() + "</CZDM><QD_BZ>" + getQD_BZ()
				+ "</QD_BZ><QDXMMC>" + getQDXMMC() + "</QDXMMC><CHYY>"
				+ getCHYY() + "</CHYY><TSCHBZ>" + getTSCHBZ()
				+ "</TSCHBZ><KPHJJE>" + getKPHJJE() + "</KPHJJE><HJBHSJE>"
				+ getHJBHSJE() + "</HJBHSJE><HJSE>" + getHJSE() + "</HJSE><BZ>"
				+ getBZ() + "</BZ><BYZD1>" + getBYZD1() + "</BYZD1><BYZD2>"
				+ getBYZD2() + "</BYZD2><BYZD3>" + getBYZD3()
				+ "</BYZD3><BYZD4>" + getBYZD4() + "</BYZD4><BYZD5>"
				+ getBYZD5() + "</BYZD5></FPKJXX_FPTXX>";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
