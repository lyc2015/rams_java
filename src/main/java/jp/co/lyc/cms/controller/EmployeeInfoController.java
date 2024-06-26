package jp.co.lyc.cms.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.AccountInfoModel;
import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.model.EmployeeInfoCsvModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.service.EmployeeInfoService;
import jp.co.lyc.cms.service.WagesInfoService;
import jp.co.lyc.cms.util.UtilsController;
import jp.co.lyc.cms.validation.EmployeeInfoValidation;

@Controller
@RequestMapping(value = "/employee")
public class EmployeeInfoController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EmployeeInfoService employeeInfoService;

	@Autowired
	WagesInfoService wagesInfoService;

	@Autowired
	UtilsController utilsController;

	@Autowired
	S3Controller s3Controller;

	/**
	 * データを取得
	 * 
	 * @param emp
	 * @return List
	 */
	String errorsMessage = "";;

	@RequestMapping(value = "/getEmployeeInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getEmployeeInfo(@RequestBody EmployeeModel emp) {
		logger.info("GetEmployeeInfoController.getEmployeeInfo:" + "検索開始");
		errorsMessage = "";
		DataBinder binder = new DataBinder(emp);
		binder.setValidator(new EmployeeInfoValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		Map<String, Object> result = new HashMap<>();
		if (results.hasErrors()) {
			results.getAllErrors().forEach(o -> {
				FieldError error = (FieldError) o;
				errorsMessage += error.getDefaultMessage();// エラーメッセージ
			});
			result.put("errorsMessage", errorsMessage);// エラーメッセージ
			return result;
		}

		List<EmployeeModel> employeeList = new ArrayList<EmployeeModel>();
		List<EmployeeModel> employeeListTemp = new ArrayList<EmployeeModel>();
		List<String> employeeNoList = new ArrayList<String>();
		try {
			Map<String, Object> sendMap = getParam(emp);
			employeeList = employeeInfoService.getEmployeeInfo(sendMap);

			if (employeeList.size() <= 0) {
				result.put("isNullMessage", "該当データなし");// 該当データがなし
				result.put("data", employeeList);
				return result;
			}

			/*
			 * if (emp.getKadou() != null && emp.getKadou().equals("1")) { employeeNoList =
			 * employeeInfoService.verificationEmployeeInfo(); for (int i = 0; i <
			 * employeeList.size(); i++) { for (int j = 0; j < employeeNoList.size(); j++) {
			 * if (employeeList.get(i).getEmployeeNo().equals(employeeNoList.get(j))) {
			 * employeeList.remove(i); i = 0; break; } } } }
			 */

			for (int i = 0; i < employeeList.size(); i++) {
				// 名前
				employeeList.get(i).setEmployeeName(
						employeeList.get(i).getEmployeeFristName() + " " + employeeList.get(i).getEmployeeLastName());

				// 電話番号
				String phoneNo = employeeList.get(i).getPhoneNo();
				if (phoneNo != null && phoneNo.length() >= 11) {
					phoneNo = UtilsController.dateAddDash(phoneNo);
				}
				employeeList.get(i).setPhoneNo(phoneNo);

				// 年齢
				String birthday = employeeList.get(i).getBirthday();
				if (birthday != null && birthday.length() >= 8) {
					SimpleDateFormat sft = new SimpleDateFormat("yyyyMMdd");
					Date date = sft.parse(birthday);
					int age = EmployeeInfoController.getAgeByBirth(date);
					birthday = UtilsController.dateAddOblique(birthday) + "(" + Integer.toString(age) + ")";
				}
				employeeList.get(i).setBirthday(birthday);

				// 入社年月
				String intoCompanyYearAndMonth = employeeList.get(i).getIntoCompanyYearAndMonth();
				if (intoCompanyYearAndMonth != null && intoCompanyYearAndMonth.length() >= 6) {
					intoCompanyYearAndMonth = UtilsController.dateAddOblique(intoCompanyYearAndMonth);
				}
				employeeList.get(i).setIntoCompanyYearAndMonth(intoCompanyYearAndMonth);

				// ローマ字
				if (employeeList.get(i).getAlphabetName() != null) {
					String alphabetName = employeeList.get(i).getAlphabetName();
					if (alphabetName.split(" ").length > 2) {
						String[] temp = alphabetName.split(" ");
						alphabetName = temp[0] + " " + temp[1] + temp[2];
						employeeList.get(i).setAlphabetName(alphabetName);
					}
				}
			}

			for (int i = 0; i < employeeList.size(); i++) {
				if (employeeList.get(i).getEmployeeNo().substring(3, 4).equals("G")) {
					employeeListTemp.add(employeeList.get(i));
					employeeList.remove(i);
					i--;
				}
			}
			for (int i = 0; i < employeeList.size(); i++) {
				if (!employeeList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
					employeeListTemp.add(employeeList.get(i));
					employeeList.remove(i);
					i--;
				}
			}
			employeeListTemp.addAll(employeeList);
			for (int i = 0; i < employeeListTemp.size(); i++) {
				// 番号
				employeeListTemp.get(i).setRowNo(i + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("data", employeeListTemp);
		logger.info("GetEmployeeInfoController.getEmployeeInfo:" + "検索結束");
		return result;
	}

	/**
	 * 社員情報を追加
	 * 
	 * @param emp
	 * @return boolean
	 */
	@RequestMapping(value = "/insertEmployee", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertEmployee(@RequestParam(value = "emp", required = false) String JSONEmp,
			@RequestParam(value = "resumeInfo1", required = false) MultipartFile resumeInfo1,
			@RequestParam(value = "resumeInfo2", required = false) MultipartFile resumeInfo2,
			@RequestParam(value = "residentCardInfo", required = false) MultipartFile residentCardInfo,
			@RequestParam(value = "passportInfo", required = false) MultipartFile passportInfo) throws Exception {
		logger.info("GetEmployeeInfoController.insertEmployee:" + "追加開始");
		errorsMessage = "を入力してください。";
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		EmployeeModel emp = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<EmployeeModel>() {
		});
		if (resumeInfo1 != null) {
			emp.setResumeInfo1(resumeInfo1.getOriginalFilename());
		}
		Map<String, Object> resultMap = new HashMap<>();// 戻す

		/*
		 * DataBinder binder = new DataBinder(emp); binder.setValidator(new
		 * EmployeeInfoValidation()); binder.validate(); BindingResult results =
		 * binder.getBindingResult();
		 * 
		 * if (results.hasErrors()) { results.getAllErrors().forEach(o -> { FieldError
		 * error = (FieldError) o; errorsMessage += error.getDefaultMessage();//
		 * エラーメッセージ }); resultMap.put("errorsMessage", errorsMessage);// エラーメッセージ return
		 * resultMap; }
		 */
		if (emp.getNationalityCode() == null || emp.getNationalityCode().equals("")) {
			errorsMessage = "国籍 " + errorsMessage;
		}
		if (emp.getIntoCompanyYearAndMonth() == null || emp.getIntoCompanyYearAndMonth().equals("")) {
			errorsMessage = "入社年月 " + errorsMessage;
		}

		if (emp.getGenderStatus() == null || emp.getGenderStatus().equals("")) {
			errorsMessage = "性別  " + errorsMessage;
		}

		if (emp.getAlphabetName1() == null || emp.getAlphabetName1().equals("") || emp.getAlphabetName2() == null
				|| emp.getAlphabetName2().equals("")) {
			errorsMessage = "ローマ字  " + errorsMessage;
		}

		if (emp.getEmployeeFristName() == null || emp.getEmployeeFristName().equals("")
				|| emp.getEmployeeLastName() == null || emp.getEmployeeLastName().equals("")) {
			errorsMessage = "社員名  " + errorsMessage;
		}

		if (emp.getEmployeeFristName() == null || emp.getEmployeeFristName().equals("")
				|| emp.getEmployeeLastName() == null || emp.getEmployeeLastName().equals("")
				|| emp.getAlphabetName1() == null || emp.getAlphabetName1().equals("") || emp.getAlphabetName2() == null
				|| emp.getAlphabetName2().equals("") || emp.getGenderStatus() == null
				|| emp.getGenderStatus().equals("") || emp.getIntoCompanyYearAndMonth() == null
				|| emp.getIntoCompanyYearAndMonth().equals("") || emp.getNationalityCode() == null
				|| emp.getNationalityCode().equals("")) {
			resultMap.put("errorsMessage", errorsMessage);
			return resultMap;
		}

		if (resumeInfo1 != null && emp.getResumeName1().equals("")) {
			resultMap.put("errorsMessage", "履歴書1名を入力してください。");
			return resultMap;
		}

		if (resumeInfo2 != null && emp.getResumeName2().equals("")) {
			resultMap.put("errorsMessage", "履歴書2名を入力してください。");
			return resultMap;
		}

		Map<String, Object> sendMap = getParam(emp);// パラメータ
		boolean result = true;
		try {
			sendMap = utilsController.upload(resumeInfo1, sendMap, "resumeInfo1", emp.getResumeName1());
			sendMap = utilsController.upload(resumeInfo2, sendMap, "resumeInfo2", emp.getResumeName2());
			sendMap = utilsController.upload(residentCardInfo, sendMap, "residentCardInfo", "在留カード");
			sendMap = utilsController.upload(passportInfo, sendMap, "passportInfo", "パスポート");
			S3Model s3Model = new S3Model();
			if (sendMap.get("resumeInfo1") != null && sendMap.get("resumeInfo1") != "") {
				String filePath = sendMap.get("resumeInfo1").toString();
				String fileKey = filePath.split("file/")[1];
				s3Model.setFileKey(fileKey);
				s3Model.setFilePath(filePath);
				s3Controller.uploadFile(s3Model);
			}
			if (sendMap.get("resumeInfo2") != null && sendMap.get("resumeInfo2") != "") {
				String filePath = sendMap.get("resumeInfo2").toString();
				String fileKey = filePath.split("file/")[1];
				s3Model.setFileKey(fileKey);
				s3Model.setFilePath(filePath);
				s3Controller.uploadFile(s3Model);
			}
			result = employeeInfoService.insertEmployee((HashMap<String, Object>) sendMap);

		} catch (Exception e) {
			resultMap.put("result", false);
			return resultMap;
		}
		resultMap.put("result", result);
		logger.info("GetEmployeeInfoController.insertEmployee:" + "追加結束");
		return resultMap;
	}

	/**
	 * 社員情報を削除
	 * 
	 * @param emp
	 * @return boolean
	 */
	@RequestMapping(value = "/deleteEmployeeInfo", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteEmployeeInfo(@RequestBody EmployeeModel emp) throws Exception {
		logger.info("GetEmployeeInfoController.deleteEmployeeInfo:" + "削除開始");

		Map<String, Object> sendMap = getParam(emp);
		boolean result = true;
		EmployeeModel model = employeeInfoService.getEmployeeByEmployeeNo(sendMap);
		String folderKey = "履歴書/" + model.getEmployeeNo() + "_" + model.getEmployeeFristName()
				+ model.getEmployeeLastName();
		result = employeeInfoService.deleteEmployeeInfo(sendMap);
		if (result) {
			// 自分のファイルを削除
			// utilsController.deleteDir(emp.getResidentCardInfo());
			S3Model s3Model = new S3Model();
			s3Model.setFolderKey(folderKey);
			s3Controller.deleteFolder(s3Model);
		}
		logger.info("GetEmployeeInfoController.deleteEmployeeInfo:" + "削除結束");
		return result;
	}

	/**
	 * EmployeeNoによると、社員情報を取得
	 * 
	 * @param emp
	 * @return EmployeeModel
	 */
	@RequestMapping(value = "/getEmployeeByEmployeeNo", method = RequestMethod.POST)
	@ResponseBody
	public EmployeeModel getEmployeeByEmployeeNo(@RequestBody EmployeeModel emp) throws Exception {
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "EmployeeNoによると、社員情報を取得開始");
		Map<String, Object> sendMap = getParam(emp);
		EmployeeModel model = employeeInfoService.getEmployeeByEmployeeNo(sendMap);
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "EmployeeNoによると、社員情報を取得結束");
		return model;
	}

	/**
	 * BP情報update
	 * 
	 * @param bpInfo
	 * @return
	 */
	@RequestMapping(value = "/updatebpInfo", method = RequestMethod.POST)
	@ResponseBody
	public void updatebpInfo(@RequestBody BpInfoModel bpInfoModel) throws Exception {
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "updatebpInfo開始");
		bpInfoModel.setUpdateUser(getSession().getAttribute("employeeName").toString());
		employeeInfoService.updatebpInfo(bpInfoModel);
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "updatebpInfo結束");
	}

	/**
	 * BP情報delete
	 * 
	 * @param bpInfo
	 * @return
	 */
	@RequestMapping(value = "/deletebpInfo", method = RequestMethod.POST)
	@ResponseBody
	public void deletebpInfo(@RequestBody BpInfoModel bpInfoModel) throws Exception {
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "deletebpInfo開始");
		employeeInfoService.deletebpInfo(bpInfoModel);
		logger.info("GetEmployeeInfoController.getEmployeeByEmployeeNo:" + "deletebpInfo結束");
	}

	/**
	 * 社員情報csv出力
	 * 
	 * @param employeeNo
	 * @return boolean
	 */
	@RequestMapping(value = "/csvDownload", method = RequestMethod.POST)
	@ResponseBody
	public List<EmployeeInfoCsvModel> csvDownload(@RequestBody List<String> employeeNo) throws Exception {
		logger.info("GetEmployeeInfoController.csvDownload:" + "csvDownloadによると、社員情報csvを出力開始");
		List<EmployeeInfoCsvModel> employeeList = new ArrayList<EmployeeInfoCsvModel>();
		List<EmployeeInfoCsvModel> employeeTempList = new ArrayList<EmployeeInfoCsvModel>();

		employeeList = employeeInfoService.getEmployeesCSV(employeeNo);
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getBirthday().equals("")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				employeeList.get(i)
						.setAge(String.valueOf(getAgeByBirth(dateFormat.parse(employeeList.get(i).getBirthday()))));
			} else {
				employeeList.get(i).setAge("");
			}
			if (!employeeList.get(i).getStayPeriod().equals("")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				employeeList.get(i).setStayPeriodYears(
						String.valueOf(getAgeByBirth(dateFormat.parse(employeeList.get(i).getStayPeriod()))));
			} else {
				employeeList.get(i).setStayPeriodYears("");
			}
			if (!employeeList.get(i).getIntoCompanyYearAndMonth().equals("")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
				employeeList.get(i).setIntoCompanyYears(String
						.valueOf(getAgeByBirth(dateFormat.parse(employeeList.get(i).getIntoCompanyYearAndMonth()))));
			} else {
				employeeList.get(i).setIntoCompanyYears("");
			}
		}
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				employeeTempList.add(employeeList.get(i));
				employeeList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < employeeList.size(); i++) {
			employeeTempList.add(employeeList.get(i));
		}
		logger.info("GetEmployeeInfoController.csvDownload:" + "csvDownloadによると、社員情報csvを出力結束");
		return employeeTempList;
	}

	/**
	 * 社員情報を修正
	 * 
	 * @param emp
	 * @return boolean
	 */

	@RequestMapping(value = "/updateEmployee", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateEmployee(@RequestParam(value = "emp", required = false) String JSONEmp,
			@RequestParam(value = "resumeInfo1", required = false) MultipartFile resumeInfo1,
			@RequestParam(value = "resumeInfo2", required = false) MultipartFile resumeInfo2,
			@RequestParam(value = "residentCardInfo", required = false) MultipartFile residentCardInfo,
			@RequestParam(value = "passportInfo", required = false) MultipartFile passportInfo,
			@RequestParam(value = "resumeInfo1URL", required = false) String resumeInfo1URL,
			@RequestParam(value = "resumeInfo1Key", required = false) String resumeInfo1Key,
			@RequestParam(value = "resumeInfo2URL", required = false) String resumeInfo2URL,
			/*
			 * @RequestParam(value = "resumeInfo2Key", required = false) String
			 * resumeInfo2Key,
			 */
			@RequestParam(value = "residentCardInfoURL", required = false) String residentCardInfoURL,
			@RequestParam(value = "passportInfoURL", required = false) String passportInfoURL) throws Exception {
		logger.info("GetEmployeeInfoController.updateEmployee:" + "修正開始");
		errorsMessage = "を入力してください。";
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		EmployeeModel emp = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<EmployeeModel>() {
		});

		if (resumeInfo1 != null) {
			emp.setResumeInfo1(resumeInfo1.getOriginalFilename());
		}
		Map<String, Object> resultMap = new HashMap<>();// 繰り返し

		/*
		 * DataBinder binder = new DataBinder(emp); binder.setValidator(new
		 * EmployeeInfoValidation()); binder.validate(); BindingResult results =
		 * binder.getBindingResult();
		 * 
		 * if (results.hasErrors()) { results.getAllErrors().forEach(o -> { FieldError
		 * error = (FieldError) o; errorsMessage += error.getDefaultMessage();//
		 * エラーメッセージ }); resultMap.put("errorsMessage", errorsMessage);// エラーメッセージ return
		 * resultMap; }
		 */

		if (emp.getNationalityCode() == null || emp.getNationalityCode().equals("")) {
			errorsMessage = "国籍 " + errorsMessage;
		}
		if (emp.getIntoCompanyYearAndMonth() == null || emp.getIntoCompanyYearAndMonth().equals("")) {
			errorsMessage = "入社年月 " + errorsMessage;
		}

		if (emp.getGenderStatus() == null || emp.getGenderStatus().equals("")) {
			errorsMessage = "性別  " + errorsMessage;
		}

		if (emp.getAlphabetName1() == null || emp.getAlphabetName1().equals("") || emp.getAlphabetName2() == null
				|| emp.getAlphabetName2().equals("")) {
			errorsMessage = "ローマ字  " + errorsMessage;
		}

		if (emp.getEmployeeFristName() == null || emp.getEmployeeFristName().equals("")
				|| emp.getEmployeeLastName() == null || emp.getEmployeeLastName().equals("")) {
			errorsMessage = "社員名  " + errorsMessage;
		}

		if (emp.getEmployeeFristName() == null || emp.getEmployeeFristName().equals("")
				|| emp.getEmployeeLastName() == null || emp.getEmployeeLastName().equals("")
				|| emp.getAlphabetName1() == null || emp.getAlphabetName1().equals("") || emp.getAlphabetName2() == null
				|| emp.getAlphabetName2().equals("") || emp.getGenderStatus() == null
				|| emp.getGenderStatus().equals("") || emp.getIntoCompanyYearAndMonth() == null
				|| emp.getIntoCompanyYearAndMonth().equals("") || emp.getNationalityCode() == null
				|| emp.getNationalityCode().equals("")) {
			resultMap.put("errorsMessage", errorsMessage);
			return resultMap;
		}

		if (resumeInfo1 != null && emp.getResumeName1().equals("")) {
			resultMap.put("errorsMessage", "履歴書1名を入力してください。");
			return resultMap;
		}

		if (resumeInfo2 != null && emp.getResumeName2().equals("")) {
			resultMap.put("errorsMessage", "履歴書2名を入力してください。");
			return resultMap;
		}

		Map<String, Object> sendMap = getParam(emp);
		boolean result = true;
		try {
			sendMap = utilsController.upload(resumeInfo1, sendMap, "resumeInfo1", emp.getResumeName1());
			sendMap = utilsController.upload(resumeInfo2, sendMap, "resumeInfo2", emp.getResumeName2());
			sendMap = utilsController.upload(residentCardInfo, sendMap, "residentCardInfo", "在留カード");
			sendMap = utilsController.upload(passportInfo, sendMap, "passportInfo", "パスポート");
			S3Model s3Model = new S3Model();
			if (sendMap.get("resumeInfo1") != null && sendMap.get("resumeInfo1") != "") {
				if (!resumeInfo1URL.equals("")) {
					String deletefileKey = resumeInfo1URL.split("/file/")[1];
					s3Model.setFileKey(deletefileKey);
					s3Controller.deleteFile(s3Model);
				}
				String filePath = sendMap.get("resumeInfo1").toString();
				String fileKey = filePath.split("file/")[1];
				s3Model.setFileKey(fileKey);
				s3Model.setFilePath(filePath);
				s3Controller.uploadFile(s3Model);
			} else {
				if (!resumeInfo1Key.equals("") && emp.getResumeName1().equals("")) {
					String deletefileKey = resumeInfo1Key.split("/file/")[1];
					s3Model.setFileKey(deletefileKey);
					s3Controller.deleteFile(s3Model);
				}
			}
			/*
			 * if (sendMap.get("resumeInfo2") != null && sendMap.get("resumeInfo2") != "") {
			 * if (!resumeInfo2URL.equals("")) { String deletefileKey =
			 * resumeInfo2URL.split("/file/")[1]; s3Model.setFileKey(deletefileKey);
			 * s3Controller.deleteFile(s3Model); } String filePath =
			 * sendMap.get("resumeInfo2").toString(); String fileKey =
			 * filePath.split("file/")[1]; s3Model.setFileKey(fileKey);
			 * s3Model.setFilePath(filePath); s3Controller.uploadFile(s3Model); } else { if
			 * (!StringUtils.isBlank(resumeInfo2Key) &&
			 * StringUtils.isBlank(emp.getResumeName2())) { String deletefileKey =
			 * resumeInfo2Key.split("/file/")[1]; s3Model.setFileKey(deletefileKey);
			 * s3Controller.deleteFile(s3Model); } }
			 */
			/*
			 * if (StringUtils.isBlank(emp.getResumeName1()) &&
			 * StringUtils.isBlank(emp.getResumeName2())) { if (!resumeInfo2Key.equals(""))
			 * { String deletefileKey = resumeInfo2Key.split("/file/")[1].substring(0,
			 * resumeInfo2Key.split("/file/")[1].lastIndexOf("/") + 1);
			 * s3Model.setFileKey(deletefileKey); s3Controller.deleteFolder(s3Model); } if
			 * (!resumeInfo1Key.equals("")) { String deletefileKey =
			 * resumeInfo1Key.split("/file/")[1].substring(0,
			 * resumeInfo2Key.split("/file/")[1].lastIndexOf("/") + 1);
			 * s3Model.setFileKey(deletefileKey); s3Controller.deleteFolder(s3Model); } }
			 */
			result = employeeInfoService.updateEmployee(sendMap);
			if (!emp.getNewEmployeeNo().equals(emp.getEmployeeNo())) {
				wagesInfoService.updateEmpInfo(emp.getEmployeeNo(), emp.getNewEmployeeNo());
			}
		} catch (Exception e) {
			resultMap.put("result", false);
			resultMap.put("errorsMessage", "修正異常");
			logger.info("GetEmployeeInfoController.updateEmployee:" + "修正異常" + e);
			return resultMap;
		}
		resultMap.put("result", result);
		resultMap.put("residentCardInfo", sendMap.get("residentCardInfo"));
		resultMap.put("passportInfo", sendMap.get("passportInfo"));
		resultMap.put("resumeInfo1", sendMap.get("resumeInfo1"));
		resultMap.put("resumeInfo2", sendMap.get("resumeInfo2"));
		logger.info("GetEmployeeInfoController.updateEmployee:" + "修正結束");
		return resultMap;
	}

	/**
	 * 社員情報を修正
	 * 
	 * @param emp
	 * @return boolean
	 */

	@RequestMapping(value = "/updatePassWord", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updatePassWord(@RequestParam(value = "emp", required = false) String JSONEmp)
			throws Exception {
		logger.info("GetEmployeeInfoController.updateEmployee:" + "修正開始");
		errorsMessage = "を入力してください。";
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		EmployeeModel emp = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<EmployeeModel>() {
		});

		Map<String, Object> resultMap = new HashMap<>();// 繰り返し

		Map<String, Object> sendMap = getParam(emp);
		boolean result = true;
		try {
			result = employeeInfoService.updatePassword(sendMap);
		} catch (Exception e) {
			resultMap.put("result", false);
			return resultMap;
		}
		resultMap.put("result", result);
		logger.info("GetEmployeeInfoController.updateEmployee:" + "修正結束");
		return resultMap;
	}

	/**
	 * 条件を取得
	 * 
	 * @param emp
	 * @return
	 */
	public Map<String, Object> getParam(EmployeeModel emp) {
		HttpSession loginSession = getSession();
		Map<String, Object> sendMap = new HashMap<String, Object>();
		String newEmployeeNo = emp.getNewEmployeeNo();// 社員番号
		String employeeNo = emp.getEmployeeNo();// 社員番号
		String employeeFristName = emp.getEmployeeFristName();// 社員氏
		String employeeLastName = emp.getEmployeeLastName();// 社員名
		String furigana = (emp.getFurigana1() != null ? emp.getFurigana1() : "") + " "
				+ (emp.getFurigana2() != null ? emp.getFurigana2() : "");// カタカナ
		String alphabetName = (emp.getAlphabetName1() != null ? emp.getAlphabetName1() : "") + " "
				+ (emp.getAlphabetName2() != null ? emp.getAlphabetName2() : "")
				+ (emp.getAlphabetName3() != null ? " " + emp.getAlphabetName3() : "");// カタカナ
		String alphabetName2 = emp.getAlphabetName2() != null ? emp.getAlphabetName2() : "";
		String alphabetName3 = emp.getAlphabetName3() != null ? emp.getAlphabetName3() : "";
		String birthday = emp.getBirthday();// 年齢
		String genderStatus = emp.getGenderStatus();// 性別ステータス
		String intoCompanyCode = emp.getIntoCompanyCode();// 入社区分
		String employeeFormCode = emp.getEmployeeFormCode();// 社員形式
		String japaneseCalendar = emp.getJapaneseCalendar();// 和暦
		String occupationCode = emp.getOccupationCode();// 職種コード
		String departmentCode = emp.getDepartmentCode();// 部署
		String companyMail = emp.getCompanyMail();// 社内メール
		String graduationUniversity = emp.getGraduationUniversity();// 卒業学校
		String major = emp.getMajor();// 専門
		String graduationYearAndMonth = emp.getGraduationYearAndMonth();// 卒業年月
		String introducer = emp.getIntroducer();// 紹介者
		Boolean resume1Changed = emp.getResume1Changed() != null ? emp.getResume1Changed() : false;

		// 入社年月
		String intoCompanyYearAndMonth = null;
		if (emp.getIntoCompanyYearAndMonth() != null) {
			intoCompanyYearAndMonth = emp.getIntoCompanyYearAndMonth().equals(" ") ? ""
					: emp.getIntoCompanyYearAndMonth();// 入社年月
		}

		String retirementYearAndMonth = emp.getRetirementYearAndMonth();// 退職年月
		String retirementResonClassification = emp.getRetirementResonClassification();// 退職区分
		String comeToJapanYearAndMonth = emp.getComeToJapanYearAndMonth();// 来日年月
		String nationalityCode = emp.getNationalityCode();// 出身地コード(国)
		String birthplace = emp.getBirthplace();// 出身地(県)
		String phoneNo = emp.getPhoneNo();// 携帯電話
		String japaneseLevelCode = emp.getJapaneseLevelCode();// 日本語のレベル
		String englishLevelCode = emp.getEnglishLevelCode();// 英語
		String certification1 = emp.getCertification1();// 資格1
		String certification2 = emp.getCertification2();// 資格2
		String developLanguage1 = emp.getDevelopLanguage1();// 技術语言1
		String developLanguage2 = emp.getDevelopLanguage2();// 技術语言2
		String developLanguage3 = emp.getDevelopLanguage3();// 技術语言3
		String developLanguage4 = emp.getDevelopLanguage4();// 技術语言4
		String developLanguage5 = emp.getDevelopLanguage5();// 技術语言5
		String frameWork1 = emp.getFrameWork1();// フレームワーク1
		String frameWork2 = emp.getFrameWork2();// フレームワーク2
		String residenceCode = emp.getResidenceCode();// 在留資格
		String residenceCardNo = emp.getResidenceCardNo();// 在留カード
		String stayPeriod = emp.getStayPeriod();// 在留期間
		String passportStayPeriod = emp.getPassportStayPeriod();// パスポート期間
		String immigrationStartTime = emp.getImmigrationStartTime();// 出入国届開始時間
		String immigrationEndTime = emp.getImmigrationEndTime();// 出入国届終了時間
		String contractDeadline = emp.getContractDeadline();// 契約期限
		String employmentInsuranceStatus = emp.getEmploymentInsuranceStatus();// 雇用保険加入
		String employmentInsuranceNo = emp.getEmploymentInsuranceNo();// 雇用保険番号
		String socialInsuranceStatus = emp.getSocialInsuranceStatus();// 社会保険加入
		String socialInsuranceNo = emp.getSocialInsuranceNo();// 社会保険番号
		String socialInsuranceDate = emp.getSocialInsuranceDate();// 社会保険時間
		String myNumber = emp.getMyNumber();// マイナンバー
		String resumeName1 = emp.getResumeName1();// 備考１
		String resumeName2 = emp.getResumeName2();// 備考２
		String passportNo = emp.getPassportNo();// パスポート
		String ageFrom = emp.getAgeFrom();// 開始年齢
		String ageTo = emp.getAgeTo();// 終了年齢
		String customer = emp.getCustomer();// お客様先
		String unitPriceFrom = emp.getUnitPriceFrom();// 単価範囲from
		String unitPriceTo = emp.getUnitPriceTo();// 単価範囲to
		String kadou = emp.getKadou();// 稼働
		String intoCompanyYearAndMonthFrom = emp.getIntoCompanyYearAndMonthFrom();// 入社年月元
		String intoCompanyYearAndMonthTo = emp.getIntoCompanyYearAndMonthTo();// 入社年月先
		String authorityCode = emp.getAuthorityCode();// 権限
		String employeeStatus = emp.getEmployeeStatus();// 社員ステータス
		String yearsOfExperience = emp.getYearsOfExperience();// 経験年数
		AccountInfoModel accountInfoModel = emp.getAccountInfo();// 口座情報
		BpInfoModel bpInfoModel = emp.getBpInfoModel();// bp情報
		String password = emp.getPassword();// パスワード
		String siteRoleCode = emp.getSiteRoleCode();// 役割コード
		String projectTypeCode = emp.getProjectTypeCode();// 分野コード

		// 住所情報開始
		String postcode = emp.getPostcode();// 郵便番号
		String firstHalfAddress = emp.getFirstHalfAddress();// 住所前半
		String lastHalfAddress = emp.getLastHalfAddress();// 住所後半
		String stationCode = emp.getStationCode();// 最寄駅1

		String employeeName = emp.getEmployeeName();// 社員名
		String picInfo = emp.getPicInfo();

		String residentCardInfoName = emp.getResidentCardInfoName();
		String passportInfoName = emp.getPassportInfoName();

		if (stationCode != null) {
			sendMap.put("stationCode", stationCode);
		}
		// 住所情報終了
		if (passportInfoName != null) {
			sendMap.put("passportInfoName", passportInfoName);
		}
		if (residentCardInfoName != null) {
			sendMap.put("residentCardInfoName", residentCardInfoName);
		}
		if (newEmployeeNo != null) {
			sendMap.put("newEmployeeNo", newEmployeeNo);
		}
		if (employeeNo != null) {
			sendMap.put("employeeNo", employeeNo);
		}
		if (employeeFristName != null) {
			sendMap.put("employeeFristName", employeeFristName);
		}
		if (employeeLastName != null) {
			sendMap.put("employeeLastName", employeeLastName);
		}
		if (furigana != null) {
			sendMap.put("furigana", furigana);
		}
		if (alphabetName != null) {
			sendMap.put("alphabetName", alphabetName);
		}
		if (alphabetName2 != null) {
			sendMap.put("alphabetName2", alphabetName2);
		}
		if (alphabetName3 != null) {
			sendMap.put("alphabetName3", alphabetName3);
		}
		if (birthday != null) {
			sendMap.put("birthday", birthday);
		}
		if (japaneseCalendar != null) {
			sendMap.put("japaneseCalendar", japaneseCalendar);
		}

		if (occupationCode != null) {
			sendMap.put("occupationCode", occupationCode);
		}
		if (departmentCode != null) {
			sendMap.put("departmentCode", departmentCode);
		}
		if (companyMail != null) {
			sendMap.put("companyMail", companyMail);
		}
		if (graduationUniversity != null) {
			sendMap.put("graduationUniversity", graduationUniversity);
		}
		if (major != null) {
			sendMap.put("major", major);
		}
		if (graduationYearAndMonth != null) {
			sendMap.put("graduationYearAndMonth", graduationYearAndMonth);
		}
		if (intoCompanyYearAndMonth != null) {
			sendMap.put("intoCompanyYearAndMonth", intoCompanyYearAndMonth);
		}
		if (retirementYearAndMonth != null) {
			sendMap.put("retirementYearAndMonth", retirementYearAndMonth);
		}
		if (retirementResonClassification != null) {
			sendMap.put("retirementResonClassification", retirementResonClassification);
		}
		if (comeToJapanYearAndMonth != null) {
			sendMap.put("comeToJapanYearAndMonth", comeToJapanYearAndMonth);
		}
		if (birthplace != null) {
			sendMap.put("birthplace", birthplace);
		}
		if (phoneNo != null) {
			sendMap.put("phoneNo", phoneNo);
		}

		if (residenceCode != null) {
			sendMap.put("residenceCode", residenceCode);
		}
		if (residenceCardNo != null) {
			sendMap.put("residenceCardNo", residenceCardNo);
		}
		if (stayPeriod != null) {
			sendMap.put("stayPeriod", stayPeriod);
		}
		if (passportStayPeriod != null) {
			sendMap.put("passportStayPeriod", passportStayPeriod);
		}
		if (immigrationStartTime != null) {
			sendMap.put("immigrationStartTime", immigrationStartTime);
		}
		if (immigrationEndTime != null) {
			sendMap.put("immigrationEndTime", immigrationEndTime);
		}
		if (contractDeadline != null) {
			sendMap.put("contractDeadline", contractDeadline);
		}
		if (employmentInsuranceStatus != null) {
			sendMap.put("employmentInsuranceStatus", employmentInsuranceStatus);
		}
		if (employmentInsuranceNo != null) {
			sendMap.put("employmentInsuranceNo", employmentInsuranceNo);
		}
		if (socialInsuranceStatus != null) {
			sendMap.put("socialInsuranceStatus", socialInsuranceStatus);
		}
		if (socialInsuranceNo != null) {
			sendMap.put("socialInsuranceNo", socialInsuranceNo);
		}
		if (socialInsuranceDate != null) {
			sendMap.put("socialInsuranceDate", socialInsuranceDate);
		}
		if (myNumber != null) {
			sendMap.put("myNumber", myNumber);
		}
		if (resumeName2 != null) {
			sendMap.put("resumeName2", resumeName2);
		}
		if (resumeName1 != null) {
			sendMap.put("resumeName1", resumeName1);
		}
		if (passportNo != null) {
			sendMap.put("passportNo", passportNo);
		}
		if (employeeFormCode != null) {
			sendMap.put("employeeFormCode", employeeFormCode);
		}
		if (customer != null) {
			sendMap.put("customer", customer);
		}
		if (intoCompanyCode != null) {
			sendMap.put("intoCompanyCode", intoCompanyCode);
		}
		if (nationalityCode != null) {
			sendMap.put("nationalityCode", nationalityCode);
		}

		if (genderStatus != null) {
			sendMap.put("genderStatus", genderStatus);
		}
		if (ageFrom != null) {
			sendMap.put("ageFrom", ageFrom);
		}
		if (ageTo != null) {
			sendMap.put("ageTo", ageTo);
		}

		if (unitPriceFrom != null) {
			sendMap.put("unitPriceFrom", unitPriceFrom);
		}
		if (unitPriceTo != null) {
			sendMap.put("unitPriceTo", unitPriceTo);
		}
		if (japaneseLevelCode != null) {
			sendMap.put("japaneseLevelCode", japaneseLevelCode);
		}
		if (englishLevelCode != null) {
			sendMap.put("englishLevelCode", englishLevelCode);
		}
		if (certification1 != null) {
			sendMap.put("certification1", certification1);
		}
		if (certification2 != null) {
			sendMap.put("certification2", certification2);
		}
		if (postcode != null) {
			sendMap.put("postcode", postcode);
		}
		if (firstHalfAddress != null) {
			sendMap.put("firstHalfAddress", firstHalfAddress);
		}
		if (lastHalfAddress != null) {
			sendMap.put("lastHalfAddress", lastHalfAddress);
		}
		if (developLanguage4 != null) {
			sendMap.put("developLanguage4", developLanguage4);
		}
		if (developLanguage5 != null) {
			sendMap.put("developLanguage5", developLanguage5);
		}
		if (frameWork1 != null) {
			sendMap.put("frameWork1", frameWork1);
		}
		if (frameWork2 != null) {
			sendMap.put("frameWork2", frameWork2);
		}
		if (introducer != null) {
			sendMap.put("introducer", introducer);
		}
		if (kadou != null) {
			sendMap.put("kadou", kadou);
		}

		if (developLanguage1 != null) {
			sendMap.put("developLanguage1", developLanguage1);
		}
		if (developLanguage2 != null) {
			sendMap.put("developLanguage2", developLanguage2);
		}
		if (developLanguage3 != null) {
			sendMap.put("developLanguage3", developLanguage3);
		}
		if (intoCompanyYearAndMonthFrom != null) {
			sendMap.put("intoCompanyYearAndMonthFrom", intoCompanyYearAndMonthFrom);
		}
		if (intoCompanyYearAndMonthTo != null) {
			sendMap.put("intoCompanyYearAndMonthTo", intoCompanyYearAndMonthTo);
		}
		if (authorityCode != null) {
			sendMap.put("authorityCode", authorityCode);
		}
		sendMap.put("updateUser", loginSession.getAttribute("employeeName"));
		if (employeeStatus != null) {
			sendMap.put("employeeStatus", employeeStatus);
		}
		if (yearsOfExperience != null) {
			sendMap.put("yearsOfExperience", yearsOfExperience);
		}
		sendMap.put("bankInfoModel", accountInfoModel);
		sendMap.put("bpInfoModel", bpInfoModel);
		if (password != null) {
			sendMap.put("password", password);
		}
		if (siteRoleCode != null) {
			sendMap.put("siteRoleCode", siteRoleCode);
		}
		if (projectTypeCode != null) {
			sendMap.put("projectTypeCode", projectTypeCode);
		}
		if (employeeName != null) {
			sendMap.put("employeeName", employeeName);
		}
		if (picInfo != null) {
			sendMap.put("picInfo", picInfo);
		}

		Date date = new Date();// 此时date为当前的时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");// 设置当前时间的格式，为年-月-日
		sendMap.put("nowDate", dateFormat.format(date));
		if (resume1Changed) {
			// 画面是直接显示并没有做jst和utc的9小时转化，因此这里也保持和以前一样直接存当地时间
			LocalDateTime now = LocalDateTime.now();
			// LocalDateTime updatedTime = now.plusHours(9);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedTime = now.format(formatter);
			sendMap.put("updateTime", formattedTime);
		}

		return sendMap;
	}

	public static int getAgeByBirth(Date birthday) {
		// Calendar：日历
		/* 从Calendar对象中或得一个Date对象 */
		Calendar cal = Calendar.getInstance();
		/* 把出生日期放入Calendar类型的bir对象中，进行Calendar和Date类型进行转换 */
		Calendar bir = Calendar.getInstance();
		bir.setTime(birthday);
		/* 取出当前年月日 */
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayNow = cal.get(Calendar.DAY_OF_MONTH);
		/* 取出出生年月日 */
		int yearBirth = bir.get(Calendar.YEAR);
		int monthBirth = bir.get(Calendar.MONTH);
		int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
		/* 如果生日大于当前日期，进行反转日期计算 */
		if (cal.before(bir)) {
			// 进行反转值计
			/* 取出当前年月日 */
			yearNow = bir.get(Calendar.YEAR);
			monthNow = bir.get(Calendar.MONTH);
			dayNow = bir.get(Calendar.DAY_OF_MONTH);
			/* 取出出生年月日 */
			yearBirth = cal.get(Calendar.YEAR);
			monthBirth = cal.get(Calendar.MONTH);
			dayBirth = cal.get(Calendar.DAY_OF_MONTH);
		}
		/* 大概年龄是当前年减去出生年 */
		int age = yearNow - yearBirth;
		/* 如果出当前月小与出生月，或者当前月等于出生月但是当前日小于出生日，那么年龄age就减一岁 */
		if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
			age--;
		}
		return age;
	}
}
