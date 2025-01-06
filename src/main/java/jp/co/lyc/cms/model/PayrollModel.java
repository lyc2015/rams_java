package jp.co.lyc.cms.model;

public class PayrollModel {

		String employeeNo;// 社員名
		String employeeStatus;// 社員区分
		String intoCompanyYearAndMonth; //入社年份	
		String yearAndMonth; //年度番号
		String reflectYearAndMonth;
		String unitPrice; //単価	
		String salary; //給料
		String bonusPayoff; //赏与	
		String socialInsurancePayoff; //社会保险	
		String othersPayoff; //其他合计	
		Boolean isSalaryIncrease;  //是否涨薪	
		Boolean isWaiting;//是否待机
		String customerNo;	//お客様
		String customerName;  //お客様
		String averageSalary;	//平均給料
		String averageBonusPayoff;	//平均赏与
		String averageTraffic;	//平均交通费
		String averageSocialInsurancePayoff;  //平均社会保险
		String averageOther;	//其他平均
		String averageValue;   //平均支付费用

		
		public String getEmployeeNo() {
			return employeeNo;
		}
		public void setEmployeeNo(String employeeNo) {
			this.employeeNo = employeeNo;
		}
		public String getEmployeeStatus() {
			return employeeStatus;
		}
		public void setEmployeeStatus(String employeeStatus) {
			this.employeeStatus = employeeStatus;
		}
		public String getYearAndMonth() {
			return yearAndMonth;
		}
		public void setYearAndMonth(String yearAndMonth) {
			this.yearAndMonth = yearAndMonth;
		}
		public String getReflectYearAndMonth() {
			return reflectYearAndMonth;
		}
		public void setReflectYearAndMonth(String reflectYearAndMonth) {
			this.reflectYearAndMonth = reflectYearAndMonth;
		}
		public String getIntoCompanyYearAndMonth() {
			return intoCompanyYearAndMonth;
		}
		public void setIntoCompanyYearAndMonth(String intoCompanyYearAndMonth) {
			this.intoCompanyYearAndMonth = intoCompanyYearAndMonth;
		}
		public String getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(String unitPrice) {
			this.unitPrice = unitPrice;
		}
		public String getSalary() {
			return salary;
		}
		public void setSalary(String salary) {
			this.salary = salary;
		}
		public String getBonusPayoff() {
			return bonusPayoff;
		}
		public void setBonusPayoff(String bonusPayoff) {
			this.bonusPayoff = bonusPayoff;
		}
		public String getSocialInsurancePayoff() {
			return socialInsurancePayoff;
		}
		public void setSocialInsurancePayoff(String socialInsurancePayoff) {
			this.socialInsurancePayoff = socialInsurancePayoff;
		}
		public String getOthersPayoff() {
			return othersPayoff;
		}
		public void setOthersPayoff(String othersPayoff) {
			this.othersPayoff = othersPayoff;
		}
		public Boolean getIsSalaryIncrease() {
			return isSalaryIncrease;
		}
		public void setIsSalaryIncrease(Boolean isSalaryIncrease) {
			this.isSalaryIncrease = isSalaryIncrease;
		}
		public Boolean getIsWaiting() {
			return isWaiting;
		}
		public void setIsWaiting(Boolean isWaiting) {
			this.isWaiting = isWaiting;
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
		public String getAverageSalary() {
			return averageSalary;
		}
		public void setAverageSalary(String averageSalary) {
			this.averageSalary = averageSalary;
		}
		public String getAverageBonusPayoff() {
			return averageBonusPayoff;
		}
		public void setAverageBonusPayoff(String averageBonusPayoff) {
			this.averageBonusPayoff = averageBonusPayoff;
		}
		public String getAverageTraffic() {
			return averageTraffic;
		}
		public void setAverageTraffic(String averageTraffic) {
			this.averageTraffic = averageTraffic;
		}
		public String getAverageSocialInsurancePayoff() {
			return averageSocialInsurancePayoff;
		}
		public void setAverageSocialInsurancePayoff(String averageSocialInsurancePayoff) {
			this.averageSocialInsurancePayoff = averageSocialInsurancePayoff;
		}
		public String getAverageOther() {
			return averageOther;
		}
		public void setAverageOther(String averageOther) {
			this.averageOther = averageOther;
		}
		public String getAverageValue() {
			return averageValue;
		}
		public void setAverageValue(String averageValue) {
			this.averageValue = averageValue;
		}
		@Override
		public String toString() {
			return "PayrollModel [employeeNo=" + employeeNo + ", employeeStatus=" + employeeStatus
					+ ", intoCompanyYearAndMonth=" + intoCompanyYearAndMonth + ", yearAndMonth=" + yearAndMonth
					+ ", reflectYearAndMonth=" + reflectYearAndMonth + ", unitPrice=" + unitPrice + ", salary=" + salary
					+ ", bonusPayoff=" + bonusPayoff + ", socialInsurancePayoff=" + socialInsurancePayoff
					+ ", othersPayoff=" + othersPayoff + ", isSalaryIncrease=" + isSalaryIncrease + ", isWaiting="
					+ isWaiting + ", customerNo=" + customerNo + ", customerName=" + customerName + ", averageSalary="
					+ averageSalary + ", averageBonusPayoff=" + averageBonusPayoff + ", averageTraffic="
					+ averageTraffic + ", averageSocialInsurancePayoff=" + averageSocialInsurancePayoff
					+ ", averageOther=" + averageOther + ", averageValue=" + averageValue + ", getEmployeeNo()="
					+ getEmployeeNo() + ", getEmployeeStatus()=" + getEmployeeStatus() + ", getYearAndMonth()="
					+ getYearAndMonth() + ", getReflectYearAndMonth()=" + getReflectYearAndMonth()
					+ ", getIntoCompanyYearAndMonth()=" + getIntoCompanyYearAndMonth() + ", getUnitPrice()="
					+ getUnitPrice() + ", getSalary()=" + getSalary() + ", getBonusPayoff()=" + getBonusPayoff()
					+ ", getSocialInsurancePayoff()=" + getSocialInsurancePayoff() + ", getOthersPayoff()="
					+ getOthersPayoff() + ", getIsSalaryIncrease()=" + getIsSalaryIncrease() + ", getIsWaiting()="
					+ getIsWaiting() + ", getCustomerNo()=" + getCustomerNo() + ", getCustomerName()="
					+ getCustomerName() + ", getAverageSalary()=" + getAverageSalary() + ", getAverageBonusPayoff()="
					+ getAverageBonusPayoff() + ", getAverageTraffic()=" + getAverageTraffic()
					+ ", getAverageSocialInsurancePayoff()=" + getAverageSocialInsurancePayoff()
					+ ", getAverageOther()=" + getAverageOther() + ", getAverageValue()=" + getAverageValue()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}

		

		
}
