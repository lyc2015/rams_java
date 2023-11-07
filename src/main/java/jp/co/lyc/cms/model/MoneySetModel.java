package jp.co.lyc.cms.model;

import java.io.Serializable;

public class MoneySetModel implements Serializable {

	private static final long serialVersionUID = -2028159323401651353L;

	public String id;
	public String rowNo;
	public String employeeNo;//社員番号
	public String employeeName;
	public String employeeNameTitle;
	public String startYearAndMonth;
	public String additionMoneyCode;
	public String additionNumberOfTimesStatus;
	public String additionMoneyResonCode;
	public String updateUser;
	public String admissionStartDate;
	public String admissionEndDate;
	public String workState;
	public String belongCustomerName;
	public boolean isFinalSiteFinish; // 最近的一个现场是否已经终了
	String salesStaff;
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
	public String getRowNo() {
		return rowNo;
	}
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	public boolean isFinalSiteFinish() {
		return isFinalSiteFinish;
	}
	public void setFinalSiteFinish(boolean isFinalSiteFinish) {
		this.isFinalSiteFinish = isFinalSiteFinish;
	}
	public String getAdmissionStartDate() {
		return admissionStartDate;
	}
	public String getAdmissionEndDate() {
		return admissionEndDate;
	}
	public String getWorkState() {
		return workState;
	}
	public void setAdmissionStartDate(String admissionStartDate) {
		this.admissionStartDate = admissionStartDate;
	}
	public void setAdmissionEndDate(String admissionEndDate) {
		this.admissionEndDate = admissionEndDate;
	}
	public void setWorkState(String workState) {
		this.workState = workState;
	}
	public String getEmployeeNameTitle() {
		return employeeNameTitle;
	}
	public void setEmployeeNameTitle(String employeeNameTitle) {
		this.employeeNameTitle = employeeNameTitle;
	}
	
	public String getBelongCustomerName() {
		return belongCustomerName;
	}
	public void setBelongCustomerName(String belongCustomerName) {
		this.belongCustomerName = belongCustomerName;
	}
	public String getSalesStaff() {
		return salesStaff;
	}
	public void setSalesStaff(String salesStaff) {
		this.salesStaff = salesStaff;
	}
	// 加算回合=固定一回
	public boolean isAdditionNumberOfTimesFix() {
		return "1".equals(additionNumberOfTimesStatus);
	}

	// 获取加算金额
	public int getAdditionMoneyNumber() {
		if ("0".equals(additionMoneyCode)) {
			return 10000;
		}
		if ("1".equals(additionMoneyCode)) {
			return 20000;
		}
		if ("2".equals(additionMoneyCode)) {
			return 30000;
		}
		if ("3".equals(additionMoneyCode)) {
			return 40000;
		}
		if ("4".equals(additionMoneyCode)) {
			return 50000;
		}
		return 0;
	}
}