package jp.co.lyc.cms.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sun.mail.util.MailSSLSocketFactory;

import jp.co.lyc.cms.model.EmailModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.ResultModel;
import jp.co.lyc.cms.service.UtilsService;
import net.sf.json.JSONObject;

@Controller
public class UtilsController {

	// ????????????????????????
	static {
		System.getProperties().setProperty("mail.mime.splitlongparameters", "false");
	}

	@Autowired
	UtilsService utilsService;

	public static Map<String, String> JapanHoliday = new HashMap<String, String>();
	static {
		StringBuffer json = new StringBuffer();
		try {
			URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
			InputStream is = url.openStream(); // throws an IOException
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
		}
		com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(json.toString());
//		String yearMonth = (String)jsonObject.getOrDefault("yearMonth", "");
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			JapanHoliday.put(key, jsonObject.getString(key));
		}
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNationality", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getNationality() {
		List<ModelClass> list = utilsService.getNationalitys();
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSalesPattern", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSalesPattern() {
		List<ModelClass> list = utilsService.getSalesPuttern();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSpecialPointCondition", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSpecialPointCondition() {
		List<ModelClass> list = utilsService.getSpecialPoint();
		return list;
	}

	/**
	 * BP??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getBpGrossProfit", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBpGrossProfit() {
		List<ModelClass> list = utilsService.getBpGrossProfit();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getCustomerContractStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomerContractStatus() {
		Properties properties = getProperties();
		String customerContractStatus = properties.getProperty("customerContract");
		List<ModelClass> list = getStatus(customerContractStatus);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSiteStateStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSiteStateStatus() {
		Properties properties = getProperties();
		String siteStateStatus = properties.getProperty("siteState");
		List<ModelClass> list = getStatus(siteStateStatus);
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeForm", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmployeeForm() {
		List<ModelClass> list = utilsService.getStaffForms();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getVisa", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getVisa() {
		List<ModelClass> list = utilsService.getVisa();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTechnologyType", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTechnologyType(@RequestBody EmployeeModel emp) {
		Map<String, String> sendMap = getParam(emp);
		List<ModelClass> list = utilsService.getTechnologyType(sendMap);
		return list;
	}

	/**
	 * ?????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getLevel", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getLevel() {
		List<ModelClass> list = utilsService.getLevel();
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getJapaneseLevel", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getJapaneseLevel() {
		List<ModelClass> list = utilsService.getJapaneseLevel();
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCompanyNature", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCompanyNature() {
		List<ModelClass> list = utilsService.getCompanyNature();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPosition", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getPosition() {
		List<ModelClass> list = utilsService.getPosition();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCustomerDepartment", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomerDepartment() {
		List<ModelClass> list = utilsService.getDepartmentMasterDrop();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getBankInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBankInfo() {
		List<ModelClass> list = utilsService.getBankInfo();
		return list;
	}
	
	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getBankBranch", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBankBranch() {
		List<ModelClass> list = utilsService.getBankBranch();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getBankBranchInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBankBranchInfo(@RequestBody HashMap<String, String> sendMap) {
		List<ModelClass> list = utilsService.getBankBranchInfo(sendMap);
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPaymentSite", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getPaymentSite() {
		List<ModelClass> list = utilsService.getPaymentSite();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getGender", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getGender() {
		Properties properties = getProperties();
		String gender = properties.getProperty("gender");
		List<ModelClass> list = getStatus(gender);
		return list;
	}

	/**
	 * approval???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getApproval", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getApproval() {
		Properties properties = getProperties();
		String approval = properties.getProperty("approval");
		List<ModelClass> list = getStatus(approval);
		return list;
	}

	/**
	 * CheckSection???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCheckSection", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCheckSection() {
		Properties properties = getProperties();
		String checkSection = properties.getProperty("checkSection");
		List<ModelClass> list = getStatus(checkSection);
		return list;
	}

	/**
	 * CheckSection???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getRound", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getRound() {
		Properties properties = getProperties();
		String round = properties.getProperty("round");
		List<ModelClass> list = getStatus(round);
		return list;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getListedCompany", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getListedCompany() {
		Properties properties = getProperties();
		String listedCompany = properties.getProperty("listedCompany");
		List<ModelClass> list = getStatus(listedCompany);
		return list;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getHousing", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getHousing() {
		Properties properties = getProperties();
		String housing = properties.getProperty("housing");
		List<ModelClass> list = getStatus(housing);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getPayOffRange", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getPayOffRange() {
		Properties properties = getProperties();
		String payOffRange = properties.getProperty("payOffRange");
		List<ModelClass> list = getStatus(payOffRange);
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getMaster", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getMaster() {
		Properties properties = getProperties();
		String master = properties.getProperty("master");
		List<ModelClass> list = getStatus(master);
		return list;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getEmployeeStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmployeeStatus() {
		Properties properties = getProperties();
		String employee = properties.getProperty("employee");
		List<ModelClass> list = getStatus(employee);
		return list;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getNewMember", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getNewMember() {
		Properties properties = getProperties();
		String newMember = properties.getProperty("newMember");
		List<ModelClass> list = getStatus(newMember);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getAccountType", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getAccountType() {
		Properties properties = getProperties();
		String accountType = properties.getProperty("accountType");
		List<ModelClass> list = getStatus(accountType);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getAccountBelongs", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getAccountBelongs() {
		Properties properties = getProperties();
		String accountBelongs = properties.getProperty("accountBelongs");
		List<ModelClass> list = getStatus(accountBelongs);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getBonus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBonus() {
		Properties properties = getProperties();
		String bonus = properties.getProperty("bonus");
		List<ModelClass> list = getStatus(bonus);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getInsurance", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getInsurance() {
		Properties properties = getProperties();
		String insurance = properties.getProperty("insurance");
		List<ModelClass> list = getStatus(insurance);
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSalesStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSalesStatus() {
		Properties properties = getProperties();
		String salesProgressCode = properties.getProperty("salesProgressCode");
		List<ModelClass> list = getStatus(salesProgressCode);
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSalesPriorityStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSalesPriorityStatus() {
		Properties properties = getProperties();
		String salesPriorityStatus = properties.getProperty("salesPriorityStatus");
		List<ModelClass> list = getStatus(salesPriorityStatus);
		return list;
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getTheSelectProjectperiodStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTheSelectProjectperiodStatus() {
		Properties properties = getProperties();
		String salesPriorityStatus = properties.getProperty("theSelectProjectperiodStatus");
		List<ModelClass> list = getStatus(salesPriorityStatus);
		return list;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSendWorkReportStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSendWorkReportStatus() {
		Properties properties = getProperties();
		String sendWorkReportStatus = properties.getProperty("sendWorkReportStatus");
		List<ModelClass> list = getStatus(sendWorkReportStatus);
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSalesPerson", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSalesPerson() {
		List<ModelClass> list = utilsService.getSalesPerson();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSalesProgress", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSalesProgress() {
		List<ModelClass> list = utilsService.getSalesProgress();
		return list;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getJapaneaseConversationLevel", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getJapaneaseConversationLevel() {
		List<ModelClass> list = utilsService.getJapaneaseConversationLevel();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEnglishConversationLevel", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEnglishConversationLevel() {
		List<ModelClass> list = utilsService.getEnglishConversationLevel();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProjectPhase", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getProjectPhase() {
		List<ModelClass> list = utilsService.getProjectPhase();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getStation", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getStation() {
		List<ModelClass> list = utilsService.getStation();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getCostClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCostClassification() {
		List<ModelClass> list = utilsService.getCostClassification();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getTransportation", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTransportation() {
		List<ModelClass> list = utilsService.getTransportation();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getTypeOfIndustry", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTypeOfIndustry() {
		List<ModelClass> list = utilsService.getTypeOfIndustry();
		return list;
	}

	public List<ModelClass> getStatus(String string) {
		JSONObject sJson = JSONObject.fromObject(string);
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) sJson;
		List<ModelClass> list = new ArrayList<ModelClass>();
		for (Entry<String, String> entry : map.entrySet()) {
			ModelClass statusModel = new ModelClass();
			statusModel.setCode(entry.getKey());
			statusModel.setName(entry.getValue());
			statusModel.setValue(entry.getKey());
			statusModel.setText(entry.getValue());
			list.add(statusModel);
		}
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getIntoCompany", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getIntoCompany() {
		List<ModelClass> list = utilsService.getIntoCompany();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSiteRole", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSiteRole() {
		List<ModelClass> list = utilsService.getSiteMaster();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCustomer", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomer() {
		List<ModelClass> list = utilsService.getCustomer();
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTopCustomer", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTopCustomer() {
		List<ModelClass> list = utilsService.getTopCustomer();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDevelopLanguage", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getDevelopLanguage() {
		List<ModelClass> list = utilsService.getDevelopLanguage();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getOccupation", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getOccupation() {
		List<ModelClass> list = utilsService.getOccupation();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDepartment", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getDepartment() {
		List<ModelClass> list = utilsService.getDepartment();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAuthority", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getAuthority() {
		List<ModelClass> list = utilsService.getAuthority();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEnglishLevel", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEnglishLevel() {
		List<ModelClass> list = utilsService.getEnglishLevel();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getQualification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getQualification() {
		List<ModelClass> list = utilsService.getQualification();
		return list;
	}

	/**
	 * ???????????????BP??????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeNameNoBP", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmployeeNameNoBP() {
		List<ModelClass> list = utilsService.getEmployeeNameNoBP();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCustomerName", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomerName() {
		List<ModelClass> list = utilsService.getCustomerName();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCustomerNameWithMail", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomerNameWithMail() {
		List<ModelClass> list = utilsService.getCustomerNameWithMail();
		return list;
	}

	/**
	 * ???????????????
	 * 
	 * @param emp
	 * @return
	 */
	public Map<String, String> getParam(EmployeeModel emp) {
		Map<String, String> sendMap = new HashMap<String, String>();
		String developmentLanguageNo1 = emp.getDevelopLanguage1();// ????????????1
		if (developmentLanguageNo1 != null && developmentLanguageNo1.length() != 0) {
			sendMap.put("developmentLanguageNo1", developmentLanguageNo1);
		}
		return sendMap;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param emp
	 * @return
	 */
	@RequestMapping(value = "/getPassword", method = RequestMethod.POST)
	@ResponseBody
	public String getPassword(@RequestBody EmployeeModel emp) {
		return utilsService.getPassword(emp.getEmployeeNo());
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeName", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmployeeName() {
		List<ModelClass> list = utilsService.getEmployeeName();
		return list;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getWorkingEmployeeNo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getWorkingEmployeeNo() {
		List<ModelClass> list = utilsService.getWorkingEmployeeNo();
		return list;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNotWorkingEmployeeNo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getNotWorkingEmployeeNo() {
		List<ModelClass> list = utilsService.getEmployeeName();
		List<ModelClass> working = utilsService.getWorkingEmployeeNo();
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < working.size(); j++) {
				if (list.get(i).getCode().equals(working.get(j).getCode())) {
					list.remove(i);
					i--;
					break;
				}
			}
		}
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param emp
	 * @return
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public boolean resetPassword(@RequestBody EmployeeModel emp) {
		HashMap<String, String> sendMap = new HashMap<>();
		sendMap.put("employeeNo", emp.getEmployeeNo());
		sendMap.put("password", emp.getPassword());
		sendMap.put("oldPassword", emp.getOldPassword());
		return utilsService.resetPassword(sendMap);
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNoBP", method = RequestMethod.POST)
	@ResponseBody
	public String getNoBP(@RequestBody ModelClass mo) {
		Map<String, String> sendMap = new HashMap<String, String>();
		String columnName = mo.getColumnName();// ??????????????????????????????
		String typeName = mo.getTypeName();// ????????????????????????
		String table = mo.getName();// ????????????
		if (columnName != null && columnName.length() != 0) {
			sendMap.put("columnName", columnName);
		}
		if (typeName != null && typeName.length() != 0) {
			sendMap.put("typeName", typeName);
		}
		if (table != null && table.length() != 0) {
			sendMap.put("table", table);
		}
		String no = utilsService.getNoBP(sendMap);
		if (no != null) {
			no = typeName + (String.format("%0" + 3 + "d", Integer.parseInt(no) + 1));
		} else {
			no = typeName + "001";
		}
		return no;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNO", method = RequestMethod.POST)
	@ResponseBody
	public String getNO(@RequestBody ModelClass mo) {
		Map<String, String> sendMap = new HashMap<String, String>();
		// sendMap.put("columnName", "customerNo");
		// sendMap.put("typeName", "C");
		// sendMap.put("table", "employee_site_information");
		String columnName = mo.getColumnName();// ??????????????????????????????
		String typeName = mo.getTypeName();// ????????????????????????
		String table = mo.getName();// ????????????
		if (columnName != null && columnName.length() != 0) {
			sendMap.put("columnName", columnName);
		}
		if (typeName != null && typeName.length() != 0) {
			sendMap.put("typeName", typeName);
		}
		if (table != null && table.length() != 0) {
			sendMap.put("table", table);
		}
		String no = utilsService.getNO(sendMap);
		return no;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNoNew", method = RequestMethod.POST)
	@ResponseBody
	public String getNoNew(@RequestBody ModelClass mo) {
		Map<String, String> sendMap = new HashMap<String, String>();
		// sendMap.put("columnName", "customerNo");
		// sendMap.put("typeName", "C");
		// sendMap.put("table", "employee_site_information");
		String columnName = mo.getColumnName();// ??????????????????????????????
		String typeName = mo.getTypeName();// ????????????????????????
		String table = mo.getName();// ????????????
		if (columnName != null && columnName.length() != 0) {
			sendMap.put("columnName", columnName);
		}
		if (typeName != null && typeName.length() != 0) {
			sendMap.put("typeName", typeName);
		}
		if (table != null && table.length() != 0) {
			sendMap.put("table", table);
		}
		String no = utilsService.getNoNew(sendMap);
		if (no != null) {
			no = typeName + (String.format("%0" + 3 + "d", Integer.parseInt(no) + 1));
		} else {
			no = typeName + "001";
		}
		return no;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNoG", method = RequestMethod.POST)
	@ResponseBody
	public String getNoG(@RequestBody ModelClass mo) {
		Map<String, String> sendMap = new HashMap<String, String>();
		// sendMap.put("columnName", "customerNo");
		// sendMap.put("typeName", "C");
		// sendMap.put("table", "employee_site_information");
		String columnName = mo.getColumnName();// ??????????????????????????????
		String typeName = mo.getTypeName();// ????????????????????????
		String table = mo.getName();// ????????????
		if (columnName != null && columnName.length() != 0) {
			sendMap.put("columnName", columnName);
		}
		if (typeName != null && typeName.length() != 0) {
			sendMap.put("typeName", typeName);
		}
		if (table != null && table.length() != 0) {
			sendMap.put("table", table);
		}
		String no = utilsService.getNoG(sendMap);
		if (no != null) {
			no = typeName + (String.format("%0" + 2 + "d", Integer.parseInt(no) + 1));
		} else {
			no = typeName + "01";
		}
		return no;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNoSP", method = RequestMethod.POST)
	@ResponseBody
	public String getNoSP(@RequestBody ModelClass mo) {
		Map<String, String> sendMap = new HashMap<String, String>();
		// sendMap.put("columnName", "customerNo");
		// sendMap.put("typeName", "C");
		// sendMap.put("table", "employee_site_information");
		String columnName = mo.getColumnName();// ??????????????????????????????
		String typeName = mo.getTypeName();// ????????????????????????
		String table = mo.getName();// ????????????
		if (columnName != null && columnName.length() != 0) {
			sendMap.put("columnName", columnName);
		}
		if (typeName != null && typeName.length() != 0) {
			sendMap.put("typeName", typeName);
		}
		if (table != null && table.length() != 0) {
			sendMap.put("table", table);
		}
		String no = utilsService.getNoSP(sendMap);
		if (no != null) {
			no = typeName + (String.format("%0" + 3 + "d", Integer.parseInt(no) + 1));
		} else {
			no = typeName + "001";
		}
		return no;
	}

	/**
	 * xml?????????
	 * 
	 * @return
	 */
	public Properties getProperties() {
		Resource resource = new ClassPathResource("system.properties");
		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	public final static String UPLOAD_PATH_PREFIX_resumeInfo = "c:/file/?????????/";
	public final static String UPLOAD_PATH_PREFIX_others = "c:/file/??????/";

	public Map<String, Object> upload(MultipartFile uploadFile, Map<String, Object> sendMap, String key, String Info) {
		if (uploadFile == null) {
			/* sendMap.put(key, ""); */
			return sendMap;
		}
		String realPath;
		if (key.equals("resumeInfo1") || key.equals("resumeInfo2")) {
			realPath = new String(UPLOAD_PATH_PREFIX_resumeInfo + sendMap.get("employeeNo") + "_"
					+ sendMap.get("employeeFristName") + sendMap.get("employeeLastName"));
		} else {
			realPath = new String(UPLOAD_PATH_PREFIX_others + sendMap.get("employeeNo") + "_"
					+ sendMap.get("employeeFristName") + sendMap.get("employeeLastName"));
		}

		File file = new File(realPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		String fileName = uploadFile.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		// String newName = sendMap.get("employeeFristName").toString() +
		// sendMap.get("employeeLastName").toString() + "_" + Info + "." + suffix;
		String alphabetName2 = sendMap.get("alphabetName2").toString().substring(0, 1).toUpperCase();
		String alphabetName3 = sendMap.get("alphabetName3").toString().length() > 0
				? sendMap.get("alphabetName3").toString().substring(0, 1).toUpperCase()
				: "";
		String newName = sendMap.get("employeeFristName").toString() + alphabetName2 + alphabetName3 + "_" + Info + "."
				+ suffix;
		try {
			File newFile = new File(file.getAbsolutePath() + File.separator + newName);
			uploadFile.transferTo(newFile);
			sendMap.put(key, realPath + "/" + newName);
			return sendMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendMap;

	}

	public static final String DOWNLOAD_PATH_BASE = "C:/file/";

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	@ResponseBody
	public void downloadTemplateFile(@RequestBody ModelClass model, HttpServletResponse response) throws IOException {
		String filePath = model.getName();
		InputStream is = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				return;
			}
			is = new FileInputStream(file);
			os = response.getOutputStream();
			bis = new BufferedInputStream(is);
			// ?????????????????????
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/octet-stream; charset=UTF-8");
			StringBuffer contentDisposition = new StringBuffer("attachment; filename=\"");
			String fileName = new String(file.getName().getBytes(), "utf-8");
			contentDisposition.append(fileName).append("\"");
			response.setHeader("Content-disposition", contentDisposition.toString());
			// ????????????
			byte[] buffer = new byte[500];
			int i;
			while ((i = bis.read(buffer)) != -1) {
				os.write(buffer, 0, i);
			}
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
				if (bis != null)
					bis.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * ????????????????????????
	 * 
	 * @param emailMod
	 */
	public ResultModel EmailSend(EmailModel emailMod) {
		ResultModel resulterr = new ResultModel();
		Session session = null;
		try {
			// ????????????????????????
			Properties properties = new Properties();
			// ????????????
			properties.setProperty("mail.debug", "true");
			// ????????????
			properties.setProperty("mail.host", "smtp.lolipop.jp");
			// ??????????????????
			properties.setProperty("mail.smtp.auth", "true");
			// ?????? ????????????
			properties.setProperty("mail.transpot.prococol", "smtp");
			// ??????????????????????????????
			properties.put("mail.smtp.port", 587);
			// ??????ssl??????????????????
			properties.setProperty("mail.smtp.ssl.enable", "smtp");
			// ??????????????????
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			// properties ?????????????????????put??????
			properties.put("Mail.smtp.ssl.socketFactory", sf);
			// ?????????????????????????????????session ---> ?????????
			session = Session.getInstance(properties);
			// ??????????????????
			Transport transport = session.getTransport();
			// ????????????
			String mailPass = utilsService.getMailPass();
			String name = mailPass.split(" ")[0];
			String pass = mailPass.split(" ")[1];
			transport.connect(name, pass);

			// ??????????????????
			Message message = new MimeMessage(session);
			// ???????????????
			message.setFrom(new InternetAddress(emailMod.getMailFrom()));
			// ??????????????????
			message.setSubject(emailMod.getMailTitle());
			// message.setContent(emailMod.getContext(), "text/html;charset=utf-8");
			
			if (emailMod.getSelectedMailCC() != null) {
				String[] addresssCC = emailMod.getSelectedMailCC();
				int lenCC = addresssCC.length;
				Address[] addsCC = new Address[lenCC];
				for (int i = 0; i < lenCC; i++) {
					addsCC[i] = new InternetAddress(addresssCC[i]);
				}
				// InternetAddress[] sendCC = new InternetAddress[] {new
				// InternetAddress("jyw.fendou@gmail.com", "", "UTF-8")};
				message.addRecipients(MimeMessage.RecipientType.CC, addsCC);
			}
			// ???multipart????????????????????????????????????????????????????????????????????????
			MimeMultipart multipart = new MimeMultipart();
			// ???????????????????????????
			MimeBodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(emailMod.getMailConfirmContont(), "text/plain;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			// multipart.addBodyPart(filePart);
			multipart.setSubType("mixed");
			// ???multipart????????????message???
			message.setContent(multipart);

			String[] addresss = emailMod.getSelectedmail().split(",");
			int len = 0;
			for (int i = 0; i < addresss.length; i++) {
				if (!addresss[i].equals(""))
					len++;
			}
			Address[] adds = new Address[len];
			len = 0;
			for (int i = 0; i < addresss.length; i++) {
				if (!addresss[i].equals("")) {
					adds[len] = new InternetAddress(addresss[i]);
					len++;
				}
			}
			message.addRecipients(MimeMessage.RecipientType.TO, adds);

			// ????????????
			transport.sendMessage(message, message.getAllRecipients());
			// transport.close();
			resulterr.setSuccess();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulterr.setErrMsg(e.getMessage());
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulterr.setErrMsg(e.getMessage());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulterr.setErrMsg(e.getMessage());
		}
		return resulterr;
	}

	/**
	 * sendMailWithFile
	 * 
	 * @param emailMod
	 * @param path
	 */
	public ResultModel sendMailWithFile(EmailModel emailMod) {
		ResultModel resultModel = new ResultModel();// ??????
		Session session = null;
		try {
			// ????????????????????????
			Properties properties = new Properties();
			// ????????????
			properties.setProperty("mail.debug", "true");
			// ????????????
			properties.setProperty("mail.host", "smtp.lolipop.jp");
			// ??????????????????
			properties.setProperty("mail.smtp.auth", "true");
			// ?????? ????????????
			properties.setProperty("mail.transpot.prococol", "smtp");
			// ??????????????????????????????
			properties.put("mail.smtp.port", 587);
			// ??????ssl??????????????????
			properties.setProperty("mail.smtp.ssl.enable", "smtp");
			// ??????????????????
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			// properties ?????????????????????put??????
			properties.put("Mail.smtp.ssl.socketFactory", sf);
			// ?????????????????????????????????session ---> ?????????
			session = Session.getInstance(properties);
			// ??????????????????
			Transport transport = session.getTransport();
			// ????????????
			String mailPass = utilsService.getMailPass();
			String name = mailPass.split(" ")[0];
			String pass = mailPass.split(" ")[1];
			transport.connect(name, pass);
			// ??????????????????
			Message message = new MimeMessage(session);
			// ???????????????
			message.setFrom(new InternetAddress(emailMod.getMailFrom()));
			// ??????????????????
			message.setSubject(emailMod.getMailTitle());
			// message.setContent(emailMod.getContext(), "text/html;charset=utf-8");
			if (emailMod.getSelectedMailCC() != null) {
				String[] addresssCC = emailMod.getSelectedMailCC();
				int lenCC = addresssCC.length;
				Address[] addsCC = new Address[lenCC];
				for (int i = 0; i < lenCC; i++) {
					addsCC[i] = new InternetAddress(addresssCC[i]);
				}
				// InternetAddress[] sendCC = new InternetAddress[] {new
				// InternetAddress("jyw.fendou@gmail.com", "", "UTF-8")};
				message.addRecipients(MimeMessage.RecipientType.CC, addsCC);
			}

			// ???multipart????????????????????????????????????????????????????????????????????????
			MimeMultipart multipart = new MimeMultipart();
			// ???????????????????????????
			MimeBodyPart contentPart = new MimeBodyPart();
			String mailConfirmContont = emailMod.getMailConfirmContont();
			contentPart.setContent(mailConfirmContont, "text/plain;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			if(emailMod.getPaths() != null && emailMod.getPaths().length != 0) {
				for (int i = 0; i < emailMod.getPaths().length; i++) {
					// ????????????
					MimeBodyPart filePart = new MimeBodyPart();
					DataSource source = new FileDataSource(emailMod.getPaths()[i]);
					// DataSource source = new
					// FileDataSource("C:\\file\\?????????\\LYC168_????????????\\??????EE_aaa.xls");
					// ?????????????????????
					filePart.setDataHandler(new DataHandler(source));
					// ?????????????????????
					String filenames = MimeUtility.encodeText(source.getName());
					filenames = filenames.replace("\\r", "").replace("\\n", "").replace(" ", "");
					filePart.setFileName(filenames);
					multipart.addBodyPart(filePart);
				}
			}
			//????????????
			MultipartFile[] files = emailMod.getFiles();
			if(files != null && files.length != 0) {
				for (int i = 0; i < files.length; i++) {
					// ????????????
					MimeBodyPart filePart = new MimeBodyPart();
					File file = MultipartFileToFile(files[i]);
					DataSource source = new FileDataSource(file);
					// ?????????????????????
					filePart.setDataHandler(new DataHandler(source));
					// ?????????????????????
					String filenames = MimeUtility.encodeText(source.getName());
					filenames = filenames.replace("\\r", "").replace("\\n", "").replace(" ", "");
					filePart.setFileName(filenames);
					multipart.addBodyPart(filePart);
				}
			}
			
			// multipart.addBodyPart(filePart);
			multipart.setSubType("mixed");
			// ???multipart????????????message???
			message.setContent(multipart);

			String[] addresss = emailMod.getSelectedmail().split(",");
			int len = 0;
			for (int i = 0; i < addresss.length; i++) {
				if (!addresss[i].equals(""))
					len++;
			}
			if(len==0) {
				resultModel.setErrMsg("Invalid Addresses");
				return resultModel;
			}
			Address[] adds = new Address[len];
			len = 0;
			for (int i = 0; i < addresss.length; i++) {
				if (!addresss[i].equals("")) {
					adds[len] = new InternetAddress(addresss[i]);
					len++;
				}
			}
			message.addRecipients(MimeMessage.RecipientType.TO, adds);

			// ????????????
			transport.sendMessage(message, message.getAllRecipients());
			// transport.close();
			resultModel.setSuccess();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultModel.setErrMsg(e.getMessage());
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultModel.setErrMsg(e.getMessage());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultModel.setErrMsg(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultModel.setErrMsg(e.getMessage());
		}
		return resultModel;
	}
	
	/**
     * ???MultipartFile?????????File
     * @param multiFile
     * @return
     */
    private File MultipartFileToFile(MultipartFile multiFile) {
        // ???????????????
        String fileName = multiFile.getOriginalFilename();
        // ??????????????????
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // ??????????????????????????????????????????,????????????????????????????????????
        try {
            File file = File.createTempFile(fileName, prefix);
            multiFile.transferTo(file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


	/**
	 * enterPeriod???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEnterPeriod", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEnterPeriod() {
		Properties properties = getProperties();
		String enterPeriod = properties.getProperty("enterPeriod");
		List<ModelClass> list = getStatus(enterPeriod);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @param startTime ??? ????????????
	 * @param endTime   ??? ????????????
	 * @return
	 */
	public static int caculateTotalTime(String startTime, String endTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		Date date = null;
		Long l = 0L;
		try {
			date = formatter.parse(startTime);
			long ts = date.getTime();
			date1 = formatter.parse(endTime);
			long ts1 = date1.getTime();

			l = (ts - ts1) / (1000 * 60 * 60 * 24);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l.intValue();
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSituationChange", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSituationChange() {

		List<ModelClass> list = utilsService.getSituationChange();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNonSiteClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getNonSiteClassification() {
		List<ModelClass> list = utilsService.getNonSiteClassification();
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSuccessRate", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSuccessRate() {
		List<ModelClass> list = utilsService.getSuccessRate();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAgeClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getAgeClassification() {
		List<ModelClass> list = utilsService.getAgeClassification();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAdmissionMonth", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getAdmissionMonth() {
		List<ModelClass> list = utilsService.getAdmissionMonth();
		return list;
	}

	/**
	 * ??????????????????????????? ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getStorageListName", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getStorageListName() {
		List<ModelClass> list = utilsService.getStorageListName();
		return list;
	}

	/**
	 * ???????????????????????????????????? ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getStorageListName0", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getStorageListName0() {
		List<ModelClass> list = utilsService.getStorageListName0();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDealDistinction", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getDealDistinction() {
		List<ModelClass> list = utilsService.getDealDistinction();
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFrameWork", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getFrameWork() {
		List<ModelClass> list = utilsService.getFramework();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProposeClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getProposeClassification() {
		List<ModelClass> list = utilsService.getProposeClassification();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getInterviewClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getInterviewClassification() {
		List<ModelClass> list = utilsService.getInterviewClassification();
		return list;
	}

	/**
	 * ???????????????????????? ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPurchasingManagers", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getPurchasingManagers() {
		List<ModelClass> list = utilsService.getPurchasingManagers();
		return list;
	}

	/**
	 * ???????????????????????? ??????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPurchasingManagersWithMail", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getPurchasingManagersWithMail() {
		List<ModelClass> list = utilsService.getPurchasingManagersWithMail();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTypteOfContract", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTypteOfContract() {
		List<ModelClass> list = utilsService.getTypteOfContract();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getMarriageClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getMarriageClassification() {
		List<ModelClass> list = utilsService.getMarriageClassification();
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getRetirementResonClassification", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getRetirementResonClassification() {
		List<ModelClass> list = utilsService.getRetirementResonClassification();
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getEmploymentInsuranceStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmploymentInsuranceStatus() {
		Properties properties = getProperties();
		String employmentInsuranceStatus = properties.getProperty("employmentInsuranceStatus");
		List<ModelClass> list = getStatus(employmentInsuranceStatus);
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getSocialInsuranceStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSocialInsuranceStatus() {
		Properties properties = getProperties();
		String socialInsuranceStatus = properties.getProperty("socialInsuranceStatus");
		List<ModelClass> list = getStatus(socialInsuranceStatus);
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getBasicContractStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getBasicContractStatus() {
		Properties properties = getProperties();
		String basicContractStatus = properties.getProperty("basicContractStatus");
		List<ModelClass> list = getStatus(basicContractStatus);
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getWorkingConditionStatus", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getWorkingConditionStatus() {
		Properties properties = getProperties();
		String workingConditionStatus = properties.getProperty("workingConditionStatus");
		List<ModelClass> list = getStatus(workingConditionStatus);
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getCustomerAbbreviation", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getCustomerAbbreviation() {
		List<ModelClass> list = utilsService.getCustomerAbbreviation();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getNoOfInterview", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getNoOfInterview() {
		List<ModelClass> list = utilsService.getNoOfInterview();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProjectPeriod", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getProjectPeriod() {
		List<ModelClass> list = utilsService.getProjectPeriod();
		return list;
	}

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProjectType", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getProjectType() {
		List<ModelClass> list = utilsService.getProjectType();
		return list;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProjectNo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getProjectNo() {
		List<ModelClass> list = utilsService.getProjectNo();
		return list;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getSendReportOfDateSeting", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getSendReportOfDateSeting() {
		List<ModelClass> list = utilsService.getSendReportOfDateSeting();
		return list;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeNameByOccupationName", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getEmployeeNameByOccupationName() {
		List<ModelClass> list = utilsService.getEmployeeNameByOccupationName();
		return list;
	}

	/**
	 * serverIP
	 * 
	 * @return
	 */

	@RequestMapping(value = "/getServerIP", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getServerIP() {
		Properties properties = getProperties();
		String serverIP = properties.getProperty("serverIP");
		List<ModelClass> list = getStatus(serverIP);
		return list;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTransaction", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> getTransaction() {
		Properties properties = getProperties();
		String transaction = properties.getProperty("transaction");
		List<ModelClass> list = getStatus(transaction);
		return list;
	}

	/**
	 * 0130 -> 01:30
	 * 
	 * @param time(string)
	 * @param inputChar(string default->:)
	 * @return String
	 */
	public static String TimeInsertChar(String time, String inputChar) {
		return StringUtils.isEmpty(time) ? "" : time.substring(0, 2) + inputChar + time.substring(2, 4);
	}

	/**
	 * 0130 -> 01:30
	 * 
	 * @param time(string)
	 * @param inputChar(string default->:)
	 * @return String
	 */
	public static String TimeInsertChar(String time) {
		return TimeInsertChar(time, ":");
	}

	/**
	 * ?????????
	 * 
	 * @param timestamp in Millis
	 * @return Boolean
	 */
	public static Boolean isHoliday(String date) {
		return isHoliday(date, "yyyy-MM-dd");
	}

	/**
	 * ?????????
	 * 
	 * @param dateString date
	 * @param dateFormat date format
	 * @return Boolean
	 */
	public static Boolean isHoliday(String dateString, String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Date date = new Date();
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		return JapanHoliday.containsKey(date) || week == Calendar.SUNDAY || week == Calendar.SATURDAY;
	}

	/**
	 * ????????? / ??????????????? yyyymmdd -> yyyy/mm/dd yyyymm -> yyyy/mm
	 * 
	 * @param date String
	 * @return date String
	 */
	public static String dateAddOblique(String date) {
		switch (date.length()) {
		case 6:
			date = date.substring(0, 4) + "/" + date.substring(4, 6);
			break;
		case 8:
			date = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
			break;
		default:
			break;
		}
		return date;
	}

	/**
	 * ??????????????????????????? 00000000000 -> 000-0000-0000
	 * 
	 * @param phoneNo String
	 * @return phoneNo String
	 */
	public static String dateAddDash(String phoneNo) {
		switch (phoneNo.length()) {
		case 11:
			phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3, 7) + "-" + phoneNo.substring(7, 11);
			break;
		default:
			break;
		}
		return phoneNo;
	}
}
