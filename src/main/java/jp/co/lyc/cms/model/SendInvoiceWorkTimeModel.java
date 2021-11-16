package jp.co.lyc.cms.model;

public class SendInvoiceWorkTimeModel {

	int rowNo;
	String employeeNo;
	String employeeName;
	String systemName;
	String unitPrice;
	String payOffRange1;
	String payOffRange2;
	String sumWorkTime;
	String DeductionsAndOvertimePayOfUnitPrice;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

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

}
