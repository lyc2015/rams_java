package jp.co.lyc.cms.model;

public class CertificatePrintingModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2028159323401651353L;

	String certificate; // 証明書種類
	String employeeName; // 社員名前
	String employeeNo; // 社員番号
	String birthday; // 誕生日
	String address; // 住所
	String intoCompanyYearAndMonth; // 入社年月
	String nowYearAndMonth; // 在職年月まで
	String workingTime; // 勤務時間
	String lastDayofYearAndMonth; // 退職年月まで
	String retirementYearAndMonth; // 退職年月
	String occupationCode; // 役職
	String remark; // 備考

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIntoCompanyYearAndMonth() {
		return intoCompanyYearAndMonth;
	}

	public void setIntoCompanyYearAndMonth(String intoCompanyYearAndMonth) {
		this.intoCompanyYearAndMonth = intoCompanyYearAndMonth;
	}

	public String getNowYearAndMonth() {
		return nowYearAndMonth;
	}

	public void setNowYearAndMonth(String nowYearAndMonth) {
		this.nowYearAndMonth = nowYearAndMonth;
	}

	public String getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(String workingTime) {
		this.workingTime = workingTime;
	}

	public String getLastDayofYearAndMonth() {
		return lastDayofYearAndMonth;
	}

	public void setLastDayofYearAndMonth(String lastDayofYearAndMonth) {
		this.lastDayofYearAndMonth = lastDayofYearAndMonth;
	}

	public String getRetirementYearAndMonth() {
		return retirementYearAndMonth;
	}

	public void setRetirementYearAndMonth(String retirementYearAndMonth) {
		this.retirementYearAndMonth = retirementYearAndMonth;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
