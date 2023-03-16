package jp.co.lyc.cms.model;

import java.io.Serializable;

public class MoneySetModel implements Serializable {

	private static final long serialVersionUID = -2028159323401651353L;

	public String id;
	public String employeeNo;//社員番号
	public String employeeName;
	public String startYearAndMonth;
	public String additionMoneyCode;
	public String additionNumberOfTimesStatus;
	public String additionMoneyResonCode;
	public String updateUser;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getStartYearAndMonth() {
		return startYearAndMonth;
	}
	public void setStartYearAndMonth(String startYearAndMonth) {
		this.startYearAndMonth = startYearAndMonth;
	}
	
}