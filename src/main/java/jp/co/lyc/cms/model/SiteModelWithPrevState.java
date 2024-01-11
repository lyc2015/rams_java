package jp.co.lyc.cms.model;

public class SiteModelWithPrevState {
  SiteModel siteModel;
	String prevWorkState;
	String prevAdmissionEndDate;
	String prevScheduledEndDate;

	public SiteModel getSiteModel() {
		return siteModel;
	}

	public void setSiteModel(SiteModel siteModel) {
		this.siteModel = siteModel;
	}

	public String getPrevAdmissionEndDate() {
		return prevAdmissionEndDate;
	}

	public void setPrevAdmissionEndDate(String prevAdmissionEndDate) {
		this.prevAdmissionEndDate = prevAdmissionEndDate;
	}

	public String getPrevWorkState() {
		return prevWorkState;
	}

	public void setPrevWorkState(String prevWorkState) {
		this.prevWorkState = prevWorkState;
	}

	public String getPrevScheduledEndDate() {
		return prevScheduledEndDate;
	}

	public void setPrevScheduledEndDate(String prevScheduledEndDate) {
		this.prevScheduledEndDate = prevScheduledEndDate;
	}
}
