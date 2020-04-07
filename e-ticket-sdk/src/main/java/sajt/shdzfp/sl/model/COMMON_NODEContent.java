package sajt.shdzfp.sl.model;

public class COMMON_NODEContent {

	private String NAME = "";
	private String VALUE = "";
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getVALUE() {
		return VALUE;
	}
	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	@Override
	public String toString() {
		return "<COMMON_NODE><NAME>"+getNAME()+"</NAME><VALUE>"+getVALUE()+"</VALUE></COMMON_NODE>";
	}
	
}
