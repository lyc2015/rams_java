package jp.co.lyc.cms.model;

public class PayrollAveragedModel {

	String employeeNo; // 社員名
	String salaryStartYear;//精算开始时间
	String salaryEndYear;//精算结束时间
	String salary;//給料
	String bonusPayoffAmount;//赏与	
	String trafficPayoffAmount;//交通
	String socialInsurancePayoffAmount;//社会保险
	String othersPayoffAmount;//其他合计	
	String totalPayoffAmount;//总合计
	
	String updateUser;

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getSalaryStartYear() {
		return salaryStartYear;
	}

	public void setSalaryStartYear(String salaryStartYear) {
		this.salaryStartYear = salaryStartYear;
	}

	public String getSalaryEndYear() {
		return salaryEndYear;
	}

	public void setSalaryEndYear(String salaryEndYear) {
		this.salaryEndYear = salaryEndYear;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getBonusPayoffAmount() {
		return bonusPayoffAmount;
	}

	public void setBonusPayoffAmount(String bonusPayoffAmount) {
		this.bonusPayoffAmount = bonusPayoffAmount;
	}

	public String getTrafficPayoffAmount() {
		return trafficPayoffAmount;
	}

	public void setTrafficPayoffAmount(String trafficPayoffAmount) {
		this.trafficPayoffAmount = trafficPayoffAmount;
	}

	public String getSocialInsurancePayoffAmount() {
		return socialInsurancePayoffAmount;
	}

	public void setSocialInsurancePayoffAmount(String socialInsurancePayoffAmount) {
		this.socialInsurancePayoffAmount = socialInsurancePayoffAmount;
	}

	public String getOthersPayoffAmount() {
		return othersPayoffAmount;
	}

	public void setOthersPayoffAmount(String othersPayoffAmount) {
		this.othersPayoffAmount = othersPayoffAmount;
	}

	public String getTotalPayoffAmount() {
		return totalPayoffAmount;
	}

	public void setTotalPayoffAmount(String totalPayoffAmount) {
		this.totalPayoffAmount = totalPayoffAmount;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

}
