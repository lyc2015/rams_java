package jp.co.lyc.cms.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SendLettersConfirmModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2028159323401651353L;

	String employeeNo;
	String employeeName;
	String salesYearAndMonth;
	String hopeHighestPrice;
	String unitPrice;
	String salesProgressCode;
	String remark1;
	String remark2;
	String updateUser;
	String employeeStatus;
	String stationName;
	String nearestStation;
	String developLanguage;
	String genderStatus;
	String nationalityName;
	String birthday;
	String yearsOfExperience;
	String japaneseLevelName;
	String beginMonth;
	String[] employeeNos;
	ArrayList<String> resumeInfoList;
	String resumeInfoName;// 履歴書
	String resumeInfo1;// 履歴書情報1
	String resumeInfo2;//
	String resumeName1;// 履歴書情報1
	String resumeName2;//
	String resume;// 履歴書情報1

	int index;//

	public String getResumeName1() {
		return resumeName1;
	}

	public void setResumeName1(String resumeName1) {
		this.resumeName1 = resumeName1;
	}

	public String getResumeName2() {
		return resumeName2;
	}

	public void setResumeName2(String resumeName2) {
		this.resumeName2 = resumeName2;
	}

	public String getResumeInfoName() {
		return resumeInfoName;
	}

	public void setResumeInfoName(String resumeInfoName) {
		this.resumeInfoName = resumeInfoName;
	}

	public ArrayList<String> getResumeInfoList() {
		return resumeInfoList;
	}

	public void setResumeInfoList(ArrayList<String> resumeInfoList) {
		this.resumeInfoList = resumeInfoList;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getResumeInfo1() {
		return resumeInfo1;
	}

	public void setResumeInfo1(String resumeInfo1) {
		this.resumeInfo1 = resumeInfo1;
	}

	public String getResumeInfo2() {
		return resumeInfo2;
	}

	public void setResumeInfo2(String resumeInfo2) {
		this.resumeInfo2 = resumeInfo2;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String[] getEmployeeNos() {
		return employeeNos;
	}

	public void setEmployeeNos(String[] employeeNos) {
		this.employeeNos = employeeNos;
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

	public String getSalesYearAndMonth() {
		return salesYearAndMonth;
	}

	public void setSalesYearAndMonth(String salesYearAndMonth) {
		this.salesYearAndMonth = salesYearAndMonth;
	}

	public String getHopeHighestPrice() {
		return hopeHighestPrice;
	}

	public void setHopeHighestPrice(String hopeHighestPrice) {
		this.hopeHighestPrice = hopeHighestPrice;
	}

	public String getSalesProgressCode() {
		return salesProgressCode;
	}

	public void setSalesProgressCode(String salesProgressCode) {
		this.salesProgressCode = salesProgressCode;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getNearestStation() {
		return nearestStation;
	}

	public void setNearestStation(String nearestStation) {
		this.nearestStation = nearestStation;
	}

	public String getDevelopLanguage() {
		return developLanguage;
	}

	public void setDevelopLanguage(String developLanguage) {
		this.developLanguage = developLanguage;
	}

	public String getGenderStatus() {
		return genderStatus;
	}

	public void setGenderStatus(String genderStatus) {
		this.genderStatus = genderStatus;
	}

	public String getNationalityName() {
		return nationalityName;
	}

	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(String yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public String getJapaneseLevelName() {
		return japaneseLevelName;
	}

	public void setJapaneseLevelName(String japaneseLevelName) {
		this.japaneseLevelName = japaneseLevelName;
	}

	public String getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(String beginMonth) {
		this.beginMonth = beginMonth;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
