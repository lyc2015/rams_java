package jp.co.lyc.cms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessResourceFailureException;

public class SalesSituationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2028159323401651353L;

	/* 現在の日付 */
	Date curDate;
	/* 社員営業され日付 */
	String salesDate;
	/* 面談情報更新処理用 */
	String salesDateUpdate;

	String employeeNo;
	String employeeFristName;
	String employeeLastName;

	String salesYearAndMonth;
	String intoCompanyYearAndMonth;
	String interviewDate1;
	String stationCode;
	String interviewCustomer1;
	String interviewDate2;
	String stationCode1;
	String stationCode2;
	String interviewCustomer2;
	String successRate1;
	String successRate2;
	String salesStaff1;
	String salesStaff2;
	String interviewResultAwaiting1;
	String interviewResultAwaiting2;
	String hopeLowestPrice;
	String hopeHighestPrice;
	String hopeRemark;
	String salesProgressCode;
	String bpSalesProgressCode;
	String remark;
	String remark1;
	String remark2;
	String salesStaff;
	String salesStaffName;

	Date updateTime;
	Date createTime;
	String updateUser;

	String employeeName;
	String nearestStation;
	String developLanguageCode;
	String developLanguageName;

	String developLanguage;
	String siteRoleCode;
	String siteRoleName;
	String unitPrice;
	String price;
	String salesPriorityStatus;
	String customer;
	String admissionStartDate;
	String admissionPeriodDate;
	String customerNo;
	String admissionEndDate;
	String scheduledEndDate;
	String resumeInfo1;// 履歴書情報1
	String resumeInfo2;// 履歴書情報2
	Date resume1Date;
	String resumeDate;
	String resumeName1;// 履歴書情報1
	String resumeName2;// 履歴書情報2
	int rowNo;//
	String nowCustomer;
	String customerContractStatus;
	String purchasingManagers2;
	String positionCode2;
	String purchasingManagersMail2;
	String employeeFullName;
	String genderStatus;
	String nationalityName;
	String employeeStatus;
	String birthday;
	String yearsOfExperience;
	String comeToJapanYearAndMonth;
	String projectPhase;
	String projectPhaseName;
	String japaneseLevelCode;
	String englishLevelCode;
	String age;
	String japaneaseConversationLevel;
	String japaneaseLevelDesc;
	String englishConversationLevel;
	String developLanguage1;
	String developLanguage2;
	String developLanguage3;
	String developLanguage4;
	String developLanguage5;
	String developLanguage6;
	String frameWork1;
	String frameWork2;

	String confirmPrice;
	String confirmCustomer;
	ArrayList<String> employeeNoList;
	ArrayList<String> resumeInfoList;
	ArrayList<String> resumeInfo1List;
	ArrayList<String> resumeInfo2List;
	String text;

	String interviewClassificationCode1;
	String interviewClassificationCode2;
	String interviewInfo1;
	String interviewInfo2;
	String interviewUrl1;
	String interviewUrl2;
	String interviewInfoNum;

	String bpUnitPrice;
	String bpOtherCompanyAdmissionEndDate;
	String customerAbbreviation;
	String finishReason;
	String workState;
	String retirementYearAndMonth;

	String alphabetName;
	String completePercet;

	public String getCompletePercet() {
		return completePercet;
	}

	public void setCompletePercet(String completePercet) {
		this.completePercet = completePercet;
	}

	public String getSalesStaffName() {
		return salesStaffName;
	}

	public void setSalesStaffName(String salesStaffName) {
		this.salesStaffName = salesStaffName;
	}

	public String getComeToJapanYearAndMonth() {
		return comeToJapanYearAndMonth;
	}

	public void setComeToJapanYearAndMonth(String comeToJapanYearAndMonth) {
		this.comeToJapanYearAndMonth = comeToJapanYearAndMonth;
	}

	public String getCustomerAbbreviation() {
		return customerAbbreviation;
	}

	public void setCustomerAbbreviation(String customerAbbreviation) {
		this.customerAbbreviation = customerAbbreviation;
	}

	public String getBpOtherCompanyAdmissionEndDate() {
		return bpOtherCompanyAdmissionEndDate;
	}

	public void setBpOtherCompanyAdmissionEndDate(String bpOtherCompanyAdmissionEndDate) {
		this.bpOtherCompanyAdmissionEndDate = bpOtherCompanyAdmissionEndDate;
	}

	public String getBpUnitPrice() {
		return bpUnitPrice;
	}

	public void setBpUnitPrice(String bpUnitPrice) {
		this.bpUnitPrice = bpUnitPrice;
	}

	public String getFrameWork1() {
		return frameWork1;
	}

	public void setFrameWork1(String frameWork1) {
		this.frameWork1 = frameWork1;
	}

	public String getFrameWork2() {
		return frameWork2;
	}

	public void setFrameWork2(String frameWork2) {
		this.frameWork2 = frameWork2;
	}

	public String getInterviewInfoNum() {
		return interviewInfoNum;
	}

	public void setInterviewInfoNum(String interviewInfoNum) {
		this.interviewInfoNum = interviewInfoNum;
	}

	public String getInterviewClassificationCode1() {
		return interviewClassificationCode1;
	}

	public void setInterviewClassificationCode1(String interviewClassificationCode1) {
		this.interviewClassificationCode1 = interviewClassificationCode1;
	}

	public String getInterviewClassificationCode2() {
		return interviewClassificationCode2;
	}

	public void setInterviewClassificationCode2(String interviewClassificationCode2) {
		this.interviewClassificationCode2 = interviewClassificationCode2;
	}

	public String getInterviewInfo1() {
		return interviewInfo1;
	}

	public void setInterviewInfo1(String interviewInfo1) {
		this.interviewInfo1 = interviewInfo1;
	}

	public String getInterviewInfo2() {
		return interviewInfo2;
	}

	public void setInterviewInfo2(String interviewInfo2) {
		this.interviewInfo2 = interviewInfo2;
	}

	public String getInterviewUrl1() {
		return interviewUrl1;
	}

	public void setInterviewUrl1(String interviewUrl1) {
		this.interviewUrl1 = interviewUrl1;
	}

	public String getInterviewUrl2() {
		return interviewUrl2;
	}

	public void setInterviewUrl2(String interviewUrl2) {
		this.interviewUrl2 = interviewUrl2;
	}

	public String getProjectPhaseName() {
		return projectPhaseName;
	}

	public void setProjectPhaseName(String projectPhaseName) {
		this.projectPhaseName = projectPhaseName;
	}

	public String getDevelopLanguage6() {
		return developLanguage6;
	}

	public void setDevelopLanguage6(String developLanguage6) {
		this.developLanguage6 = developLanguage6;
	}

	public String getIntoCompanyYearAndMonth() {
		return intoCompanyYearAndMonth;
	}

	public void setIntoCompanyYearAndMonth(String intoCompanyYearAndMonth) {
		this.intoCompanyYearAndMonth = intoCompanyYearAndMonth;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResumeName1() {
		return resumeName1;
	}

	public void setResumeName1(String resumeName1) {
		this.resumeName1 = resumeName1;
	}

	public Date getResume1Date() {
		return resume1Date;
	}

	public void setResume1Date(Date resume1Date) {
		this.resume1Date = resume1Date;
	}

	public String getResumeDate() {
		return resumeDate;
	}

	public void setResumeDate(String resumeDate) {
		this.resumeDate = resumeDate;
	}

	public String getResumeName2() {
		return resumeName2;
	}

	public void setResumeName2(String resumeName2) {
		this.resumeName2 = resumeName2;
	}

	public ArrayList<String> getResumeInfoList() {
		return resumeInfoList;
	}

	public void setResumeInfoList(ArrayList<String> resumeInfoList) {
		this.resumeInfoList = resumeInfoList;
	}

	public String getSiteRoleName() {
		return siteRoleName;
	}

	public void setSiteRoleName(String siteRoleName) {
		this.siteRoleName = siteRoleName;
	}

	public ArrayList<String> getResumeInfo1List() {
		return resumeInfo1List;
	}

	public void setResumeInfo1List(ArrayList<String> resumeInfo1List) {
		this.resumeInfo1List = resumeInfo1List;
	}

	public ArrayList<String> getResumeInfo2List() {
		return resumeInfo2List;
	}

	public void setResumeInfo2List(ArrayList<String> resumeInfo2List) {
		this.resumeInfo2List = resumeInfo2List;
	}

	public ArrayList<String> getEmployeeNoList() {
		return employeeNoList;
	}

	public void setEmployeeNoList(ArrayList<String> employeeNoList) {
		this.employeeNoList = employeeNoList;
	}

	public String getConfirmPrice() {
		return confirmPrice;
	}

	public void setConfirmPrice(String confirmPrice) {
		this.confirmPrice = confirmPrice;
	}

	public String getConfirmCustomer() {
		return confirmCustomer;
	}

	public void setConfirmCustomer(String confirmCustomer) {
		this.confirmCustomer = confirmCustomer;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}

	public Date getCurDate() {
		return curDate;
	}

	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}

	public String getSalesDateUpdate() {
		return salesDateUpdate;
	}

	public void setSalesDateUpdate(String salesDateUpdate) {
		this.salesDateUpdate = salesDateUpdate;
	}

	/* 2020/12/09 START 張棟 */
	/* 稼働開始 */
	String theMonthOfStartWork;

	public String getTheMonthOfStartWork() {
		return theMonthOfStartWork;
	}

	public void setTheMonthOfStartWork(String theMonthOfStartWork) {
		this.theMonthOfStartWork = theMonthOfStartWork;
	}

	/* 2020/12/09 END 張棟 */

	public String getStationCode1() {
		return stationCode1;
	}

	public void setStationCode1(String stationCode1) {
		this.stationCode1 = stationCode1;
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

	public String getJapaneaseConversationLevel() {
		return japaneaseConversationLevel;
	}

	public void setJapaneaseConversationLevel(String japaneaseConversationLevel) {
		this.japaneaseConversationLevel = japaneaseConversationLevel;
	}

	public String getJapaneaseLevelDesc() {
		return japaneaseLevelDesc;
	}

	public void setJapaneaseLevelDesc(String japaneaseLevelDesc) {
		this.japaneaseLevelDesc = japaneaseLevelDesc;
	}

	public String getEnglishConversationLevel() {
		return englishConversationLevel;
	}

	public void setEnglishConversationLevel(String englishConversationLevel) {
		this.englishConversationLevel = englishConversationLevel;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getJapaneseLevelCode() {
		return japaneseLevelCode;
	}

	public void setJapaneseLevelCode(String japaneseLevelCode) {
		this.japaneseLevelCode = japaneseLevelCode;
	}

	public String getEnglishLevelCode() {
		return englishLevelCode;
	}

	public void setEnglishLevelCode(String englishLevelCode) {
		this.englishLevelCode = englishLevelCode;
	}

	public String getEmployeeFullName() {
		return employeeFullName;
	}

	public void setEmployeeFullName(String employeeFullName) {
		this.employeeFullName = employeeFullName;
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

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
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

	public String getProjectPhase() {
		return projectPhase;
	}

	public void setProjectPhase(String projectPhase) {
		this.projectPhase = projectPhase;
	}

	public String getPurchasingManagers2() {
		return purchasingManagers2;
	}

	public void setPurchasingManagers2(String purchasingManagers2) {
		this.purchasingManagers2 = purchasingManagers2;
	}

	public String getPositionCode2() {
		return positionCode2;
	}

	public void setPositionCode2(String positionCode2) {
		this.positionCode2 = positionCode2;
	}

	public String getPurchasingManagersMail2() {
		return purchasingManagersMail2;
	}

	public void setPurchasingManagersMail2(String purchasingManagersMail2) {
		this.purchasingManagersMail2 = purchasingManagersMail2;
	}

	public String getCustomerContractStatus() {
		return customerContractStatus;
	}

	public void setCustomerContractStatus(String customerContractStatus) {
		this.customerContractStatus = customerContractStatus;
	}

	public String getNowCustomer() {
		return nowCustomer;
	}

	public void setNowCustomer(String nowCustomer) {
		this.nowCustomer = nowCustomer;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
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

	public String getAdmissionEndDate() {
		return admissionEndDate;
	}

	public void setAdmissionEndDate(String admissionEndDate) {
		this.admissionEndDate = admissionEndDate;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getAdmissionStartDate() {
		return admissionStartDate;
	}

	public void setAdmissionStartDate(String admissionStartDate) {
		this.admissionStartDate = admissionStartDate;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSalesPriorityStatus() {
		return salesPriorityStatus;
	}

	public void setSalesPriorityStatus(String salesPriorityStatus) {
		this.salesPriorityStatus = salesPriorityStatus;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	public String getSiteRoleCode() {
		return siteRoleCode;
	}

	public void setSiteRoleCode(String siteRoleCode) {
		this.siteRoleCode = siteRoleCode;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getSalesYearAndMonth() {
		return salesYearAndMonth;
	}

	public void setSalesYearAndMonth(String salesYearAndMonth) {
		this.salesYearAndMonth = salesYearAndMonth;
	}

	public String getInterviewDate1() {
		return interviewDate1;
	}

	public void setInterviewDate1(String interviewDate1) {
		this.interviewDate1 = interviewDate1;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getInterviewCustomer1() {
		return interviewCustomer1;
	}

	public void setInterviewCustomer1(String interviewCustomer1) {
		this.interviewCustomer1 = interviewCustomer1;
	}

	public String getInterviewDate2() {
		return interviewDate2;
	}

	public void setInterviewDate2(String interviewDate2) {
		this.interviewDate2 = interviewDate2;
	}

	public String getStationCode2() {
		return stationCode2;
	}

	public void setStationCode2(String stationCode2) {
		this.stationCode2 = stationCode2;
	}

	public String getInterviewCustomer2() {
		return interviewCustomer2;
	}

	public void setInterviewCustomer2(String interviewCustomer2) {
		this.interviewCustomer2 = interviewCustomer2;
	}

	public String getSuccessRate1() {
		return successRate1;
	}

	public void setSuccessRate1(String successRate1) {
		this.successRate1 = successRate1;
	}

	public String getSuccessRate2() {
		return successRate2;
	}

	public void setSuccessRate2(String successRate2) {
		this.successRate2 = successRate2;
	}

	public String getSalesStaff1() {
		return salesStaff1;
	}

	public void setSalesStaff1(String salesStaff1) {
		this.salesStaff1 = salesStaff1;
	}

	public String getSalesStaff2() {
		return salesStaff2;
	}

	public void setSalesStaff2(String salesStaff2) {
		this.salesStaff2 = salesStaff2;
	}

	public String getInterviewResultAwaiting1() {
		return interviewResultAwaiting1;
	}

	public void setInterviewResultAwaiting1(String interviewResultAwaiting1) {
		this.interviewResultAwaiting1 = interviewResultAwaiting1;
	}

	public String getInterviewResultAwaiting2() {
		return interviewResultAwaiting2;
	}

	public void setInterviewResultAwaiting2(String interviewResultAwaiting2) {
		this.interviewResultAwaiting2 = interviewResultAwaiting2;
	}

	public String getHopeLowestPrice() {
		return hopeLowestPrice;
	}

	public void setHopeLowestPrice(String hopeLowestPrice) {
		this.hopeLowestPrice = hopeLowestPrice;
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

	public String getSalesStaff() {
		return salesStaff;
	}

	public void setSalesStaff(String salesStaff) {
		this.salesStaff = salesStaff;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getDevelopLanguageCode() {
		return developLanguageCode;
	}

	public void setDevelopLanguageCode(String developLanguageCode) {
		this.developLanguageCode = developLanguageCode;
	}

	public String getDevelopLanguageName() {
		return developLanguageName;
	}

	public void setDevelopLanguageName(String developLanguageName) {
		this.developLanguageName = developLanguageName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getScheduledEndDate() {
		return scheduledEndDate;
	}

	public void setScheduledEndDate(String scheduledEndDate) {
		this.scheduledEndDate = scheduledEndDate;
	}

	public String getHopeRemark() {
		return hopeRemark;
	}

	public void setHopeRemark(String hopeRemark) {
		this.hopeRemark = hopeRemark;
	}

	public String getFinishReason() {
		return finishReason;
	}

	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
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

	public String getAdmissionPeriodDate() {
		return admissionPeriodDate;
	}

	public void setAdmissionPeriodDate(String admissionPeriodDate) {
		this.admissionPeriodDate = admissionPeriodDate;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public String getRetirementYearAndMonth() {
		return retirementYearAndMonth;
	}

	public void setRetirementYearAndMonth(String retirementYearAndMonth) {
		this.retirementYearAndMonth = retirementYearAndMonth;
	}

	public String getBpSalesProgressCode() {
		return bpSalesProgressCode;
	}

	public void setBpSalesProgressCode(String bpSalesProgressCode) {
		this.bpSalesProgressCode = bpSalesProgressCode;
	}

	public String getAlphabetName() {

		return alphabetName;
	}

	public void setAlphabetName(String alphabetName) {
		this.alphabetName = alphabetName;
	}
}
