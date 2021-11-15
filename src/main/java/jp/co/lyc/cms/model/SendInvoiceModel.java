package jp.co.lyc.cms.model;

import java.util.List;

public class SendInvoiceModel {

	int rowNo;
	String employeeNo;
	String employeeName;
	String payOffRange1;
	String payOffRange2;
	String customerNo;
	String customerName;
	String purchasingManagers;
	String purchasingManagersMail;
	String sumWorkTime;
	String DeductionsAndOvertimePayOfUnitPrice;

	List<SendInvoiceWorkTimeModel> sendInvoiceWorkTimeModel;

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getPayOffRange1() {
		return payOffRange1;
	}

	public void setPayOffRange1(String payOffRange1) {
		this.payOffRange1 = payOffRange1;
	}

	public String getPayOffRange2() {
		return payOffRange2;
	}

	public void setPayOffRange2(String payOffRange2) {
		this.payOffRange2 = payOffRange2;
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

	public String getPurchasingManagers() {
		return purchasingManagers;
	}

	public void setPurchasingManagers(String purchasingManagers) {
		this.purchasingManagers = purchasingManagers;
	}

	public String getPurchasingManagersMail() {
		return purchasingManagersMail;
	}

	public void setPurchasingManagersMail(String purchasingManagersMail) {
		this.purchasingManagersMail = purchasingManagersMail;
	}

	public String getSumWorkTime() {
		return sumWorkTime;
	}

	public void setSumWorkTime(String sumWorkTime) {
		this.sumWorkTime = sumWorkTime;
	}

	public String getDeductionsAndOvertimePayOfUnitPrice() {
		return DeductionsAndOvertimePayOfUnitPrice;
	}

	public void setDeductionsAndOvertimePayOfUnitPrice(String deductionsAndOvertimePayOfUnitPrice) {
		DeductionsAndOvertimePayOfUnitPrice = deductionsAndOvertimePayOfUnitPrice;
	}

	public List<SendInvoiceWorkTimeModel> getSendInvoiceWorkTimeModel() {
		return sendInvoiceWorkTimeModel;
	}

	public void setSendInvoiceWorkTimeModel(List<SendInvoiceWorkTimeModel> sendInvoiceWorkTimeModel) {
		this.sendInvoiceWorkTimeModel = sendInvoiceWorkTimeModel;
	}

}
