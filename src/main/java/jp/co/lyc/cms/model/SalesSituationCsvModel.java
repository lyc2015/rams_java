package jp.co.lyc.cms.model;

import java.io.Serializable;

public class SalesSituationCsvModel implements Serializable {
	/**
	 * 
	 */
	// private static final long serialVersionUID = -2028159323401651353L;
	String employeeNo;// 社員番号 T001
	String alphabetName;// ローマ名 T002
	String employeeFristName;// 苗字 T001
	String employeeName;// 社員氏名 T001
	String yearsOfExperience;// 経験 优先T019没有的话用T002的
	String theMonthOfStartWork;// 稼動 T019
	String siteRoleCode;// 役割 T002
	String japaneaseConversationLevel;// 日本語 优先T019没有的话用T002的
	String developLanguage1;// 開発言語 优先T019没有的话用T002的
	String developLanguage2;// 開発言語 优先T019没有的话用T002的
	String developLanguage3;// 開発言語 优先T019没有的话用T002的
	String developLanguage4;// 開発言語 优先T019没有的话用T002的
	String developLanguage5;// 開発言語 优先T019没有的话用T002的
	String developLanguage6;// 開発言語 优先T019没有的话用T002的
	String stationName;// 寄り駅 优先T019没有的话用T003的
	String unitPrice;// 単価 优先T019，没有的话从T006EmployeeSiteInfo取最新的现场的单价
	String salesProgressCode;// 営業状況 T010
	String remark;// 備考 T019

	public String getEmployeeFristName() {
		return employeeFristName;
	}

	public void setEmployeeFristName(String employeeFristName) {
		this.employeeFristName = employeeFristName;
	}

	public String getAlphabetName() {
		return alphabetName;
	}

	public void setAlphabetName(String alphabetName) {
		this.alphabetName = alphabetName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
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

	public String getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(String yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public String getTheMonthOfStartWork() {
		return theMonthOfStartWork;
	}

	public void setTheMonthOfStartWork(String theMonthOfStartWork) {
		this.theMonthOfStartWork = theMonthOfStartWork;
	}

	public String getSiteRoleCode() {
		return siteRoleCode;
	}

	public void setSiteRoleCode(String siteRoleCode) {
		this.siteRoleCode = siteRoleCode;
	}

	public String getJapaneaseConversationLevel() {
		return japaneaseConversationLevel;
	}

	public void setJapaneaseConversationLevel(String japaneaseConversationLevel) {
		this.japaneaseConversationLevel = japaneaseConversationLevel;
	}

	public String getDevelopLanguage1() {
		return developLanguage1;
	}

	public void setDevelopLanguage1(String developLanguage1) {
		this.developLanguage1 = developLanguage1;
	}

	public String getDevelopLanguage2() {
		return developLanguage2;
	}

	public void setDevelopLanguage2(String developLanguage2) {
		this.developLanguage2 = developLanguage2;
	}

	public String getDevelopLanguage3() {
		return developLanguage3;
	}

	public void setDevelopLanguage3(String developLanguage3) {
		this.developLanguage3 = developLanguage3;
	}

	public String getDevelopLanguage4() {
		return developLanguage4;
	}

	public void setDevelopLanguage4(String developLanguage4) {
		this.developLanguage4 = developLanguage4;
	}

	public String getDevelopLanguage5() {
		return developLanguage5;
	}

	public void setDevelopLanguage5(String developLanguage5) {
		this.developLanguage5 = developLanguage5;
	}

	public String getDevelopLanguage6() {
		return developLanguage6;
	}

	public void setDevelopLanguage6(String developLanguage6) {
		this.developLanguage6 = developLanguage6;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getSalesProgressCode() {
		return salesProgressCode;
	}

	public void setSalesProgressCode(String salesProgressCode) {
		this.salesProgressCode = salesProgressCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
