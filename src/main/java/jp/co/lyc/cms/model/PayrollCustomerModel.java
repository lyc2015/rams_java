package jp.co.lyc.cms.model;

public class PayrollCustomerModel {
	
	String employeeNo;// 社員名
	String admissionEndDate; //结束时间
	String admissionStartDate;//开始时间
	String unitPrice; //単価	
	String customerNo;	//お客様
	String customerName;  //お客様
	
	
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
	public String getAdmissionEndDate() {
		return admissionEndDate;
	}
	public void setAdmissionEndDate(String admissionEndDate) {
		this.admissionEndDate = admissionEndDate;
	}
	public String getAdmissionStartDate() {
		return admissionStartDate;
	}
	public void setAdmissionStartDate(String admissionStartDate) {
		this.admissionStartDate = admissionStartDate;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	

}
