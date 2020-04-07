package sajt.shdzfp.sl.model;

public class ZData {

	private DataDescription dataDescription = new DataDescription();
	public DataDescription getDataDescription() {
		return dataDescription;
	}
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private String content = "";
}
