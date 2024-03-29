package jp.co.lyc.cms.model;

import java.util.Date;

public class SalesProfitModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2028159323401651353L;
	String rowNo;// 番号
	String yearAndMonth;// 年月
	String employeeStatus;// 社員区分・返信同じ
	String employeeFrom;// 所属
	String employeeName;// 氏名
	String customerName;// お客様・返信同じ
	String workDate;// 入出場期間
	String unitPrice;// 単価・返信同じ
	String profit;// 売上
	String salary;// 給料(発注額)
	String siteRoleName;// 粗利
	// ＤＢ検索用↓返信用
	String firstName;// 姓
	String lastName;// 名
	String admissionStartDate;
	String admissionEndDate;
	String customerContractStatus; // 契约区分
	String intoCompanyCode; // 入社区分
	Date startDate;// 現場開始時間
	Date endDate;// 現場終了時間
	String pdf; // pdf
	String startTime; // 現場開始時間
	String endTime; // 現場終了時間
	

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getCustomerContractStatus() {
		return customerContractStatus;
	}

	public void setCustomerContractStatus(String customerContractStatus) {
		this.customerContractStatus = customerContractStatus;
	}

	public String getIntoCompanyCode() {
		return intoCompanyCode;
	}

	public void setIntoCompanyCode(String intoCompanyCode) {
		this.intoCompanyCode = intoCompanyCode;
	}

	public String getAdmissionStartDate() {
		return admissionStartDate;
	}

	public void setAdmissionStartDate(String admissionStartDate) {
		this.admissionStartDate = admissionStartDate;
	}

	public String getAdmissionEndDate() {
		return admissionEndDate;
	}

	public void setAdmissionEndDate(String admissionEndDate) {
		this.admissionEndDate = admissionEndDate;
	}

	public String getRowNo() {
		return rowNo;
	}

	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}

	public String getYearAndMonth() {
		return yearAndMonth;
	}

	public void setYearAndMonth(String yearAndMonth) {
		this.yearAndMonth = yearAndMonth;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getEmployeeFrom() {
		return employeeFrom;
	}

	public void setEmployeeFrom(String employeeFrom) {
		this.employeeFrom = employeeFrom;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getSiteRoleName() {
		return siteRoleName;
	}

	public void setSiteRoleName(String siteRoleName) {
		this.siteRoleName = siteRoleName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
