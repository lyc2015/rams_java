package jp.co.lyc.cms.model;

import java.io.Serializable;

public class ModelClass implements Serializable {

	private static final long serialVersionUID = -2028159323401651353L;

	String code;
	String name;
	String value;
	String text;
	String columnName;
	String typeName;
	String mail;
	String employeeFristName;
	String employeeLastName;

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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
