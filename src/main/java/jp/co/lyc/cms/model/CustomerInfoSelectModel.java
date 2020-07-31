package jp.co.lyc.cms.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerInfoSelectModel {
	
	ArrayList<HashMap<String, String>> customerRanking;
	ArrayList<HashMap<String, String>> companyNature;
	ArrayList<HashMap<String, String>> position;

	public ArrayList<HashMap<String, String>> getPosition() {
		return position;
	}

	public void setPosition(ArrayList<HashMap<String, String>> position) {
		this.position = position;
	}

	public ArrayList<HashMap<String, String>> getCustomerRanking() {
		return customerRanking;
	}

	public void setCustomerRanking(ArrayList<HashMap<String, String>> customerRanking) {
		this.customerRanking = customerRanking;
	}

	public ArrayList<HashMap<String, String>> getCompanyNature() {
		return companyNature;
	}

	public void setCompanyNature(ArrayList<HashMap<String, String>> companyNature) {
		this.companyNature = companyNature;
	}
}
