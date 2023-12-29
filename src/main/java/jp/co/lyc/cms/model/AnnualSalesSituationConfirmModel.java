package jp.co.lyc.cms.model;

import java.io.Serializable;

public class AnnualSalesSituationConfirmModel implements Serializable {
  String employeeNo;
  String salesYearAndMonth;
  String salesStaff;
  String employeeFirstName;
  String employeeLastName;
  String salesStaffFirstName;
  String salesStaffLastName;
  String customerNo;
  String admissionEndDate;
  String customerAbbreviation;

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

  public String getSalesStaff() {
    return salesStaff;
  }

  public void setSalesStaff(String salesStaff) {
    this.salesStaff = salesStaff;
  }

  public String getEmployeeFirstName() {
    return employeeFirstName;
  }

  public void setEmployeeFirstName(String employeeFirstName) {
    this.employeeFirstName = employeeFirstName;
  }

  public String getEmployeeLastName() {
    return employeeLastName;
  }

  public void setEmployeeLastName(String employeeLastName) {
    this.employeeLastName = employeeLastName;
  }

  public String getSalesStaffFirstName() {
    return salesStaffFirstName;
  }

  public void setSalesStaffLastName(String salesStaffLastName) {
    this.salesStaffLastName = salesStaffLastName;
  }

  public String getCustomerNo() {
    return customerNo;
  }

  public void setCustomerNo(String customerNo) {
    this.customerNo = customerNo;
  }

  public String getAdmissionEndDate() {
    return admissionEndDate;
  }

  public void setAdmissionEndDate(String admissionEndDate) {
    this.admissionEndDate = admissionEndDate;
  }

  public String getCustomerAbbreviation() {
    return customerAbbreviation;
  }

  public void setCustomerAbbreviation(String customerAbbreviation) {
    this.customerAbbreviation = customerAbbreviation;
  }
}