package sajt.shdzfp.sl.model;

public class FPXZContent {

	private String FPQQLSH = "";
	private String DSPTBM = "";
	private String NSRSBH = "";
	private String DDH = "";
	private String PDF_XZFS = "";
	//1443406187837
	public String getFPQQLSH() {
		return FPQQLSH;
	}
	public void setFPQQLSH(String fPQQLSH) {
		FPQQLSH = fPQQLSH;
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
	public String getDDH() {
		return DDH;
	}
	public void setDDH(String dDH) {
		DDH = dDH;
	}
	public String getPDF_XZFS() {
		return PDF_XZFS;
	}
	public void setPDF_XZFS(String pDF_XZFS) {
		PDF_XZFS = pDF_XZFS;
	}
	
	@Override
	public String toString(){
		return "<REQUEST_FPXXXZ_NEW class=\"REQUEST_FPXXXZ_NEW\"><FPQQLSH>"+getFPQQLSH()+"</FPQQLSH><DSPTBM>"+getDSPTBM()+"</DSPTBM><NSRSBH>"+getNSRSBH()+"</NSRSBH><DDH>"+getDDH()+"</DDH><PDF_XZFS>"+getPDF_XZFS()+"</PDF_XZFS></REQUEST_FPXXXZ_NEW>";
	}
	
}
