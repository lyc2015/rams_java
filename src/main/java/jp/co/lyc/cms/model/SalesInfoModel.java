package jp.co.lyc.cms.model;

import java.util.Date;

public class SalesInfoModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2028159323401651353L;
	String employeeFristName; // 氏名
	String employeeLastName; // 苗字
	String customerName; // 会社名
	String customerAbbreviation; // 会社略称
	String salary; // 給料
	String unitPrice; // 単価
	String admissionStartDate; // 現場開始年月
	String admissionEndDate; // 現場終了年月
	String employeeStatus; // 社員区分
	String employeeStatusName; // 社員区分
	String employeeFrom; // 所属
	// データ処理
	String rowNo; // 自動採番
	String employeeNo; // 社員番号
	String startEndDate; // 現場開始から終了年月
	String employeeName; // 氏名
	String yearAndMonth; // 年月
	String workDate; // 入場期間
	String profit; // 売上
	int month; // 月数
	String siteRoleName; // 粗利
	String bpBelongCustomerCode; // 所属会社
	String profitAll; // 売上合計
	String siteRoleNameAll; // 粗利合計
	String bpSiteRoleNameAll; // BP営業粗利合計
	String bpUnitPrice; // BP単価
	String reflectYearAndMonth; // 反映年月
	String customerNo; // 会社番号
	String intoCompanyName; // 会社番号
	String intoCompanyCode; // 会社番号
	String customerContractStatus; // 契约区分
	String salesProgressName; // 営業結果パターン
	String startTime; // 現場開始年月
	String endTime; // 現場終了年月
	String levelCode; // レベルコード
	String point; // ポイント
	String specialsalesPoint; // 特別ポイント
	String pointAll; // 合计ポイント
	String specialsalesPointCondition; // 特別ポイント理由
	String salesProgressCode;
	Date startDate;// 現場開始時間
	Date endDate;// 現場終了時間
	String bpGrossProfit; // BP粗利
	String workState; // 現場状態
	boolean firstAdmission; // 初めての現場
	String introducer; // 紹介者	
	String introducerEmployeeName; // 紹介者name
	String salesStaff; // 营业入力
	String bpSiteRoleName; // BP営業粗利
	String occupationCode; // 職種 
	String salesEmployeeName; // 営業name
	String salesOccupationCode; // 営業職種
	String remarks;
	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getIntroducerEmployeeName() {
		return introducerEmployeeName;
	}

	public void setIntroducerEmployeeName(String introducerEmployeeName) {
		this.introducerEmployeeName = introducerEmployeeName;
	}

	public String getSalesEmployeeName() {
		return salesEmployeeName;
	}

	public void setSalesEmployeeName(String salesEmployeeName) {
		this.salesEmployeeName = salesEmployeeName;
	}

	public String getSalesOccupationCode() {
		return salesOccupationCode;
	}

	public void setSalesOccupationCode(String salesOccupationCode) {
		this.salesOccupationCode = salesOccupationCode;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getBpSiteRoleName() {
		return bpSiteRoleName;
	}

	public void setBpSiteRoleName(String bpSiteRoleName) {
		this.bpSiteRoleName = bpSiteRoleName;
	}

	public String getSalesStaff() {
		return salesStaff;
	}

	public void setSalesStaff(String salesStaff) {
		this.salesStaff = salesStaff;
	}

	public String getBpSiteRoleNameAll() {
		return bpSiteRoleNameAll;
	}

	public void setBpSiteRoleNameAll(String bpSiteRoleNameAll) {
		this.bpSiteRoleNameAll = bpSiteRoleNameAll;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public boolean isFirstAdmission() {
		return firstAdmission;
	}

	public void setFirstAdmission(boolean firstAdmission) {
		this.firstAdmission = firstAdmission;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public String getBpGrossProfit() {
		return bpGrossProfit;
	}

	public void setBpGrossProfit(String bpGrossProfit) {
		this.bpGrossProfit = bpGrossProfit;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getPointAll() {
		return pointAll;
	}

	public void setPointAll(String pointAll) {
		this.pointAll = pointAll;
	}

	public String getSpecialsalesPoint() {
		return specialsalesPoint;
	}

	public void setSpecialsalesPoint(String specialsalesPoint) {
		this.specialsalesPoint = specialsalesPoint;
	}

	public String getSpecialsalesPointCondition() {
		return specialsalesPointCondition;
	}

	public void setSpecialsalesPointCondition(String specialsalesPointCondition) {
		this.specialsalesPointCondition = specialsalesPointCondition;
	}

	public String getEmployeeStatusName() {
		return employeeStatusName;
	}

	public void setEmployeeStatusName(String employeeStatusName) {
		this.employeeStatusName = employeeStatusName;
	}

	public String getSalesProgressCode() {
		return salesProgressCode;
	}

	public void setSalesProgressCode(String salesProgressCode) {
		this.salesProgressCode = salesProgressCode;
	}

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

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getSalesProgressName() {
		return salesProgressName;
	}

	public void setSalesProgressName(String salesProgressName) {
		this.salesProgressName = salesProgressName;
	}

	public String getCustomerContractStatus() {
		return customerContractStatus;
	}

	public void setCustomerContractStatus(String customerContractStatus) {
		this.customerContractStatus = customerContractStatus;
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

	public String getIntoCompanyName() {
		return intoCompanyName;
	}

	public void setIntoCompanyName(String intoCompanyName) {
		this.intoCompanyName = intoCompanyName;
	}

	public String getIntoCompanyCode() {
		return intoCompanyCode;
	}

	public void setIntoCompanyCode(String intoCompanyCode) {
		this.intoCompanyCode = intoCompanyCode;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getReflectYearAndMonth() {
		return reflectYearAndMonth;
	}

	public void setReflectYearAndMonth(String reflectYearAndMonth) {
		this.reflectYearAndMonth = reflectYearAndMonth;
	}

	public String getBpUnitPrice() {
		return bpUnitPrice;
	}

	public void setBpUnitPrice(String bpUnitPrice) {
		this.bpUnitPrice = bpUnitPrice;
	}

	public String getProfitAll() {
		return profitAll;
	}

	public void setProfitAll(String profitAll) {
		this.profitAll = profitAll;
	}

	public String getSiteRoleNameAll() {
		return siteRoleNameAll;
	}

	public void setSiteRoleNameAll(String siteRoleNameAll) {
		this.siteRoleNameAll = siteRoleNameAll;
	}

	public String getBpBelongCustomerCode() {
		return bpBelongCustomerCode;
	}

	public void setBpBelongCustomerCode(String bpBelongCustomerCode) {
		this.bpBelongCustomerCode = bpBelongCustomerCode;
	}

	public String getRowNo() {
		return rowNo;
	}

	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
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

	public String getEmployeeFristName() {
		return employeeFristName;
	}

	public void setEmployeeFristName(String employeeFristName) {
		this.employeeFristName = employeeFristName;
	}

	public String getEmployeeLastName() {
		return employeeLastName;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAbbreviation() {
		return customerAbbreviation;
	}

	public void setCustomerAbbreviation(String customerAbbreviation) {
		this.customerAbbreviation = customerAbbreviation;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getStartEndDate() {
		return startEndDate;
	}

	public void setStartEndDate(String startEndDate) {
		this.startEndDate = startEndDate;
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

	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
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
}
