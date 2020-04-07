package sajt.shdzfp.sl.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Interface {

	private GlobalInfo globalInfo = new GlobalInfo();
	private ReturnStateInfo returnStateInfo = new ReturnStateInfo();
	private ZData zData = new ZData();

	public GlobalInfo getGlobalInfo() {
		return globalInfo;
	}

	public void setGlobalInfo(GlobalInfo globalInfo) {
		this.globalInfo = globalInfo;
	}

	public ReturnStateInfo getReturnStateInfo() {
		return returnStateInfo;
	}

	public void setReturnStateInfo(ReturnStateInfo returnStateInfo) {
		this.returnStateInfo = returnStateInfo;
	}

	public ZData getzData() {
		return zData;
	}

	public void setzData(ZData zData) {
		this.zData = zData;
	}

}
