package sajt.shdzfp.sl.model;

public class DataDescription {

	private String zipCode = "0";
	private String encryptCode = "1";
	private String codeType = "3DES";
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getEncryptCode() {
		return encryptCode;
	}
	public void setEncryptCode(String encryptCode) {
		this.encryptCode = encryptCode;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
}
