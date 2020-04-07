package sajt.shdzfp.sl.model;

public class FPKJXX_DDXXContent {
	private String DDH = "";
	private String THDH = "";
	private String DDDATE = "";
	public String getDDH() {
		return DDH;
	}
	public void setDDH(String dDH) {
		DDH = dDH;
	}
	public String getTHDH() {
		return THDH;
	}
	public void setTHDH(String tHDH) {
		THDH = tHDH;
	}
	public String getDDDATE() {
		return DDDATE;
	}
	public void setDDDATE(String dDDATE) {
		DDDATE = dDDATE;
	}
	
	@Override
	public String toString() {
		return "<FPKJXX_DDXX class=\"FPKJXX_DDXX\"><DDH>"+getDDH()+"</DDH><THDH>"+getTHDH()+"</THDH><DDDATE>"+getDDDATE()+"</DDDATE></FPKJXX_DDXX>";
	}
}
