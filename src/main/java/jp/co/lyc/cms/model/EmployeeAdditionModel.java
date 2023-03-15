package jp.co.lyc.cms.model;

import java.io.Serializable;

public class EmployeeAdditionModel implements Serializable {

	private static final long serialVersionUID = -2028159323401651353L;

	public String rowNo;//番号
	public String employeeNo;//社員番号
	public String employeeName;
	public String yearAndMonth;
	public String additionMoneyCode;
	public String additionNumberOfTimesStatus;
	public String additionMoneyResonCode;
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
	public String getYearAndMonth() {
		return yearAndMonth;
	}
	public void setYearAndMonth(String yearAndMonth) {
		this.yearAndMonth = yearAndMonth;
	}
	public String getAdditionMoneyCode() {
		return additionMoneyCode;
	}
	public void setAdditionMoneyCode(String additionMoneyCode) {
		this.additionMoneyCode = additionMoneyCode;
	}
	public String getAdditionNumberOfTimesStatus() {
		return additionNumberOfTimesStatus;
	}
	public void setAdditionNumberOfTimesStatus(String additionNumberOfTimesStatus) {
		this.additionNumberOfTimesStatus = additionNumberOfTimesStatus;
	}
	public String getAdditionMoneyResonCode() {
		return additionMoneyResonCode;
	}
	public void setAdditionMoneyResonCode(String additionMoneyResonCode) {
		this.additionMoneyResonCode = additionMoneyResonCode;
	}
	public String getRowNo() {
		return rowNo;
	}
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	
}