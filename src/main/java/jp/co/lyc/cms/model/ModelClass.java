package jp.co.lyc.cms.model;

import java.io.Serializable;

public class ModelClass implements Serializable {

	private static final long serialVersionUID = -2028159323401651353L;

	String code;
	String name;
	String columnName;
	String typeName;
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
