package jp.co.lyc.cms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.util.TextUtils;
import org.aspectj.weaver.ast.Var;
import org.castor.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.internal.FIFOCache;
import com.amazonaws.util.StringUtils;

import ch.qos.logback.core.joran.conditional.IfAction;
import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.mapper.EmployeeInfoMapper;
import jp.co.lyc.cms.mapper.SiteInfoMapper;
import jp.co.lyc.cms.model.SalesSituationModel;
import jp.co.lyc.cms.model.SalesSituationCsvModel;
import jp.co.lyc.cms.model.SiteModel;
import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.model.CompletePercetModal;
import jp.co.lyc.cms.model.MasterModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.model.SalesContent;
import jp.co.lyc.cms.service.CompletePercetService;
import jp.co.lyc.cms.service.EmployeeInfoService;
import jp.co.lyc.cms.service.SalesSituationService;
import jp.co.lyc.cms.service.UtilsService;
import jp.co.lyc.cms.util.StatusCodeToMsgMap;
import jp.co.lyc.cms.util.UtilsController;
import jp.co.lyc.cms.validation.SalesSituationValidation;
import software.amazon.ion.impl.PrivateScalarConversions.ValueVariant;

import hirondelle.date4j.DateTime;

@Controller
@RequestMapping(value = "/salesSituation")
public class SalesSituationController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesSituationService salesSituationService;

	@Autowired
	CompletePercetService completePercetService;

	@Autowired
	S3Controller s3Controller;

	@Autowired
	UtilsService utilsService;

	@Autowired
	UtilsController utilsController;

	@Autowired
	EmployeeInfoService employeeInfoService;

	@Autowired
	EmployeeInfoMapper employeeInfoMapper;

	@Autowired
	SiteInfoMapper siteInfoMapper;

	// 12月
	public static final String DECEMBER = "12";
	// 1月
	public static final String JANUARY = "01";

	/**
	 * month minus
	 * 
	 * @param 202304
	 * @return 202303
	 */
	public Date minusMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		Date returnDate = calendar.getTime();
		return returnDate;
	}

	/**
	 * Finish データを取得
	 *
	 */

	@RequestMapping(value = "/getSalesSituationFinish", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituationFinish(@RequestBody SalesSituationModel model)
			throws ParseException {
		List<SalesSituationModel> resultList = new ArrayList<SalesSituationModel>();
		// 社员
		{
			// 休假
			// T006EmployeeSiteInfo.workstate=3, 更新时间=T006EmployeeSiteInfo.updateTime
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String dateHoliday = "";
			try {
				Date date = sdf.parse(model.getSalesYearAndMonth());
				dateHoliday = new SimpleDateFormat("yyyyMM").format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<SalesSituationModel> temp = salesSituationService.getEmployeeHoliday(dateHoliday);
			if (null != temp && temp.size() > 0) {
				for (int i = 0; i < temp.size(); i++) {
					SalesSituationModel employeeHoliday = temp.get(i);
					List<SalesSituationModel> salesSituationRecent = salesSituationService
							.getEmployeeHolidayRecentSite(employeeHoliday.getEmployeeNo());
					if (null != salesSituationRecent && salesSituationRecent.size() > 0) {
						if (salesSituationRecent.size() >= 2) {
							// 显示休假前的一条现场记录
							resultList.add(salesSituationRecent.get(1));
						} else if (salesSituationRecent.size() == 1) {
							resultList.add(salesSituationRecent.get(0));
						}
					}
				}
			}
		}

		{
			// 离职
			// T002EmployeeDetail.employeeFormCode=4, 更新时间=retirementYearAndMonth
			// 先获取离职的employee list
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String dateRetire = "";
			try {
				Date date = sdf.parse(model.getSalesYearAndMonth());
				date = minusMonth(date);
				dateRetire = new SimpleDateFormat("yyyyMM").format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<SalesSituationModel> temp = salesSituationService.getEmployeeRetire(dateRetire);
			List<String> employeeNoList = new ArrayList<String>();

			if (null != temp && temp.size() > 0) {
				// 如果resultList 已经存在同个员工,休假的状态的话,离职状态优先,对休假进行覆盖
				if (null != resultList && resultList.size() > 0) {
					for (int i = 0; i < resultList.size(); i++) {
						SalesSituationModel employeeHoliday = resultList.get(i);
						for (int j = 0; j < temp.size(); j++) {
							SalesSituationModel employeeRetire = temp.get(j);
							if (null != employeeHoliday.getEmployeeNo() && null != employeeRetire.getEmployeeNo()
									&& employeeRetire.getEmployeeNo().equals(employeeHoliday.getEmployeeNo())) {
								resultList.remove(i);
							}
						}
					}
				}

				for (int i = 0; i < temp.size(); i++) {
					SalesSituationModel employeeRetire = temp.get(i);
					employeeNoList.add(employeeRetire.getEmployeeNo());
					resultList.add(employeeRetire);
				}
			}
			// 在获取unitPrice和addmissionStartDate
			if (null != employeeNoList && employeeNoList.size() > 0) {
				List<SalesSituationModel> tempWithPriceAndDate = salesSituationService
						.getEmployeeRetireSiteInfo(employeeNoList);
				if (null != tempWithPriceAndDate && tempWithPriceAndDate.size() > 0) {
					for (int i = 0; i < resultList.size(); i++) {
						SalesSituationModel employeeHolidayAndRetire = resultList.get(i);
						for (int j = 0; j < tempWithPriceAndDate.size(); j++) {
							SalesSituationModel employeeWithPriceAndDate = tempWithPriceAndDate.get(j);
							if (null != employeeWithPriceAndDate.getEmployeeNo()
									&& null != employeeHolidayAndRetire.getEmployeeNo() && employeeHolidayAndRetire
											.getEmployeeNo().equals(employeeWithPriceAndDate.getEmployeeNo())) {
								employeeHolidayAndRetire.setUnitPrice(employeeWithPriceAndDate.getUnitPrice());
								employeeHolidayAndRetire
										.setAdmissionStartDate(employeeWithPriceAndDate.getAdmissionStartDate());
								employeeHolidayAndRetire
										.setAdmissionPeriodDate(employeeWithPriceAndDate.getAdmissionPeriodDate());
								employeeHolidayAndRetire.setCustomer(employeeWithPriceAndDate.getCustomer());
								if (TextUtils.isEmpty(employeeWithPriceAndDate.getAdmissionPeriodDate())
										&& "0".equals(employeeWithPriceAndDate.getWorkState())) {
									employeeHolidayAndRetire.setAdmissionPeriodDate(
											getDifMonthByRetire(employeeWithPriceAndDate.getAdmissionStartDate(),
													employeeHolidayAndRetire.getRetirementYearAndMonth()));
								}
							}
						}
					}
				}
			}
		}
		// 协力
		{
			// 终了且所属确定 T011BpInfoSupplement.bpSalesProgressCode=4 &&
			// T006EmployeeSiteInfo.workstate=1, 更新时间=bpOtherCompanyAdmissionEndDate
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String dateBpConfirm = "";
			try {
				Date date = sdf.parse(model.getSalesYearAndMonth());
				date = minusMonth(date);
				dateBpConfirm = new SimpleDateFormat("yyyyMM").format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<SalesSituationModel> employeeList = salesSituationService.getBpEmployeeConfirmNoList();
			if (null != employeeList && employeeList.size() > 0) {
				List<String> employeeNoList = new ArrayList<String>();
				for (int i = 0; i < employeeList.size(); i++) {
					employeeNoList.add(employeeList.get(i).getEmployeeNo());
				}
				List<SalesSituationModel> temp = salesSituationService.getBpEmployeeConfirm(employeeNoList,
						dateBpConfirm);
				if (null != temp && temp.size() > 0) {
					for (int i = 0; i < temp.size(); i++) {
						SalesSituationModel newEmployee = temp.get(i);
						for (int j = 0; j < employeeList.size(); j++) {
							SalesSituationModel oldEmployee = employeeList.get(j);
							if (null != oldEmployee.getEmployeeNo() && null != newEmployee.getEmployeeNo()
									&& oldEmployee.getEmployeeNo().equals(newEmployee.getEmployeeNo())) {
								newEmployee.setCustomerAbbreviation(oldEmployee.getCustomerAbbreviation());
							}
						}
					}
					resultList.addAll(temp);
				}
			}
		}

		// 处理名字和番号
		if (null != resultList && resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				resultList.get(i).setRowNo(i + 1);
				resultList.get(i).setEmployeeName(
						resultList.get(i).getEmployeeFristName() + resultList.get(i).getEmployeeLastName());

				if (resultList.get(i).getEmployeeNo().substring(0, 3).equals("BPR")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(BPR)");
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName());
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(SP)");
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(SC)");
				}
			}
		}

		return resultList;
	}

	private String getDifMonthByRetire(String admissionStartDate, String retirementYearAndMonth) {
		if (!TextUtils.isEmpty(admissionStartDate) && !TextUtils.isEmpty(retirementYearAndMonth)) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				LocalDate startDate = LocalDate.parse(admissionStartDate, formatter);
				LocalDate retireDate = LocalDate.parse(retirementYearAndMonth, formatter);

				Period p = Period.between(startDate, retireDate);
				int months = p.getYears() * 12 + p.getMonths();
				if (months > 0) {
					return String.valueOf(months);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * データを取得 ffff
	 * 
	 * @param emp
	 * @return List
	 * @throws ParseException
	 */

	@RequestMapping(value = "/getSalesSituationNew", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituationNew(@RequestBody SalesSituationModel model)
			throws ParseException {
		List<String> employeeNoList = new ArrayList<String>();
		List<String> BpNoList = new ArrayList<String>();

		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> bpSalesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> developLanguageList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> T010SalesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> siteRoleCodeList = new ArrayList<SalesSituationModel>();
		List<BpInfoModel> T011BpInfoSupplementList = new ArrayList<BpInfoModel>();
		try {
			// 現在の日付を取得
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);

			// 社員営業され日付
			String salesDate = getSalesDate(model.getSalesYearAndMonth());
			String salesYearAndMonth = "";

			if (Integer.parseInt(model.getSalesYearAndMonth()) <= Integer.parseInt(curDate)) {
				employeeNoList = salesSituationService.getEmployeeNoListBefore(salesDate);
				T010SalesSituationList = salesSituationService.getT010SalesSituationBefore(model.getSalesYearAndMonth(),
						curDate, salesDate);

				// 检索过去月份的完成率时，直接从T026完成率表中拿。当前月以后的话，就根据该月检索出的数据进行计算，并将数字存到T026完成率表中。
				salesYearAndMonth = model.getSalesYearAndMonth();
			} else {
				employeeNoList = salesSituationService.getEmployeeNoList(model.getSalesYearAndMonth(), salesDate);
				BpNoList = salesSituationService.getBpNoList(model.getSalesYearAndMonth(), salesDate);
				T010SalesSituationList = salesSituationService.getT010SalesSituation(model.getSalesYearAndMonth(),
						curDate, salesDate);
			}
			if (employeeNoList.size() > 0)
				salesSituationList = salesSituationService.getSalesSituationList(employeeNoList, salesYearAndMonth);
			if (BpNoList.size() > 0)
				bpSalesSituationList = salesSituationService.getBpSalesSituationList(BpNoList);
			developLanguageList = salesSituationService.getDevelopLanguage();
			T011BpInfoSupplementList = salesSituationService.getT011BpInfoSupplement();
			siteRoleCodeList = salesSituationService.getSiteRoleCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesSituation" + "検索結束");

		for (int i = 0; i < bpSalesSituationList.size(); i++) {
			salesSituationList.add(bpSalesSituationList.get(i));
		}

		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSiteRoleCode() == null
					|| salesSituationList.get(i).getSiteRoleCode().equals("")) {
				for (int j = 0; j < siteRoleCodeList.size(); j++) {
					if (salesSituationList.get(i).getEmployeeNo().equals(siteRoleCodeList.get(j).getEmployeeNo())) {
						salesSituationList.get(i).setSiteRoleCode(siteRoleCodeList.get(j).getSiteRoleCode() == null ? ""
								: siteRoleCodeList.get(j).getSiteRoleCode());
					}
				}
			}
		}

		ArrayList<ModelClass> dropDownGetJapaneseLevelList = new ArrayList<ModelClass>();
		ArrayList<ModelClass> dropDownGetJapaneaseConversationLevelList = new ArrayList<ModelClass>();

		// 日本語等级，例： N1，N2，N3
		try {
			Method methodGetJapaneseLevel = utilsController.getClass().getMethod("getJapaneseLevel");
			dropDownGetJapaneseLevelList = (ArrayList<ModelClass>) methodGetJapaneseLevel.invoke(utilsController);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 日本語会话等级，例： N1流暢，N1弱い
		try {
			Method methodGetJapaneaseConversationLevel = utilsController.getClass()
					.getMethod("getJapaneaseConversationLevel");
			dropDownGetJapaneaseConversationLevelList = (ArrayList<ModelClass>) methodGetJapaneaseConversationLevel
					.invoke(utilsController);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < salesSituationList.size(); i++) {
			// 日本語
			String japaneaseLevelDes = "";
			if (!TextUtils.isEmpty(salesSituationList.get(i).getJapaneaseConversationLevel())) {
				// 从营业文章中查询
				try {
					if (null != salesSituationList.get(i).getJapaneaseConversationLevel()) {
						String japaneaseConversationLevelName = dropDownGetJapaneaseConversationLevelList
								.get(Integer.parseInt(salesSituationList.get(i).getJapaneaseConversationLevel()))
								.getName();
						japaneaseLevelDes = japaneaseConversationLevelName;
						if (japaneaseLevelDes.endsWith("業務確認")) {
							japaneaseLevelDes = japaneaseLevelDes.substring(0, japaneaseLevelDes.length() - 5);
						}
						if ("読み書き".equals(japaneaseLevelDes) || "書き読み".equals(japaneaseLevelDes)) {
							japaneaseLevelDes = "読書";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (TextUtils.isEmpty(japaneaseLevelDes)) {
				// 从个人情报中查询
				try {
					if (null != salesSituationList.get(i).getJapaneseLevelCode()) {
						String japaneseLevelCodeName = dropDownGetJapaneseLevelList
								.get(Integer.parseInt(salesSituationList.get(i).getJapaneseLevelCode())).getName();
						japaneaseLevelDes = japaneseLevelCodeName;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			salesSituationList.get(i).setJapaneaseLevelDesc(japaneaseLevelDes);

			// 社員名
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 3).equals("BPR")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BPR)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName());
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SC)");
			}

			salesSituationList.get(i).setAlphabetName(salesSituationList.get(i).getAlphabetName());

			// 履歴書名前
			if (salesSituationList.get(i).getResumeInfo1() != null
					&& !salesSituationList.get(i).getResumeInfo1().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName1();
				String resumetemp = salesSituationList.get(i).getResumeInfo1();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName1(resumeName);
				salesSituationList.get(i).setResume1Date(salesSituationList.get(i).getResume1Date());
			}

			if (salesSituationList.get(i).getResumeInfo2() != null
					&& !salesSituationList.get(i).getResumeInfo2().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName2();
				String resumetemp = salesSituationList.get(i).getResumeInfo2();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName2(resumeName);
			}

			// お客様
			salesSituationList.get(i).setCustomer("");

			// 開発言語
			for (int j = 0; j < developLanguageList.size(); j++) {
				if (salesSituationList.get(i).getDevelopLanguage1() != null && salesSituationList.get(i)
						.getDevelopLanguage1().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage1(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage2() != null && salesSituationList.get(i)
						.getDevelopLanguage2().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage2(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage3() != null && salesSituationList.get(i)
						.getDevelopLanguage3().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage3(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage4() != null && salesSituationList.get(i)
						.getDevelopLanguage4().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage4(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
			}
			String developLanguage = (salesSituationList.get(i).getDevelopLanguage1().equals(",") ? ""
					: salesSituationList.get(i).getDevelopLanguage1())
					+ (salesSituationList.get(i).getDevelopLanguage2().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage2())
					+ (salesSituationList.get(i).getDevelopLanguage3().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage3())
					+ (salesSituationList.get(i).getDevelopLanguage4().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage4());

			if (developLanguage.length() > 0)
				developLanguage = developLanguage.substring(0, developLanguage.length() - 1);
			salesSituationList.get(i).setDevelopLanguage(developLanguage);

			// T010
			for (int j = 0; j < T010SalesSituationList.size(); j++) {
				if (salesSituationList.get(i).getEmployeeNo().equals(T010SalesSituationList.get(j).getEmployeeNo())) {
					if (salesSituationList.get(i).getAdmissionEndDate() == null
							&& salesSituationList.get(i).getScheduledEndDate() != null) {
						salesSituationList.get(i).setAdmissionEndDate(salesSituationList.get(i).getScheduledEndDate());
					}

					// 現在の日付を取得
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String curDate = sdf.format(date);
					if (Integer.parseInt(model.getSalesYearAndMonth()) <= Integer.parseInt(curDate)) {
						/*
						 * if (salesSituationList.get(i).getEmployeeNo().startsWith("BP")) {
						 * salesSituationList.get(i).setSalesProgressCode(T010SalesSituationList.get(j).
						 * getBpSalesProgressCode()); } else {
						 * salesSituationList.get(i).setSalesProgressCode(T010SalesSituationList.get(j).
						 * getSalesProgressCode()); }
						 */
						salesSituationList.get(i)
								.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
						salesSituationList.get(i)
								.setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
						salesSituationList.get(i).setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
						salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
						salesSituationList.get(i)
								.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
						salesSituationList.get(i).setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
						salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
						salesSituationList.get(i)
								.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
						salesSituationList.get(i).setHopeRemark(T010SalesSituationList.get(j).getHopeRemark());
						salesSituationList.get(i)
								.setCustomerContractStatus(T010SalesSituationList.get(j).getCustomerContractStatus());
						salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
						salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
						salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
						salesSituationList.get(i).setSalesStaffName(T010SalesSituationList.get(j).getSalesStaffName());
						salesSituationList.get(i)
								.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
						salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
						salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
					} else {
						if (!(salesSituationList.get(i).getAdmissionEndDate() != null && Integer
								.parseInt(salesSituationList.get(i).getAdmissionEndDate().substring(0, 6)) >= Integer
										.parseInt(T010SalesSituationList.get(j).getSalesYearAndMonth()))
								|| (salesSituationList.get(i).getAdmissionEndDate() != null
										&& Integer.parseInt(salesSituationList.get(i).getAdmissionEndDate().substring(0,
												6)) == Integer.parseInt(model.getSalesYearAndMonth()))) {
							/*
							 * if (salesSituationList.get(i).getEmployeeNo().startsWith("BP")) {
							 * salesSituationList.get(i).setSalesProgressCode(T010SalesSituationList.get(j).
							 * getBpSalesProgressCode()); } else {
							 * salesSituationList.get(i).setSalesProgressCode(T010SalesSituationList.get(j).
							 * getSalesProgressCode()); }
							 */
							salesSituationList.get(i)
									.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
							salesSituationList.get(i)
									.setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
							salesSituationList.get(i)
									.setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
							salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
							salesSituationList.get(i)
									.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
							salesSituationList.get(i)
									.setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
							salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
							salesSituationList.get(i)
									.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
							salesSituationList.get(i).setHopeRemark(T010SalesSituationList.get(j).getHopeRemark());
							salesSituationList.get(i).setCustomerContractStatus(
									T010SalesSituationList.get(j).getCustomerContractStatus());
							salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
							salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
							salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
							salesSituationList.get(i)
									.setSalesStaffName(T010SalesSituationList.get(j).getSalesStaffName());
							salesSituationList.get(i)
									.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
							salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
							salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
						}
					}
				}
			}
		}

		// データソート
		List<SalesSituationModel> salesSituationListTemp = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> salesProgressCodeListTemp = new ArrayList<SalesSituationModel>();

		// 優先順ソート
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesProgressCode() != null
					&& !(salesSituationList.get(i).getSalesProgressCode().equals("1")
							|| salesSituationList.get(i).getSalesProgressCode().equals("0"))
					&& (salesSituationList.get(i).getSalesPriorityStatus() != null
							&& salesSituationList.get(i).getSalesPriorityStatus().equals("1"))) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}

		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesProgressCode() != null
					&& !(salesSituationList.get(i).getSalesProgressCode().equals("1")
							|| salesSituationList.get(i).getSalesProgressCode().equals("0"))
					&& (salesSituationList.get(i).getSalesPriorityStatus() != null
							&& salesSituationList.get(i).getSalesPriorityStatus().equals("2"))) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}

		// 社員区分ソート
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (!salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
			}
		}

		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				for (int j = 0; j < T011BpInfoSupplementList.size(); j++) {

					if (salesSituationListTemp.get(i).getEmployeeNo()
							.equals(T011BpInfoSupplementList.get(j).getBpEmployeeNo())) {
						if (T011BpInfoSupplementList.get(j).getBpSalesProgressCode().equals("4")) {
							// 以下两行代码被注释掉，因为这个月之前的BP状况变成所属确定以后在营业一览里面（这个月消息，以前的确定的不能消失）出不来了这个问题的修正，代码重新打开
							// 以下的三个条件分别是
							// 1.营业状况的年月是空（针对于系统做成之前的数据试用，或者营业状况表(T11)不存在的数据）
							// 2.T10表里面最新的一条的数据的营业状况年月不能于目前营业一览湖面的选中的营业状况年月，主要是为了不把以前营业一栏里面的数据拿掉（比如当前月202401，上次营业一览确定是202304）
							// 3.T10表里面最新的一条的数据的营业状况年月和画面选择的营业状况年月相当，并且T10表的営業状況コード是4的情况，主要是bp现场终了，但是没有直接在bp小画面点击所属确定，
							// 在营业一览更新了这套数据（T0会生成一条数据）
							// 这样的情况下的话，如果我们在营业一栏画面更新了BP的 所属确定的时候，这条数据也会自动的消息(营业状况的主画面如果更新了BP的状态，BP表的状态也会变化)
							if (salesSituationListTemp.get(i).getSalesDateUpdate() == null ||
									!salesSituationListTemp.get(i).getSalesDateUpdate()
											.equals(model.getSalesYearAndMonth())
									||
									(salesSituationListTemp.get(i).getSalesDateUpdate()
											.equals(model.getSalesYearAndMonth()) &&
											salesSituationListTemp.get(i).getSalesProgressCode().equals("4"))) {
								salesSituationListTemp.remove(i);
								i--;
								break;
							}
						} else {
							salesSituationListTemp.remove(i);
							i--;
							break;
						}
					}
				}
			}
		}

		// 進捗ソート
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getSalesProgressCode() != null) {
				if (salesSituationListTemp.get(i).getSalesProgressCode().equals("1")
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("0")) {
					salesProgressCodeListTemp.add(salesSituationListTemp.get(i));
					salesSituationListTemp.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < salesProgressCodeListTemp.size(); i++) {
			salesSituationListTemp.add(salesProgressCodeListTemp.get(i));
		}

		// 行番号付け
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			salesSituationListTemp.get(i).setRowNo(i + 1);

			// 服务器与本地差9小时
			Date resume1Date = salesSituationListTemp.get(i).getResume1Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(resume1Date);
			cal.add(Calendar.HOUR, 9);
			String resumeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(cal.getTime());
			salesSituationListTemp.get(i).setResumeDate(resumeDate);

			// 面談情報
			SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
			sdf.applyPattern("yyyyMMdd");// a为am/pm的标记
			Date date = new Date();// 获取当前时间
			String time = sdf.format(date);
			if (salesSituationListTemp.get(i).getInterviewDate1() != null
					&& salesSituationListTemp.get(i).getInterviewDate1().length() > 8) {
				if (Integer.parseInt(salesSituationListTemp.get(i).getInterviewDate1().substring(0, 8)) < Integer
						.parseInt(time)) {
					salesSituationListTemp.get(i).setInterviewDate1("");
					salesSituationListTemp.get(i).setInterviewCustomer1("");
					salesSituationListTemp.get(i).setStationCode1("");
				}
			}
			if (salesSituationListTemp.get(i).getInterviewDate2() != null
					&& salesSituationListTemp.get(i).getInterviewDate2().length() > 8) {
				if (Integer.parseInt(salesSituationListTemp.get(i).getInterviewDate2().substring(0, 8)) < Integer
						.parseInt(time)) {
					salesSituationListTemp.get(i).setInterviewDate2("");
					salesSituationListTemp.get(i).setInterviewCustomer2("");
					salesSituationListTemp.get(i).setStationCode2("");
				}
			}

			// 現場予定終了 進捗->結果待ち
			if (salesSituationListTemp.get(i).getAdmissionEndDate() == null
					|| salesSituationListTemp.get(i).getAdmissionEndDate().equals("")) {
				if (salesSituationListTemp.get(i).getSalesProgressCode() == null
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("")) {
					if (!salesSituationListTemp.get(i).getEmployeeStatus().equals("1")) {
						salesSituationListTemp.get(i).setSalesProgressCode("2");
					}

				}
			}
		}
		// salesSituationListTemp=salesSituationListTemp.stream()
		// .sorted(Comparator.comparing(SalesSituationModel::getSalesPriorityStatus)).collect(Collectors.toList());

		return salesSituationListTemp;
	}

	private static final int MAX_RETRIES = 3; // 最大重试次数

	// @Scheduled(cron = "0 0 21 28-31 * ? ") // 任务在每个月的28号到31号的21点到23点之间每小时执行一次。
	@Scheduled(cron = "0 0 21-23 28-31 * ?") // 任务在每个月的28号到31号的21点到23点之间每小时执行一次。
	public void getSalesSituationNewAndUpdateCompleteRate() throws ParseException {

		int retryCount = 0;
		boolean success = false;
		// 每个月的最后一天才执行
		if (isLastDayOfMonth()) {
			while (retryCount < MAX_RETRIES && !success) {
				try {
					SalesSituationModel model = new SalesSituationModel();
					model.setSalesYearAndMonth(getNextYearMonth());
					List<SalesSituationModel> salesSituationListTemp = this.getSalesSituationNew(model);

					int completeCount = 0;
					for (int i = 0; i < salesSituationListTemp.size(); i++) {
						if (salesSituationListTemp.get(i).getSalesProgressCode() != null) {
							if (salesSituationListTemp.get(i).getSalesProgressCode().equals("1")
									|| salesSituationListTemp.get(i).getSalesProgressCode().equals("0")) {
								completeCount++;
							}
						}

					}

					BigDecimal completeCountBD = new BigDecimal(completeCount);
					BigDecimal totalCountBD = new BigDecimal(salesSituationListTemp.size());

					// 完成百分比 = (completeCount / totalCount) * 100
					BigDecimal percentage = completeCountBD.divide(totalCountBD, 3, RoundingMode.HALF_UP)
							.multiply(new BigDecimal("100"));

					// HttpSession session = getSession();// ->nullのため
					CompletePercetModal completePercetModal = new CompletePercetModal();
					completePercetModal.setYearAndMonthOfPercent(model.getSalesYearAndMonth());
					completePercetModal.setUpdateUser("autoUpdateCompleteP");
					// completePercetModal.setUpdateUser(session.getAttribute("employeeName").toString());
					completePercetModal.setCompletePercet(percentage);

					// T026に保存。
					completePercetService.upsertCompletePercet(completePercetModal);
					success = true;
					logger.info("Scheduled task executed successfully.");
				} catch (Exception e) {
					retryCount++;
					logger.error("Scheduled task execution failed. Retry count: " + retryCount, e);
					if (retryCount >= MAX_RETRIES) {
						notifyAdmin(e); // 发送通知给管理员
					}
				}
			}
		}

	}

	private boolean isLastDayOfMonth() {
		// 获取当前日期
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return dayOfMonth == maxDay;
	}

	private void notifyAdmin(Exception e) {
		// 实现通知逻辑，例如发送邮件或短信通知管理员
		logger.error("Scheduled task failed after maximum retries. Sending notification to admin.", e);
	}

	public static String getNextYearMonth() {
		// 创建一个SimpleDateFormat实例，指定格式为"yyyyMM"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		// 获取当前日期
		Calendar cal = Calendar.getInstance();
		// 将月份加1
		cal.add(Calendar.MONTH, 1);
		// 将当前日期格式化为指定格式
		return sdf.format(cal.getTime());
	}

	/**
	 * データを取得 ffff
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/getSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituation(@RequestBody SalesSituationModel model) {

		logger.info("getSalesSituation:" + "検索開始");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> developLanguageList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> T010SalesSituationList = new ArrayList<SalesSituationModel>();
		List<BpInfoModel> T011BpInfoSupplementList = new ArrayList<BpInfoModel>();

		try {
			// 現在の日付を取得
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);
			// 社員営業され日付
			String salesDate = getSalesDate(model.getSalesYearAndMonth());
			// String salesDate =
			// String.valueOf(Integer.valueOf(model.getSalesYearAndMonth()) + 1);
			salesSituationList = salesSituationService.getSalesSituationModel(model.getSalesYearAndMonth(), curDate,
					salesDate);
			developLanguageList = salesSituationService.getDevelopLanguage();
			T010SalesSituationList = salesSituationService.getT010SalesSituation(model.getSalesYearAndMonth(), curDate,
					salesDate);
			T011BpInfoSupplementList = salesSituationService.getT011BpInfoSupplement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesSituation" + "検索結束");

		for (int i = 0; i < salesSituationList.size(); i++) {
			// 社員名
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SC)");
			}

			// 履歴書名前
			if (salesSituationList.get(i).getResumeInfo1() != null
					&& !salesSituationList.get(i).getResumeInfo1().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName1();
				String resumetemp = salesSituationList.get(i).getResumeInfo1();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName1(resumeName);
			}

			if (salesSituationList.get(i).getResumeInfo2() != null
					&& !salesSituationList.get(i).getResumeInfo2().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName2();
				String resumetemp = salesSituationList.get(i).getResumeInfo2();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName2(resumeName);
			}

			// お客様
			salesSituationList.get(i).setCustomer("");

			// 開発言語
			String developLanguage = "";
			for (int j = 0; j < developLanguageList.size(); j++) {
				if (salesSituationList.get(i).getDevelopLanguage1() != null && salesSituationList.get(i)
						.getDevelopLanguage1().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage2() != null && salesSituationList.get(i)
						.getDevelopLanguage2().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage3() != null && salesSituationList.get(i)
						.getDevelopLanguage3().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage4() != null && salesSituationList.get(i)
						.getDevelopLanguage4().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage5() != null && salesSituationList.get(i)
						.getDevelopLanguage5().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
			}

			if (developLanguage.length() > 0)
				developLanguage = developLanguage.substring(0, developLanguage.length() - 1);
			salesSituationList.get(i).setDevelopLanguage(developLanguage);

			// T010
			for (int j = 0; j < T010SalesSituationList.size(); j++) {
				if (salesSituationList.get(i).getEmployeeNo().equals(T010SalesSituationList.get(j).getEmployeeNo())) {
					salesSituationList.get(i)
							.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
					salesSituationList.get(i).setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
					salesSituationList.get(i).setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
					salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
					salesSituationList.get(i)
							.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
					salesSituationList.get(i).setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
					salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
					salesSituationList.get(i)
							.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
					salesSituationList.get(i).setHopeRemark(T010SalesSituationList.get(j).getHopeRemark());
					salesSituationList.get(i)
							.setCustomerContractStatus(T010SalesSituationList.get(j).getCustomerContractStatus());
					salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
					salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
					salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
					salesSituationList.get(i).setSalesStaffName(T010SalesSituationList.get(j).getSalesStaffName());
					salesSituationList.get(i)
							.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
					salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
					salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
				}
			}
		}

		// データソート
		List<SalesSituationModel> salesSituationListTemp = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> salesProgressCodeListTemp = new ArrayList<SalesSituationModel>();

		// 優先順ソート
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesPriorityStatus() != null
					&& salesSituationList.get(i).getSalesPriorityStatus().equals("1")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesPriorityStatus() != null
					&& salesSituationList.get(i).getSalesPriorityStatus().equals("2")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}

		// 社員区分ソート
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (!salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
			}
		}

		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				for (int j = 0; j < T011BpInfoSupplementList.size(); j++) {
					if (salesSituationListTemp.get(i).getEmployeeNo()
							.equals(T011BpInfoSupplementList.get(j).getBpEmployeeNo())
							&& Integer.parseInt(model.getSalesYearAndMonth()) > Integer
									.parseInt(T011BpInfoSupplementList.get(j).getBpOtherCompanyAdmissionEndDate())) {
						salesSituationListTemp.remove(i);
						i--;
						break;
					}
				}
			}
		}

		// 進捗ソート
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getSalesProgressCode() != null) {
				if ((salesSituationListTemp.get(i).getSalesProgressCode().equals("1")
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("0"))
						&& !(salesSituationListTemp.get(i).getSalesPriorityStatus() != null
								&& (salesSituationListTemp.get(i).getSalesPriorityStatus().equals("1")
										|| salesSituationListTemp.get(i).getSalesPriorityStatus().equals("2")))) {
					salesProgressCodeListTemp.add(salesSituationListTemp.get(i));
					salesSituationListTemp.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < salesProgressCodeListTemp.size(); i++) {
			salesSituationListTemp.add(salesProgressCodeListTemp.get(i));
		}

		// 行番号付け
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			salesSituationListTemp.get(i).setRowNo(i + 1);
		}
		return salesSituationListTemp;
	}

	// 社員営業され日付
	private String getSalesDate(String getSalesYearAndMonth) {
		String salesDate = getSalesYearAndMonth;

		// 仕様変更
		/*
		 * if (DECEMBER.equals(getSalesYearAndMonth.substring(4))) { salesDate =
		 * String.valueOf(Integer.valueOf(getSalesYearAndMonth.substring(0, 4)) + 1) +
		 * JANUARY; } else { salesDate =
		 * String.valueOf(Integer.valueOf(getSalesYearAndMonth) + 1); }
		 */
		return salesDate;
	}

	@RequestMapping(value = "/updateSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateSalesSituation(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "検索開始");
		String[] errorsMessage = new String[] { "" };
		DataBinder binder = new DataBinder(model);
		binder.setValidator(new SalesSituationValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		Map<String, Object> result = new HashMap<>();
		if (results.hasErrors()) {
			results.getAllErrors().forEach((o) -> {
				FieldError error = (FieldError) o;
				errorsMessage[0] += error.getDefaultMessage();// エラーメッセージ
			});
			result.put("errorsMessage", errorsMessage[0]);// エラーメッセージ
			return result;
		}
		int index = 0;
		try {
			String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
			model.setSalesDateUpdate(salesDate);
			index = salesSituationService.insertSalesSituation(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("updateREcords", index);
		logger.info("updateSalesSituation" + "検索結束");
		return result;
	}

	/**
	 * データを取得
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeSiteInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeSiteInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "検索開始");
		int index = 0;
		int index1 = 0;
		try {
			index = salesSituationService.updateEmployeeSiteInfo(model);
			index1 = salesSituationService.updateSalesSituation(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index + index1;
	}

	/**
	 * 画面初期化のデータを取得
	 * 
	 * @param emp
	 * @return List
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPersonalSalesInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getPersonalSalesInfo(@RequestBody SalesSituationModel model) throws Exception {

		logger.info("getPersonalSalesInfo:" + "検索開始");
		int count = salesSituationService.getCount(model.getEmployeeNo());
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		if (count == 0) {
			try {
				salesSituationList = salesSituationService.getPersonalSalesInfo(model.getEmployeeNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				salesSituationList = salesSituationService.getPersonalSalesInfoFromT019(model.getEmployeeNo());
				/* 2020/12/09 STRAT 張棟 */
				// 時間のフォーマットを変更する。例えば「202009->2020/09」
				if (salesSituationList != null && salesSituationList.size() != 0
						&& !StringUtils.isNullOrEmpty(salesSituationList.get(0).getTheMonthOfStartWork())) {
					salesSituationList.get(0)
							.setTheMonthOfStartWork(DateFormat(salesSituationList.get(0).getTheMonthOfStartWork()));
				} else {
					// 取るデータがnullの時

				}
				/* 2020/12/09 END 張棟 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (salesSituationList.size() > 0) {
			ArrayList<String> resumeInfoTemp = new ArrayList<String>();

			if (!(salesSituationList.get(0).getResumeName1() == null
					|| salesSituationList.get(0).getResumeName1().equals(""))) {
				resumeInfoTemp.add(salesSituationList.get(0).getResumeName1());
			}
			if (!(salesSituationList.get(0).getResumeName2() == null
					|| salesSituationList.get(0).getResumeName2().equals(""))) {
				resumeInfoTemp.add(salesSituationList.get(0).getResumeName2());
			}
			salesSituationList.get(0).setResumeInfoList(resumeInfoTemp);

			if (salesSituationList.get(0).getYearsOfExperience() != null
					&& salesSituationList.get(0).getYearsOfExperience().length() >= 4) {
				Calendar date = Calendar.getInstance();
				String year = String.valueOf(date.get(Calendar.YEAR));
				int tempYear = Integer.parseInt(year)
						- Integer.parseInt(salesSituationList.get(0).getYearsOfExperience().substring(0, 4)) + 1;
				if (tempYear < 0)
					tempYear = 0;
				salesSituationList.get(0).setYearsOfExperience(String.valueOf(tempYear));
			}

			if (salesSituationList.get(0).getAge() != null && salesSituationList.get(0).getAge().equals("")) {
				if (salesSituationList.get(0).getBirthday() != null
						&& !salesSituationList.get(0).getBirthday().equals(""))
					salesSituationList.get(0).setAge(String.valueOf(getAge(salesSituationList.get(0).getBirthday())));
			}
		}

		salesSituationList.get(0)
				.setSalesProgressCode(salesSituationList.get(salesSituationList.size() - 1).getSalesProgressCode());

		if (salesSituationList.get(salesSituationList.size() - 1).getRemark() == null
				|| salesSituationList.get(salesSituationList.size() - 1).getRemark().equals(" ")) {
			salesSituationList.get(0)
					.setRemark((salesSituationList.get(salesSituationList.size() - 1).getRemark1() == null ? ""
							: salesSituationList.get(salesSituationList.size() - 1).getRemark1())
							+ (salesSituationList.get(salesSituationList.size() - 1).getRemark2() == null ? ""
									: salesSituationList.get(salesSituationList.size() - 1).getRemark2()));
		} else {
			salesSituationList.get(0).setRemark(salesSituationList.get(salesSituationList.size() - 1).getRemark());
		}

		logger.info("updateSalesSituation" + "検索結束");
		return salesSituationList;
	}

	/**
	 * 営業csv
	 * 
	 * @param emp
	 * @return List
	 * @throws Exception
	 */
	@RequestMapping(value = "/csvDownload", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationCsvModel> csvDownload(@RequestBody List<String> employeeNo) throws Exception {

		logger.info("SalesSituationController.csvDownload:" + "csvDownloadによると、社員情報csvを出力開始");
		List<SalesSituationCsvModel> salesSituationInfoCsvList = new ArrayList<SalesSituationCsvModel>();

		salesSituationInfoCsvList = salesSituationService.getSalesSituationCSV(employeeNo);

		for (int i = 0; i < salesSituationInfoCsvList.size(); i++) {
			if (salesSituationInfoCsvList.get(i).getYearsOfExperience() != null
					&& salesSituationInfoCsvList.get(i).getYearsOfExperience().length() >= 4) {
				Calendar date = Calendar.getInstance();
				String year = String.valueOf(date.get(Calendar.YEAR));
				int tempYear = Integer.parseInt(year)
						- Integer.parseInt(salesSituationInfoCsvList.get(i).getYearsOfExperience().substring(0, 4)) + 1;
				if (tempYear < 0)
					tempYear = 0;
				salesSituationInfoCsvList.get(i).setYearsOfExperience(String.valueOf(tempYear));
			}
		}

		logger.info(
				"SalesSituationController.csvDownload:" + "csvDownloadによると、営業情報csvを出力結束");
		return salesSituationInfoCsvList;
	}

	@RequestMapping(value = "/getInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getInterviewLists(@RequestBody List<String> employeeNoList) {
		List<SalesSituationModel> interviewLists = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> interviewListsTemp = salesSituationService.getInterviewLists(employeeNoList);

		// ソート
		for (int i = 0; i < employeeNoList.size(); i++) {
			for (int j = 0; j < interviewListsTemp.size(); j++) {
				if (employeeNoList.get(i).equals(interviewListsTemp.get(j).getEmployeeNo())) {
					if (interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("SC")
							|| interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("SP")
							|| interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("BP")) {
						if (interviewListsTemp.get(j).getEmployeeNo().substring(0, 3).equals("BPR"))
							interviewListsTemp.get(j).setEmployeeName(interviewListsTemp.get(j).getEmployeeName() + "("
									+ interviewListsTemp.get(j).getEmployeeNo().substring(0, 3) + ")");
						else
							interviewListsTemp.get(j).setEmployeeName(interviewListsTemp.get(j).getEmployeeName() + "("
									+ interviewListsTemp.get(j).getEmployeeNo().substring(0, 2) + ")");
					}

					interviewLists.add(interviewListsTemp.get(j));
					break;
				}
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals("")) {
				interviewLists.get(i)
						.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
				interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
				interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
				interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
				interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
				interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());
				interviewLists.get(i).setSuccessRate1(interviewLists.get(i).getSuccessRate2());
				interviewLists.get(i).setSalesStaff1(interviewLists.get(i).getSalesStaff2());

				interviewLists.get(i).setInterviewClassificationCode2(null);
				interviewLists.get(i).setInterviewDate2(null);
				interviewLists.get(i).setStationCode2(null);
				interviewLists.get(i).setInterviewCustomer2(null);
				interviewLists.get(i).setInterviewInfo2(null);
				interviewLists.get(i).setInterviewUrl2(null);
				interviewLists.get(i).setSuccessRate2(null);
				interviewLists.get(i).setSalesStaff2(null);
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (!(interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals(""))
					&& !(interviewLists.get(i).getInterviewDate2() == null
							|| interviewLists.get(i).getInterviewDate2().equals(""))) {
				if (Long.parseLong(interviewLists.get(i).getInterviewDate1()) > Long
						.parseLong(interviewLists.get(i).getInterviewDate2())) {
					SalesSituationModel temp = new SalesSituationModel();
					temp.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode1());
					temp.setInterviewDate1(interviewLists.get(i).getInterviewDate1());
					temp.setStationCode1(interviewLists.get(i).getStationCode1());
					temp.setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer1());
					temp.setInterviewInfo1(interviewLists.get(i).getInterviewInfo1());
					temp.setInterviewUrl1(interviewLists.get(i).getInterviewUrl1());
					temp.setSuccessRate1(interviewLists.get(i).getSuccessRate1());
					temp.setSalesStaff1(interviewLists.get(i).getSalesStaff1());

					interviewLists.get(i)
							.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
					interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
					interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
					interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
					interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
					interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());
					interviewLists.get(i).setSuccessRate1(interviewLists.get(i).getSuccessRate2());
					interviewLists.get(i).setSalesStaff1(interviewLists.get(i).getSalesStaff2());

					interviewLists.get(i).setInterviewClassificationCode2(temp.getInterviewClassificationCode1());
					interviewLists.get(i).setInterviewDate2(temp.getInterviewDate1());
					interviewLists.get(i).setStationCode2(temp.getStationCode1());
					interviewLists.get(i).setInterviewCustomer2(temp.getInterviewCustomer1());
					interviewLists.get(i).setInterviewInfo2(temp.getInterviewInfo1());
					interviewLists.get(i).setInterviewUrl2(temp.getInterviewUrl1());
					interviewLists.get(i).setSuccessRate2(temp.getSuccessRate1());
					interviewLists.get(i).setSalesStaff2(temp.getSalesStaff1());
				}
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (!(interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals(""))) {
				String interviewDate = interviewLists.get(i).getInterviewDate1();

				int year = Integer.parseInt(interviewDate.substring(0, 4));
				int month = Integer.parseInt(interviewDate.substring(4, 6));
				int day = Integer.parseInt(interviewDate.substring(6, 8));
				int hour = Integer.parseInt(interviewDate.substring(8, 10));
				int minute = Integer.parseInt(interviewDate.substring(10, 12));

				DateTime now = DateTime.now(TimeZone.getDefault());
				DateTime interviewDateTime = new DateTime(year, month, day, hour, minute, 0, 0);

				if (now.minusDays(3).gteq(interviewDateTime)) {
					interviewLists.get(i)
							.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
					interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
					interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
					interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
					interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
					interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());
					interviewLists.get(i).setSuccessRate1(interviewLists.get(i).getSuccessRate2());
					interviewLists.get(i).setSalesStaff1(interviewLists.get(i).getSalesStaff2());

					interviewLists.get(i).setInterviewClassificationCode2(null);
					interviewLists.get(i).setInterviewDate2(null);
					interviewLists.get(i).setStationCode2(null);
					interviewLists.get(i).setInterviewCustomer2(null);
					interviewLists.get(i).setInterviewInfo2(null);
					interviewLists.get(i).setInterviewUrl2(null);
					interviewLists.get(i).setSuccessRate2(null);
					interviewLists.get(i).setSalesStaff2(null);

					i--;
				}
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (interviewLists.get(i).getInterviewResultAwaiting1() == null
					|| interviewLists.get(i).getInterviewResultAwaiting1().equals("")) {
				SalesSituationModel temp = new SalesSituationModel();
				temp.setInterviewResultAwaiting1(interviewLists.get(i).getInterviewResultAwaiting1());
				interviewLists.get(i).setInterviewResultAwaiting1(interviewLists.get(i).getInterviewResultAwaiting2());
				interviewLists.get(i).setInterviewResultAwaiting2(temp.getInterviewResultAwaiting1());
			}
		}

		return interviewLists;
	}

	@RequestMapping(value = "/deleteInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public void deleteInterviewLists(@RequestBody SalesSituationModel model) {
		logger.info("deleteInterviewLists:" + "削除開始");
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
		model.setSalesYearAndMonth(salesDate);
		try {
			salesSituationService.updateInterviewLists(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("deleteInterviewLists" + "削除結束");
	}

	@RequestMapping(value = "/updateInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateInterviewLists(@RequestBody SalesSituationModel model) {
		Map<String, Object> result = new HashMap<>();
		String errorsMessage = "";
		if (!model.getInterviewClassificationCode1().equals("")) {
			if (model.getInterviewDate1() == null || model.getInterviewDate1().equals("")) {
				errorsMessage += "日付 ";
			}
			if (model.getInterviewCustomer1() == null || model.getInterviewCustomer1().equals("")) {
				errorsMessage += "お客様 ";
			}
		}
		if (!model.getInterviewClassificationCode2().equals("")) {
			if (model.getInterviewDate2() == null || model.getInterviewDate2().equals("")) {
				errorsMessage += "日付 ";
			}
			if (model.getInterviewCustomer2() == null || model.getInterviewCustomer2().equals("")) {
				errorsMessage += "お客様 ";
			}
		}

		if (!errorsMessage.equals("")) {
			errorsMessage += "を入力してください。";
			result.put("errorsMessage", errorsMessage);
			return result;
		}
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
		model.setSalesYearAndMonth(salesDate);
		logger.info("updateInterviewLists:" + "更新開始");
		try {
			salesSituationService.updateInterviewLists(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateInterviewLists" + "更新結束");
		return result;
	}

	/**
	 * データを取得
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeAddressInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeAddressInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("getPersonalSalesInfo:" + "検索開始");
		int index = 0;
		try {
			index = salesSituationService.updateEmployeeAddressInfo(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index;
	}

	/**
	 * 子画面の更新ボタンを押下する
	 **/
	@RequestMapping(value = "/updateSalesSentence", method = RequestMethod.POST)
	@ResponseBody
	public int updateSalesSentence(@RequestBody SalesContent model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		if (model.getJapaneaseConversationLevel() != null && model.getJapaneaseConversationLevel().equals("")) {
			model.setJapaneaseConversationLevel(null);
		}
		if (model.getEnglishConversationLevel() != null && model.getEnglishConversationLevel().equals("")) {
			model.setEnglishConversationLevel(null);
		}
		logger.info("getPersonalSalesInfo:" + "検索開始");
		int index = 0;
		model.setBeginMonth(model.getTempDate());
		try {
			index = salesSituationService.updateSalesSentence(model);
			// 個人情報情報の最寄駅情報を同期更新 lxf-20230412
			Map<String, Object> sendMap = new HashMap<>();
			sendMap.put("employeeNo", model.getEmployeeNo());
			sendMap.put("stationCode", model.getStationCode());
			sendMap.put("updateUser", (String) getSession().getAttribute("employeeName"));
			employeeInfoMapper.updateAddressInfo(sendMap);

			// 获取开发语言 进行更新
			int developLanguageNum = 1;
			int deleteflag = 0;
			String testString = "";
			int frameWorkNum = 1;
			if (model.getDevelopLanguageCode6() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode6()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode6());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode6()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode6()) * -1 - 1));
					frameWorkNum++;
				}
			}

			if (model.getDevelopLanguageCode7() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode7()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode7());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode7()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode7()) * -1 - 1));
					frameWorkNum++;
				}
			}

			if (model.getDevelopLanguageCode8() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode8()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode8());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode8()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode8()) * -1 - 1));
					frameWorkNum++;
				}
			}

			if (model.getDevelopLanguageCode9() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode9()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode9());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode9()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode9()) * -1 - 1));
					frameWorkNum++;
				}
			}

			if (model.getDevelopLanguageCode10() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode10()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode10());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode10()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode10()) * -1 - 1));
					frameWorkNum++;
				}
			}

			if (model.getDevelopLanguageCode11() != null) {
				if (Integer.parseInt(model.getDevelopLanguageCode11()) > 0 && developLanguageNum <= 4) {
					sendMap.put("developLanguage" + String.valueOf(developLanguageNum),
							model.getDevelopLanguageCode11());
					developLanguageNum++;
				} else if (Integer.parseInt(model.getDevelopLanguageCode11()) < 0 && frameWorkNum <= 2) {
					sendMap.put("frameWork" + String.valueOf(frameWorkNum),
							String.valueOf(Integer.parseInt(model.getDevelopLanguageCode11()) * -1 - 1));
					frameWorkNum++;
				}
			}
			employeeInfoMapper.updateDevelopLanguageCode(sendMap);
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index;
	}

	/**
	 * 画面の可変項目変更する
	 * 
	 * @param model
	 * @return Map
	 * @throws ParseException
	 */
	@RequestMapping(value = "/changeDataStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeDataStatus(@RequestBody SalesSituationModel model) throws ParseException {

		Map<String, Object> result = new HashMap<>();
		HttpSession session = getSession();
		model.setUpdateUser(session.getAttribute("employeeName").toString());

		logger.info("changeDataStatus:" + "チェック開始");
		String errorsMessage = "";
		if (model.getSalesProgressCode() != null
				&& (model.getSalesProgressCode().equals("1") || model.getSalesProgressCode().equals("0"))) {
			if (model.getCustomer() == null || model.getCustomer().equals("")) {
				errorsMessage += "確定客様 ";
			}
			if (model.getPrice() == null || model.getPrice().equals("")) {
				errorsMessage += "確定単価  ";
			}

			if (!errorsMessage.equals("")) {
				errorsMessage += "を入力してください。";
				result.put("errorsMessage", errorsMessage);
				return result;
			} else {
				if (model.getSalesProgressCode().equals("1")) {
					String nextAdmission = salesSituationService.getEmpNextAdmission(model.getEmployeeNo());
					if (nextAdmission != null && nextAdmission.equals("0")) {
						salesSituationService.updateEmpNextAdmission(model);
						// errorsMessage += "稼働中の現場存在しています、現場データをチェックしてください。";
						// result.put("errorsMessage", errorsMessage);
						// return result;
					} else {
						salesSituationService.insertEmpNextAdmission(model);
					}
				} else if (model.getSalesProgressCode().equals("0")) {
					salesSituationService.updateEmpNextAdmission(model);
				}
			}
		}

		if (model.getSalesProgressCode() != null && (model.getSalesProgressCode().equals("2"))) {
			if (model.getCustomer() == null || model.getCustomer().equals("")) {
				errorsMessage += "結果待ちお客様 ";
			}
			if (!errorsMessage.equals("")) {
				errorsMessage += "を入力してください。";
				result.put("errorsMessage", errorsMessage);
				return result;
			}
		}

		// 進歩为空时 判断是否删除稼动中现场
		if (model.getSalesProgressCode() != null && (model.getSalesProgressCode().equals(""))) {
			List<SiteModel> siteList = new ArrayList<SiteModel>();
			siteList = salesSituationService.getEmpLastAdmission(model.getEmployeeNo());
			// 最后一个现场开始时间为下个月 且稼动中时 删除现场
			if (siteList.get(siteList.size() - 1).getAdmissionStartDate().substring(0, 6).equals(
					model.getAdmissionEndDate()) && siteList.get(siteList.size() - 1).getWorkState().equals("0")) {
				Map<String, Object> sendMap = new HashMap<String, Object>();
				sendMap.put("employeeNo", model.getEmployeeNo());
				sendMap.put("admissionStartDate", siteList.get(siteList.size() - 1).getAdmissionStartDate());
				siteInfoMapper.deleteSiteInfo(sendMap);
			}
		}

		logger.info("changeDataStatus:" + "更新開始");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		int updateCount = 0;
		try {
			// 現在の日付を取得
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);

			// 社員営業され日付
			String salesDate = getSalesDate(model.getAdmissionEndDate());
			/*
			 * if (DECEMBER.equals(model.getAdmissionEndDate().substring(4, 6))) { salesDate
			 * = String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 4))
			 * + 1) + JANUARY; } else { salesDate =
			 * String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 6)) +
			 * 1); }
			 */

			model.setSalesYearAndMonth(salesDate);

			if (salesSituationService.checkEmpNoAndYM(model).size() == 0) {
				salesSituationService.insertDataStatus(model);
			}

			// テーブルT010SalesSituation項目を変更する
			updateCount = salesSituationService.updateDataStatus(model);

			// テーブルT006EmployeeSiteInfo項目を変更する
			updateCount = salesSituationService.updateEMPInfo(model);

			// テーブルT011BpInfoSupplement項目を変更する
			if (model.getEmployeeNo().substring(0, 2).equals("BP")) {
				updateCount = salesSituationService.updateBPEMPInfo(model);
			}

			if (model.getEmployeeNo().substring(0, 3).equals("BPR")
					&& (model.getSalesProgressCode().equals("1") || model.getSalesProgressCode().equals("4"))) {
				Map<String, String> sendMap = new HashMap<String, String>();
				String newBpNo = utilsService.getNoBP(sendMap);
				if (newBpNo != null) {
					newBpNo = "BP" + String.format("%0" + 3 + "d", Integer.parseInt(newBpNo) + 1);
				} else {
					newBpNo = "BP001";
				}
				model.setEmployeeName(newBpNo);
				salesSituationService.updateBPR(model);
			}

			// 日付に基づいて一覧を取得
			/*
			 * salesSituationList = salesSituationService
			 * .getSalesSituationModel(model.getAdmissionEndDate().substring(0, 6), curDate,
			 * salesDate);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("changeDataStatus" + "更新結束");
		result.put("result", salesSituationList);
		return result;
	}

	/**
	 * 営業フォルダ
	 * 
	 * @return Map
	 * @throws ParseException
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkDirectory", method = RequestMethod.POST)
	@ResponseBody
	public boolean checkDirectory(@RequestBody SalesSituationModel model) throws ParseException, Exception {

		String mkDirectoryPath = "c:\\file\\営業フォルダ\\" + model.getSalesYearAndMonth();
		File folder = new File(mkDirectoryPath);
		if (!folder.exists() && !folder.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 営業フォルダ
	 * 
	 * @return Map
	 * @throws ParseException
	 * @throws Exception
	 */
	@RequestMapping(value = "/makeDirectory", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> makeDirectory(@RequestBody SalesSituationModel model) throws ParseException, Exception {

		Map<String, Object> result = new HashMap<>();
		ArrayList<String> employeeNoList = model.getEmployeeNoList();
		ArrayList<String> resumeInfo1List = model.getResumeInfo1List();
		ArrayList<String> resumeInfo2List = model.getResumeInfo2List();

		for (int i = 0; i < employeeNoList.size(); i++) {
			String mkDirectoryPath = "c:\\file\\営業フォルダ\\" + model.getSalesYearAndMonth() + "\\" + employeeNoList.get(i);
			if (mkDirectory(mkDirectoryPath)) {
				// System.out.println(mkDirectoryPath + "建立完毕");
				if (resumeInfo1List.get(i) != null)
					fileChannelCopy(resumeInfo1List.get(i), mkDirectoryPath);
				if (resumeInfo2List.get(i) != null)
					fileChannelCopy(resumeInfo2List.get(i), mkDirectoryPath);
			} else {
				// System.out.println(mkDirectoryPath + "建立失败！此目录或许已经存在！");
				deleteFile(new File(mkDirectoryPath));
				if (resumeInfo1List.get(i) != null)
					fileChannelCopy(resumeInfo1List.get(i), mkDirectoryPath);
				if (resumeInfo2List.get(i) != null)
					fileChannelCopy(resumeInfo2List.get(i), mkDirectoryPath);
			}
		}
		String dir = "c:\\file\\営業フォルダ\\" + model.getSalesYearAndMonth();
		creatTxtFile(dir, "営業文章", model.getText());
		mkDirectory("c:\\file\\salesFolder\\");
		String rar = "c:\\file\\salesFolder\\" + model.getSalesYearAndMonth() + ".rar";
		zip(dir, rar, true);
		// cmd指令打开对应文件夹
		// Runtime.getRuntime().exec("cmd /c start explorer c:\\file\\営業フォルダ\\" +
		// model.getSalesYearAndMonth());

		S3Model s3Model = new S3Model();
		s3Model.setFilePath("c:/file/salesFolder/" + model.getSalesYearAndMonth() + ".rar");
		s3Model.setFileKey("営業フォルダ/" + model.getSalesYearAndMonth() + ".rar");
		s3Controller.uploadFile(s3Model);

		return result;
	}

	@RequestMapping(value = "/getEmployeeSiteWorkTermList", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getEmployeeSiteWorkTermList(@RequestBody SalesSituationModel model)
			throws ParseException {
		List<SalesSituationModel> resultList = new ArrayList<SalesSituationModel>();

		String salesYearAndMonth = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date date = sdf.parse(model.getSalesYearAndMonth());

			// 拿到页面选择时间的一个月前的时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -1);
			Date convertDate = calendar.getTime();

			salesYearAndMonth = new SimpleDateFormat("yyyyMM").format(convertDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultList = salesSituationService.getEmployeeSiteWorkTermList(salesYearAndMonth);
		if (resultList == null || resultList.size() == 0) {
			return resultList;
		}

		// 处理名字和番号
		if (null != resultList && resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				resultList.get(i).setRowNo(i + 1);

				if (resultList.get(i).getEmployeeFristName() == null) {
					resultList.get(i).setEmployeeFristName("");
				}
				if (resultList.get(i).getEmployeeLastName() == null) {
					resultList.get(i).setEmployeeLastName("");
				}
				resultList.get(i).setEmployeeName(
						resultList.get(i).getEmployeeFristName() + resultList.get(i).getEmployeeLastName());

				if (resultList.get(i).getEmployeeNo().substring(0, 3).equals("BPR")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(BPR)");
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName());
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(SP)");
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
					resultList.get(i).setEmployeeName(resultList.get(i).getEmployeeName() + "(SC)");
				}
			}
		}

		return resultList;
	}

	private static void deleteFile(File file) {
		if (file.isFile()) {// 判断是否为文件，是，则删除
			file.delete();
		} else {// 不为文件，则为文件夹
			String[] childFilePath = file.list();// 获取文件夹下所有文件相对路径
			for (String path : childFilePath) {
				File childFile = new File(file.getAbsoluteFile() + "/" + path);
				deleteFile(childFile);// 递归，对每个都进行判断
			}
		}
	}

	/**
	 * 打包
	 *
	 * @param dir            要打包的目录
	 * @param zipFile        打包后的文件路径
	 * @param includeBaseDir 是否包括最外层目录
	 * @throws Exception
	 */
	public static void zip(String dir, String zipFile, boolean includeBaseDir) throws Exception {
		if (zipFile.startsWith(dir)) {
			throw new RuntimeException("打包生成的文件不能在打包目录中");
		}
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
			File fileDir = new File(dir);
			String baseDir = "";
			if (includeBaseDir) {
				baseDir = fileDir.getName();
			}
			compress(out, fileDir, baseDir);
		}
	}

	public static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {

		if (sourceFile.isDirectory()) {
			base = base.length() == 0 ? "" : base + File.separator;
			File[] files = sourceFile.listFiles();
			if (ArrayUtils.isEmpty(files)) {
				// todo 打包空目录
				// out.putNextEntry(new ZipEntry(base));
				return;
			}
			for (File file : files) {
				compress(out, file, base + file.getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			try (FileInputStream in = new FileInputStream(sourceFile)) {
				IOUtils.copy(in, out);
			} catch (Exception e) {
				throw new RuntimeException("打包异常: " + e.getMessage());
			}
		}
	}

	/*
	 * 
	 * */
	public static boolean mkDirectory(String path) {
		File file = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				return file.mkdirs();
			} else {
				return false;
			}
		} catch (Exception e) {
		} finally {
			file = null;
		}
		return false;
	}

	public void fileChannelCopy(String sFile, String tFile) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		File s = new File(sFile);
		String fileName = sFile.substring(sFile.lastIndexOf("/") + 1);
		File t = new File(tFile + "//" + fileName);
		if (s.exists() && s.isFile()) {
			try {
				fi = new FileInputStream(s);
				fo = new FileOutputStream(t);
				in = fi.getChannel();// 得到对应的文件通道
				out = fo.getChannel();// 得到对应的文件通道
				in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fi.close();
					in.close();
					fo.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文字列の実装
	 */
	private String DateFormat(String date) {
		String dateStr = date.substring(0, 4) + "/" + date.substring(4, 6);
		return dateStr;
	}

	/**
	 * 创建写入txt文件
	 * 
	 * @throws IOException
	 */
	public static void creatTxtFile(String path, String name, String text) throws IOException {
		File file = new File(path + "\\" + name + ".txt");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();
	}

	public static int getMonthNum(String date1, String date2) throws java.text.ParseException {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		int month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12;
		return result == 0 ? month : (month + result);
	}

	public static Integer getAge(String birthday) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date birth = df.parse(birthday);
		Calendar now = Calendar.getInstance();
		Calendar born = Calendar.getInstance();

		now.setTime(new Date());
		born.setTime(birth);

		if (born.after(now)) {
			return 0;
		}

		int age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
		if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
			age -= 1;
		}
		return age;
	}
}
