package jp.co.lyc.cms.controller;

import java.io.Console;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.MoneySetModel;
import jp.co.lyc.cms.model.SalesEmployeeModel;
import jp.co.lyc.cms.model.SalesInfoModel;
import jp.co.lyc.cms.model.SalesPointModel;
import jp.co.lyc.cms.model.SalesProfitModel;
import jp.co.lyc.cms.service.SalesMoneySetService;
import jp.co.lyc.cms.service.SalesProfitService;
import jp.co.lyc.cms.service.UtilsService;

import org.apache.http.util.TextUtils;
import org.apache.ibatis.io.ResolverUtil.IsA;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Controller
@CrossOrigin(origins = "http://127.0.0.1:3000")
public class SalesProfitController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesProfitService salesProfitService;
	@Autowired
	SalesMoneySetService salesMoneySetService;
	@Autowired
	UtilsService utilsService;

	String errorsMessage = "";

	
	@RequestMapping(value = "/getPointInfoNew", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesInfoModel> getPointInfoNew(@RequestBody SalesProfitModel salesProfitModel) throws ParseException {

		if (salesProfitModel.getEmployeeName() == null || salesProfitModel.getStartDate() == null
				|| salesProfitModel.getEndDate() == null) {
			return new ArrayList<SalesInfoModel>();
		}

		List<SalesInfoModel> siteList = salesProfitService.getSalesInfo(salesProfitModel);

		if (siteList.size() == 0) {
			return new ArrayList<SalesInfoModel>();
		}

		List<String> employeeNoList = new ArrayList<String>();
		for (int i = 0; i < siteList.size(); i++) {
			if (!siteList.get(i).getEmployeeNo().substring(0, 2).equals("BP"))
				employeeNoList.add(siteList.get(i).getEmployeeNo());
		}

		List<SalesInfoModel> salesInfo = employeeNoList.size() > 0
				? salesProfitService.getSalesInfoByemployeeNoList(employeeNoList)
				: new ArrayList<SalesInfoModel>();
		List<SalesEmployeeModel> customerName = salesProfitService.getCustomerName();

		for (int i = 0; i < siteList.size(); i++) {
			// 稼働月数計算
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
			String startDate = siteList.get(i).getAdmissionStartDate();
			String endDate = siteList.get(i).getAdmissionEndDate() == null
					? dateFormat.format(salesProfitModel.getEndDate()).toString()
					: siteList.get(i).getAdmissionEndDate();

			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
			int months = Months
					.monthsBetween(formatter.parseDateTime(startDate.substring(0, 4) + "-" + startDate.substring(4, 6)),
							formatter.parseDateTime(endDate.substring(0, 4) + "-" + endDate.substring(4, 6)))
					.getMonths() + 1;
			siteList.get(i).setMonth(months);

			// 氏名
			if (siteList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				String bpCustomerName = "";
				for (int j = 0; j < customerName.size(); j++) {
					if (siteList.get(i).getBpBelongCustomerCode() != null
							&& siteList.get(i).getBpBelongCustomerCode().equals(customerName.get(j).getCustomerNo())) {
						bpCustomerName = "(" + customerName.get(j).getCustomerName() + ")";
						break;
					}
				}
				if (bpCustomerName.equals(""))
					bpCustomerName += "(BP)";
				siteList.get(i).setEmployeeName(siteList.get(i).getEmployeeFristName()
						+ siteList.get(i).getEmployeeLastName() + bpCustomerName);

				siteList.get(i).setProfit(Integer.toString(Integer.parseInt(siteList.get(i).getUnitPrice()) * months));
				int bpSalary = 0;
				if (siteList.get(i).getBpUnitPrice() != null && !siteList.get(i).getBpUnitPrice().equals(""))
					bpSalary = Integer.parseInt(siteList.get(i).getBpUnitPrice()) * 10000 * months;
				int bpGrossProfit = Integer.parseInt(siteList.get(i).getProfit()) - bpSalary;
				if (bpGrossProfit < 60000) {
					// BP粗利 5万以下
					siteList.get(i).setBpGrossProfit("0");
				} else if (bpGrossProfit < 80000) {
					// BP粗利 6万-7万
					siteList.get(i).setBpGrossProfit("1");
				} else if (bpGrossProfit < 100000) {
					// BP粗利 8万-9万
					siteList.get(i).setBpGrossProfit("2");
				} else {
					// BP粗利 10万以上
					siteList.get(i).setBpGrossProfit("3");
				}
			} else {
				String employeeName = siteList.get(i).getEmployeeFristName() + siteList.get(i).getEmployeeLastName();
				if (siteList.get(i).getEmployeeNo().substring(0, 2).equals("SP"))
					employeeName += "(SP)";
				else if (siteList.get(i).getEmployeeNo().substring(0, 2).equals("SC"))
					employeeName += "(SC)";
				// 新人判断
				for (int j = 0; j < salesInfo.size(); j++) {
					if (siteList.get(i).getEmployeeNo().equals(salesInfo.get(j).getEmployeeNo()) && siteList.get(i)
							.getAdmissionStartDate().equals(salesInfo.get(j).getAdmissionStartDate())) {
						if (salesInfo.get(j).getIntoCompanyCode() != null
								&& salesInfo.get(j).getIntoCompanyCode().equals("0")) {
							employeeName += "(新人)";
							siteList.get(i).setIntoCompanyCode("0");
						}
						siteList.get(i).setFirstAdmission(true);
						break;
					}
				}
				siteList.get(i).setEmployeeName(employeeName);
				siteList.get(i).setEmployeeStatus("0");

				// 新人じゃない場合、経験者をつける
				if (siteList.get(i).getIntoCompanyCode() == null) {
					siteList.get(i).setIntoCompanyCode("1");
				}
			}

			// 特別ポイント計算
			if (months == 12 && siteList.get(i).getWorkState() != null && siteList.get(i).getWorkState().equals("2")) {
				siteList.get(i).setSpecialsalesPointCondition("2");
			} else if (months == 6) {
				siteList.get(i).setSpecialsalesPointCondition("0");
			} else if (siteList.get(i).isFirstAdmission() && months == 3 && (siteList.get(i).getIntroducer() != null
					&& siteList.get(i).getIntroducer().equals(salesProfitModel.getEmployeeName()))) {
				siteList.get(i).setSpecialsalesPointCondition("1");
			}

			// 年月
			siteList.get(i).setYearAndMonth(siteList.get(i).getAdmissionStartDate().substring(0, 4) + "/"
					+ siteList.get(i).getAdmissionStartDate().substring(4, 6));

			// 番号
			siteList.get(i).setRowNo(String.valueOf(i + 1));
		}

		List<SalesPointModel> pointInfoList = salesProfitService.getSalesPointInfo();
		for (int i = 0; i < siteList.size(); i++) {
			for (int j = 0; j < pointInfoList.size(); j++) {
				// 社員ポイント計算
				if (siteList.get(i).getEmployeeStatus().equals("0")) {
					if (siteList.get(i).getEmployeeStatus().equals(pointInfoList.get(j).getEmployeeStatus())
							&& siteList.get(i).getIntoCompanyCode().equals(pointInfoList.get(j).getIntoCompanyCode())) {
						siteList.get(i).setPoint(pointInfoList.get(j).getSalesPoint());
						if (siteList.get(i).getSpecialsalesPointCondition() != null
								&& siteList.get(i).getSpecialsalesPointCondition()
										.equals(pointInfoList.get(j).getSpecialPointConditionCode())) {
							siteList.get(i).setSpecialsalesPoint(pointInfoList.get(j).getSpecialsalesPoint());
						}
					}
				}
				// BPポイント計算
				else {
					if (siteList.get(i).getEmployeeStatus().equals(pointInfoList.get(j).getEmployeeStatus())
							&& siteList.get(i).getBpGrossProfit().equals(pointInfoList.get(j).getBpGrossProfit())) {
						siteList.get(i).setPoint(pointInfoList.get(j).getSalesPoint());
						if (siteList.get(i).getSpecialsalesPointCondition() != null
								&& siteList.get(i).getSpecialsalesPointCondition()
										.equals(pointInfoList.get(j).getSpecialPointConditionCode())) {
							siteList.get(i).setSpecialsalesPoint(pointInfoList.get(j).getSpecialsalesPoint());
						}
					}
				}
			}
		}

		logger.info("SalesProfitController.getSalesPointInfo:" + "検索結束");
		return siteList;
	}

	@RequestMapping(value = "/getPointInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesInfoModel> getPointInfo(@RequestBody SalesProfitModel salesProfitModel) throws ParseException {

		if (salesProfitModel.getEmployeeName() == null) {
			return new ArrayList<SalesInfoModel>();
		}

		List<SalesInfoModel> siteList = salesProfitService.getPointInfo(salesProfitModel);
		List<SalesEmployeeModel> customerName = salesProfitService.getCustomerName();
		List<SalesEmployeeModel> employeeSiteInfo = salesProfitService.getEmployeeSiteInfo();
		List<SalesPointModel> pointInfoList = salesProfitService.getSalesPointInfo();

		List<SalesInfoModel> employeeSales = salesProfitService.getEmployeeNoSalary();
		List<SalesInfoModel> employeeNameToNo = salesProfitService.getEmployeeName();

		int pointAll = 0;

		// 取值之后的操作
		for (int i = 0; i < siteList.size(); i++) {

			// 设置行番号
			siteList.get(i).setRowNo(Integer.toString(i + 1));

			// 设置社员名
			String employeeName = (siteList.get(i).getEmployeeFristName() == null ? ""
					: siteList.get(i).getEmployeeFristName())
					+ (siteList.get(i).getEmployeeLastName() == null ? "" : siteList.get(i).getEmployeeLastName());
			siteList.get(i).setEmployeeName(employeeName);

			// 设置社员区分
			String employeeStatus = "";
			if (!(siteList.get(i).getEmployeeStatus() == null)) {
				if (siteList.get(i).getEmployeeStatus().equals("0")) {
					employeeStatus = "社員";
				} else if (siteList.get(i).getEmployeeStatus().equals("1")) {
					employeeStatus = "協力";
				} else
					employeeStatus = "";
			}
			siteList.get(i).setEmployeeStatusName(employeeStatus);

			// 设置所属会社
			if (!(siteList.get(i).getBpBelongCustomerCode() == null
					|| siteList.get(i).getBpBelongCustomerCode().equals(""))) {
				for (int z = 0; z < customerName.size(); z++) {
					if (siteList.get(i).getBpBelongCustomerCode().equals(customerName.get(z).getCustomerNo())) {
						siteList.get(i).setEmployeeFrom(customerName.get(z).getCustomerName());
					}
				}
			}
			if (siteList.get(i).getEmployeeFrom() == null)
				siteList.get(i).setEmployeeFrom("BP");

			// 设置お客様
			for (int j = 0; j < employeeSiteInfo.size(); j++) {
				if (siteList.get(i).getEmployeeNo().equals(employeeSiteInfo.get(j).getEmployeeNo())) {
					if (Integer.parseInt(siteList.get(i).getYearAndMonth()) >= Integer
							.parseInt(employeeSiteInfo.get(j).getStartTime())) {
						if (employeeSiteInfo.get(j).getEndTime() == null) {
							siteList.get(i).setCustomerName(employeeSiteInfo.get(j).getCustomerName());
							siteList.get(i).setLevelCode(employeeSiteInfo.get(j).getLevelCode());
							siteList.get(i).setStartTime(employeeSiteInfo.get(j).getStartTime());
							siteList.get(i).setEndTime(employeeSiteInfo.get(j).getEndTime());
							break;
						} else if (Integer.parseInt(siteList.get(i).getYearAndMonth()) <= Integer
								.parseInt(employeeSiteInfo.get(j).getEndTime())) {
							siteList.get(i).setCustomerName(employeeSiteInfo.get(j).getCustomerName());
							siteList.get(i).setLevelCode(employeeSiteInfo.get(j).getLevelCode());
							siteList.get(i).setStartTime(employeeSiteInfo.get(j).getStartTime());
							siteList.get(i).setEndTime(employeeSiteInfo.get(j).getEndTime());
							break;
						}
					}
				}
			}

			if (siteList.get(i).getIntoCompanyName().equals("新人")) {
				siteList.get(i).setIntoCompanyCode("0");
			} else {
				siteList.get(i).setIntoCompanyCode("1");
			}

			if (siteList.get(i).getSalesProgressCode().equals("5")) {
				siteList.get(i).setSalesProgressCode("1");
			} else if (siteList.get(i).getSalesProgressCode().equals("4")) {
				siteList.get(i).setSalesProgressCode("2");
			}

			// 计算ポイント
			for (int j = 0; j < pointInfoList.size(); j++) {
				if (siteList.get(i).getEmployeeStatus() != null && pointInfoList.get(j).getEmployeeStatus() != null
						&& siteList.get(i).getIntoCompanyCode() != null
						&& pointInfoList.get(j).getIntoCompanyCode() != null
						&& siteList.get(i).getCustomerContractStatus() != null
						&& pointInfoList.get(j).getCustomerContractStatus() != null
						&& siteList.get(i).getLevelCode() != null && pointInfoList.get(j).getLevelCode() != null
						&& siteList.get(i).getSalesProgressCode() != null
						&& pointInfoList.get(j).getBpGrossProfit() != null) {
					if (siteList.get(i).getEmployeeStatus().equals(pointInfoList.get(j).getEmployeeStatus())
							&& siteList.get(i).getIntoCompanyCode().equals(pointInfoList.get(j).getIntoCompanyCode())
							&& siteList.get(i).getCustomerContractStatus()
									.equals(pointInfoList.get(j).getCustomerContractStatus())
							&& siteList.get(i).getLevelCode().equals(pointInfoList.get(j).getLevelCode())
							&& siteList.get(i).getSalesProgressCode().equals(pointInfoList.get(j).getBpGrossProfit())) {
						siteList.get(i).setPoint(pointInfoList.get(j).getSalesPoint());
					}
				}
			}

			// 特別计算ポイント
			if (siteList.get(i).getStartTime() != null) {
				if (siteList.get(i).getEndTime() == null) {
					Date day = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
					siteList.get(i).setEndTime(dateFormat.format(day));
				}
				int months = Integer.parseInt(siteList.get(i).getEndTime())
						- Integer.parseInt(siteList.get(i).getStartTime());
				if (months >= 12) {
					siteList.get(i).setSpecialsalesPointCondition("2");
				} else if (months >= 6) {
					siteList.get(i).setSpecialsalesPointCondition("0");
				} else if (months >= 3) {
					siteList.get(i).setSpecialsalesPointCondition("1");
				} else {
					siteList.get(i).setSpecialsalesPointCondition("");
				}

				for (int j = 0; j < pointInfoList.size(); j++) {
					if (siteList.get(i).getSpecialsalesPointCondition()
							.equals(pointInfoList.get(j).getSpecialPointConditionCode())) {
						siteList.get(i).setSpecialsalesPoint(pointInfoList.get(j).getSpecialsalesPoint());
					}
				}
				switch (siteList.get(i).getSpecialsalesPointCondition()) {
				case "":
					siteList.get(i).setSpecialsalesPointCondition("");
					break;
				case "0":
					siteList.get(i).setSpecialsalesPointCondition("同現場で稼働六か月以上");
					break;
				case "1":
					siteList.get(i).setSpecialsalesPointCondition("紹介入社稼動三ヶ月");
					break;
				case "2":
					siteList.get(i).setSpecialsalesPointCondition("同現場で1年以上単価調整");
					break;
				}
			}

			// 设置売上粗利
			if (salesProfitModel.getPdf().equals("true")) {
				if (siteList.get(i).getStartTime() != null && siteList.get(i).getEndTime() != null) {
					SalesProfitModel salesProfitDate = new SalesProfitModel();
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

					String startTime = siteList.get(i).getStartTime().substring(0, 4) + "/"
							+ siteList.get(i).getStartTime().substring(4, 6) + "/01 00:00:00";
					String endTime = siteList.get(i).getEndTime().substring(0, 4) + "/"
							+ siteList.get(i).getEndTime().substring(4, 6) + "/01 00:00:00";
					Date startDate = sdFormat.parse(startTime);
					Date endDate = sdFormat.parse(endTime);

					salesProfitDate.setEmployeeName(siteList.get(i).getEmployeeName());
					salesProfitDate.setStartDate(startDate);
					salesProfitDate.setEndDate(endDate);

					List<SalesInfoModel> salesInfoByPdf = this.getSalesInfoByPdf(salesProfitDate, employeeSales,
							customerName, employeeNameToNo);
					if (salesInfoByPdf.size() > 0) {
						siteList.get(i).setProfitAll(salesInfoByPdf.get(0).getProfitAll());
						siteList.get(i).setSiteRoleNameAll(salesInfoByPdf.get(0).getSiteRoleNameAll());
					}

					/*
					 * if (this.getSalesInfo(salesProfitDate).size() > 0) {
					 * siteList.get(i).setProfitAll(this.getSalesInfo(salesProfitDate).get(0).
					 * getProfitAll());
					 * siteList.get(i).setSiteRoleNameAll(this.getSalesInfo(salesProfitDate).get(0).
					 * getSiteRoleNameAll()); }
					 */
				}
			}

			// 设置契約区分
			/*
			 * if (siteList.get(i).getCustomerContractStatus().equals("0")) {
			 * siteList.get(i).setCustomerContractStatus("即存"); } else if
			 * (siteList.get(i).getCustomerContractStatus().equals("1")) {
			 * siteList.get(i).setCustomerContractStatus("新规"); } else {
			 * siteList.get(i).setCustomerContractStatus(""); }
			 */

			// 合计ポイント
			if (siteList.get(i).getPoint() != null) {
				pointAll += Integer.parseInt(siteList.get(i).getPoint());
			}
			if (siteList.get(i).getSpecialsalesPoint() != null) {
				pointAll += Integer.parseInt(siteList.get(i).getSpecialsalesPoint());
			}
		}
		if (siteList.size() > 0)
			siteList.get(0).setPointAll(Integer.toString(pointAll));
		logger.info("SalesProfitController.getSalesPointInfo:" + "検索結束");
		return siteList;
	}

	@RequestMapping(value = "/getSalesInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesInfoModel> getSalesInfo(@RequestBody SalesProfitModel salesProfitModel) {

		if (salesProfitModel.getEmployeeName() != null && salesProfitModel.getEmployeeName().equals(""))
			salesProfitModel.setEmployeeName(null);
		if (salesProfitModel.getEmployeeStatus() != null && salesProfitModel.getEmployeeStatus().equals(""))
			salesProfitModel.setEmployeeStatus(null);

		List<SalesInfoModel> employeeSales = salesProfitService.getEmployeeNoSalary();
		List<SalesEmployeeModel> customerName = salesProfitService.getCustomerName();
		List<SalesInfoModel> employeeNameToNo = salesProfitService.getEmployeeName();
		List<MoneySetModel> salesMoneySetList = salesMoneySetService.getMoneySetList();
		List<ModelClass> additionMoneyReasonList = utilsService.getAdditionMoneyReason();
		
		if (salesProfitModel.getEmployeeName() != null) {
			for (int i = 0; i < employeeNameToNo.size(); i++) {
				if (salesProfitModel.getEmployeeName().equals(employeeNameToNo.get(i).getEmployeeFristName()
						+ employeeNameToNo.get(i).getEmployeeLastName())) {
					salesProfitModel.setEmployeeName(employeeNameToNo.get(i).getEmployeeNo());
					break;
				}
			}
		}

		List<SalesInfoModel> siteList = salesProfitService.getSalesInfo(salesProfitModel);
		logger.info("SalesProfitController.getSalesPointInfo:" + "検索結束");
		if (siteList.size() > 0) {
			int siteRoleNameAll = 0;
			int BPsiteRoleNameAll = 0;
			int profitAll = 0;

			Iterator<SalesInfoModel> it = (Iterator<SalesInfoModel>) siteList.iterator();
			while (it.hasNext()) {
				String salesStaff = it.next().getSalesStaff(); // 营业者
				if (TextUtils.isEmpty(salesStaff)) { // 没有营业的数据不需要返回
					it.remove();
				}
				
			}
			
			//将数据库中临时标记的999999做还原 lxf 20230412	
			for (int i = 0; i < siteList.size(); i++) {
				if("999999".equals(siteList.get(i).getAdmissionEndDate())) {
					siteList.get(i).setAdmissionEndDate(null);
				}
				String yearAndMonth = siteList.get(i).getAdmissionStartDate().substring(0, 6);
				/*
				 * if (i > 0) { String yearAndMonthTemp = yearAndMonth.substring(0, 4) + "/" +
				 * yearAndMonth.substring(4, 6); if (yearAndMonthTemp.equals(siteList.get(i -
				 * 1).getYearAndMonth())) { yearAndMonth = ""; } else { yearAndMonth =
				 * yearAndMonthTemp; } } else { yearAndMonth = yearAndMonth.substring(0, 4) +
				 * "/" + yearAndMonth.substring(4, 6); }
				 */
				yearAndMonth = yearAndMonth.substring(0, 4) + "/" + yearAndMonth.substring(4, 6);
				siteList.get(i).setYearAndMonth(yearAndMonth);
				siteList.get(i)
						.setCustomerName(siteList.get(i).getCustomerAbbreviation() == null
								|| siteList.get(i).getCustomerAbbreviation().equals("")
										? siteList.get(i).getCustomerName()
										: siteList.get(i).getCustomerAbbreviation());
				String employeeName = (siteList.get(i).getEmployeeFristName() == null ? ""
						: siteList.get(i).getEmployeeFristName())
						+ (siteList.get(i).getEmployeeLastName() == null ? "" : siteList.get(i).getEmployeeLastName());
				if (siteList.get(i).getEmployeeNo().substring(0, 2).equals("SP"))
					employeeName += "(SP)";
				else if (siteList.get(i).getEmployeeNo().substring(0, 2).equals("SC"))
					employeeName += "(SC)";
				siteList.get(i).setEmployeeName(employeeName);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
				// String startTime =
				// dateFormat.format(salesProfitModel.getStartDate()).toString();
				// String endTime = dateFormat.format(salesProfitModel.getEndDate()).toString();
				String startTime = salesProfitModel.getStartTime().toString();
				String endTime = salesProfitModel.getEndTime().toString();
				String admissionStartDate = siteList.get(i).getAdmissionStartDate().substring(0, 6);
				String admissionEndDate = "";
				if (siteList.get(i).getAdmissionEndDate() != null)
					admissionEndDate = siteList.get(i).getAdmissionEndDate().substring(0, 6);
				String workDateStart = admissionStartDate;
				String workDateEnd = admissionEndDate;
				if (Integer.parseInt(startTime) > Integer.parseInt(admissionStartDate)) {
					workDateStart = startTime;
				}
				
				if (admissionEndDate.equals("") || Integer.parseInt(endTime) < Integer.parseInt(admissionEndDate)) {
					workDateEnd = endTime;
				}

				
				String myWorkDateStart = siteList.get(i).getAdmissionStartDate().substring(0, 4) + "/"
						+ siteList.get(i).getAdmissionStartDate().substring(4, 6) + "/"
						+ siteList.get(i).getAdmissionStartDate().substring(6, 8) ;
				String myWorkDateStartEnd = "";
				String workEndYearMonth = "";
				if(siteList.get(i).getAdmissionEndDate() != null) {
					myWorkDateStartEnd = siteList.get(i).getAdmissionEndDate().substring(0, 4) + "/"
							+ siteList.get(i).getAdmissionEndDate().substring(4, 6) + "/"
							+ siteList.get(i).getAdmissionEndDate().substring(6, 8);
					
					workEndYearMonth = siteList.get(i).getAdmissionEndDate().substring(0, 6);
				}
				siteList.get(i).setWorkDate(myWorkDateStart + '~' + myWorkDateStartEnd);
		
					

				if (workDateEnd.equals(""))
					workDateEnd = endTime;
				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
				int months = Months.monthsBetween(
						formatter.parseDateTime(workDateStart.substring(0, 4) + "-" + workDateStart.substring(4, 6)),
						formatter.parseDateTime(workDateEnd.substring(0, 4) + "-" + workDateEnd.substring(4, 6)))
						.getMonths() + 1;
				siteList.get(i).setProfit(Integer.toString(Integer.parseInt(siteList.get(i).getUnitPrice()) * months));
				siteList.get(i).setMonth(months);
				profitAll += Integer.parseInt(siteList.get(i).getUnitPrice()) * months;
				String startYandM = workDateStart;
				String endYandM = workDateEnd;
				List<String> getYandM = new ArrayList<String>();
				if (startYandM != "0" && startYandM != null && endYandM != "0" && endYandM != null) {
					int startY = Integer.parseInt(startYandM.substring(0, 4));
					int startM = Integer.parseInt(startYandM.substring(4, 6));
					int endY = Integer.parseInt(endYandM.substring(0, 4));
					int endM = Integer.parseInt(endYandM.substring(4, 6));
					int count = 0;
					for (int y = startY; y <= endY; y++) {
						if (y == startY && y == endY) {
							for (int m = startM; m <= endM; m++) {
								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(startY) + monthStr);
								count++;
							}
						} else if (y == startY) {
							for (int m = startM; m <= 12; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(startY) + monthStr);
								count++;
							}
						} else if (y != startY && y != endY) {
							for (int m = 1; m <= 12; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(y) + monthStr);
								count++;
							}
						} else if (y == endY) {
							for (int m = 1; m <= endM; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(endY) + monthStr);
								count++;
							}
						}

					}
				}
				int salary = 0;
				for (int z = 0; z < getYandM.size(); z++) {
					String employeeNo = siteList.get(i).getEmployeeNo();
					String yearMonth = getYandM.get(z);
					int salaryTemp = 0;

					for (int x = 0; x < employeeSales.size(); x++) {
						if (employeeNo.equals(employeeSales.get(x).getEmployeeNo())) {
							if (Integer.parseInt(employeeSales.get(x).getReflectYearAndMonth()) <= Integer
									.parseInt(yearMonth)) {
								if (!employeeSales.get(x).getSalary().equals(""))
									salaryTemp = Integer.parseInt(employeeSales.get(x).getSalary());
							}
						}

					}
					salary += salaryTemp;
				}
				siteList.get(i).setSalary(Integer.toString(salary));
				siteList.get(i).setRowNo(Integer.toString(i + 1));

				// bpSalary
				int bpSalary = 0;
				if (siteList.get(i).getEmployeeStatus() != null && siteList.get(i).getEmployeeStatus().equals("1")) {
					if (siteList.get(i).getBpUnitPrice() != null) {
						if (!siteList.get(i).getBpUnitPrice().equals(""))
							bpSalary = Integer.parseInt(siteList.get(i).getBpUnitPrice()) * months * 10000;
						siteList.get(i).setSalary(Integer.toString(bpSalary));
					}
				}

				// siteRoleName 粗利
				int siteRoleName = Integer.parseInt(siteList.get(i).getProfit())
						- Integer.parseInt(siteList.get(i).getSalary());
				siteList.get(i).setSiteRoleName(Integer.toString(siteRoleName));

				String eiGyooccupationCode = siteList.get(i).getSalesOccupationCode(); // 营业職種
				String employeeStatus = siteList.get(i).getEmployeeStatus(); // 社員区分
				String salesStaff = siteList.get(i).getSalesStaff(); // 营业者

				// siteRoleNameAll 粗利合计
				siteRoleNameAll +=siteRoleName;
				// BPSiteRoleName BP粗利
				double bPSiteRoleName = 0;

				if (salesStaff != null && eiGyooccupationCode != null) {
					if (eiGyooccupationCode.equals("5") ) {
						double rate = 0;
						if (employeeStatus.equals("1")) {
							rate = 0.3;
						} else if (employeeStatus.equals("2")) {
							rate = 0.5;
						}
						bPSiteRoleName = Integer.parseInt(siteList.get(i).getSiteRoleName()) * rate;
					} else if (eiGyooccupationCode.equals("1")) {
						// 职种是“营业”时， 运营的社员区分为BP，employeeStatus.equals("1")，则营业者粗利=10000
						if (employeeStatus.equals("1")) {
							int monthDif = getMonthDif(startTime, endTime, admissionStartDate, workEndYearMonth);
							bPSiteRoleName = 10000 * monthDif;
						}
						if (employeeStatus.equals("2")) {
							int monthDif = getMonthDif(startTime, endTime, admissionStartDate, workEndYearMonth);
							bPSiteRoleName = 10000 * monthDif;
						}
					}

				}
				
				siteList.get(i).setBpSiteRoleName(Double.toString(bPSiteRoleName));


				siteList.get(i).setEmployeeStatusName(siteList.get(i).getEmployeeStatus().equals("1") ? "協力" : "社員");

				if (!(siteList.get(i).getBpBelongCustomerCode() == null
						|| siteList.get(i).getBpBelongCustomerCode().equals(""))) {
					for (int z = 0; z < customerName.size(); z++) {
						if (siteList.get(i).getBpBelongCustomerCode().equals(customerName.get(z).getCustomerNo())) {
							siteList.get(i).setEmployeeFrom(customerName.get(z).getCustomerName());
						}
					}

				}
				if (siteList.get(i).getEmployeeFrom() == null)
					siteList.get(i).setEmployeeFrom("BP");
				if (siteList.get(i).getSiteRoleName() != null)
					siteList.get(i)
							.setSiteRoleName(formatString((float) Integer.parseInt(siteList.get(i).getSiteRoleName())));
				siteList.get(i).setUnitPrice(formatString((float) Integer.parseInt(siteList.get(i).getUnitPrice())));
				siteList.get(i).setProfit(formatString((float) Integer.parseInt(siteList.get(i).getProfit())));
				siteList.get(i).setSalary(formatString((float) Integer.parseInt(siteList.get(i).getSalary())));


				for (int j = 0; j < salesMoneySetList.size(); j++) {
					MoneySetModel moneySetModel = salesMoneySetList.get(j);
					if (null != moneySetModel && null != moneySetModel.getEmployeeNo() && moneySetModel.getEmployeeNo().equals(siteList.get(i).getEmployeeNo())) {
						int monthDif = 0;
						int money = moneySetModel.getAdditionMoneyNumber();
						if (moneySetModel.isAdditionNumberOfTimesFix()) {
							// 回数： 固定一回
							monthDif = getMonthDifFix(startTime, endTime, moneySetModel.getStartYearAndMonth());
							siteList.get(i).setRemarks("固定一回 (" + findAdditionMoneyReason(moneySetModel.getAdditionMoneyResonCode(),  additionMoneyReasonList) +")");
						} else {
							// 回数： 每月
							monthDif = getMonthDif(startTime, endTime, moneySetModel.getStartYearAndMonth(), workEndYearMonth);
							siteList.get(i).setRemarks("每月 (" + findAdditionMoneyReason(moneySetModel.getAdditionMoneyResonCode(),  additionMoneyReasonList) +")");
						}
						bPSiteRoleName = money * monthDif;
						siteList.get(i).setBpSiteRoleName(Double.toString(bPSiteRoleName));
						break;
					}
				}
				

				// BPsiteRoleNameAll BP粗利合计
				BPsiteRoleNameAll += bPSiteRoleName;
			}

			siteList.get(0).setProfitAll(formatString((float) profitAll));
			siteList.get(0).setSiteRoleNameAll(formatString((float) siteRoleNameAll));
			siteList.get(0).setBpSiteRoleNameAll(formatString((float) BPsiteRoleNameAll));
		
		}
		return siteList;
	}

	private String findAdditionMoneyReason(String code, List<ModelClass> list) {
		if (TextUtils.isEmpty(code)) {
			return "";
		}
		for (int i = 0; i < list.size(); i++) {
			if (code.equals(list.get(i).getCode())) {
				return list.get(i).getName();
			}
			
		}
		return code;
	}


	/**
	 * junit test /CMS/src/test/java/SalesProfitControllerTest.java
	 */
	public int getMonthDifFix(String searchStartTime, String searchEndTime, String moneySetStartTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		try {

			Calendar cSearchStartTime = Calendar.getInstance();
			cSearchStartTime.setTime(dateFormat.parse(searchStartTime));
			
			Calendar cSearchEndTime = Calendar.getInstance();
			cSearchEndTime.setTime(dateFormat.parse(searchEndTime));
			
			Calendar cMoneySetTime = Calendar.getInstance();
			cMoneySetTime.setTime(dateFormat.parse(moneySetStartTime));

			if (cMoneySetTime.compareTo(cSearchStartTime) == 0 || cMoneySetTime.compareTo(cSearchEndTime) == 0 || (cMoneySetTime.after(cSearchStartTime) && cMoneySetTime.before(cSearchEndTime))) {
				return 1;
			}
			return 0;
//			String MONTH_6 = "6";
//			String MONTH_11 = "11";
//			if (cMoneySetTime.before(cSearchStartTime) || cMoneySetTime.compareTo(cSearchStartTime) == 0) {
//				// 如果设置time 早于<=搜索start时间，则DIF = (end - start)之间的存在6,11月份，则返回1
//				int yearDif = cSearchEndTime.get(Calendar.YEAR) - cSearchStartTime.get(Calendar.YEAR);
//				if(yearDif == 0) {
//					int startMonth = cSearchStartTime.get(Calendar.MONTH) + 1;
//					int endMonth = cSearchEndTime.get(Calendar.MONTH) + 1;
//					for (int i = startMonth; i <= endMonth; i++) {
//						if (MONTH_6.equals(String.valueOf(i))  || MONTH_11.equals(String.valueOf(i))) {
//							return 1;
//						}
//					}
//					
//				} else {
//					List<String> monethList = new ArrayList<String>();
//					while (cSearchStartTime.before(cSearchEndTime)) {
//						monethList.add(String.valueOf(cSearchStartTime.get(Calendar.MONTH) + 1));
//						cSearchStartTime.add(Calendar.MONTH, 1);
//					}
//					System.out.println("monethList=" + monethList);
//					if (monethList.contains(MONTH_6) || (monethList.contains(MONTH_11))) {
//						return 1;
//					}
//				}
//			} else if (cMoneySetTime.after(cSearchEndTime)) {
//				// 如果设置time 晚于搜索end时间，则DIF = 0
//				return 0;
//			} else {
//				// 如果设置time 介于搜索start - end时间，则DIF = (end - 设置time)之间的存在6,11月份，则返回1
//				int yearDif = cSearchEndTime.get(Calendar.YEAR) - cMoneySetTime.get(Calendar.YEAR);
//				if(yearDif == 0) {
//					int startMonth = cMoneySetTime.get(Calendar.MONTH) + 1;
//					int endMonth = cSearchEndTime.get(Calendar.MONTH) + 1;
//					for (int i = startMonth; i <= endMonth; i++) {
//						if (MONTH_6.equals(String.valueOf(i))  || MONTH_11.equals(String.valueOf(i))) {
//							return 1;
//						}
//					}
//				} else {
//					List<String> monethList = new ArrayList<String>();
//					while (cSearchStartTime.before(cSearchEndTime)) {
//						monethList.add(String.valueOf(cSearchStartTime.get(Calendar.MONTH) + 1));
//						cSearchStartTime.add(Calendar.MONTH, 1);
//					}
//					System.out.println("monethList=" + monethList);
//					if (monethList.contains(MONTH_6) || (monethList.contains(MONTH_11))) {
//						return 1;
//					}
//					
//				}
//				return 0;
//			}

			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}
	
	/**
	 * junit test /CMS/src/test/java/SalesProfitControllerTest.java
	 */
	public int getMonthDif(String searchStartTime, String searchEndTime, String moneySetStartTime, String workEndYearMonth) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		try {

			Calendar cSearchStartTime = Calendar.getInstance();
			cSearchStartTime.setTime(dateFormat.parse(searchStartTime));
			
			Calendar cSearchEndTime = Calendar.getInstance();
			cSearchEndTime.setTime(dateFormat.parse(searchEndTime));
			
			Calendar cMoneySetTime = Calendar.getInstance();
			cMoneySetTime.setTime(dateFormat.parse(moneySetStartTime));
			
			Calendar cFinallyEndTime = cSearchEndTime;
			
			Calendar cWorkEndYearMonth = null;
		    if (workEndYearMonth!=null && !workEndYearMonth.equals("")) {
		    	cWorkEndYearMonth = Calendar.getInstance();
		    	cWorkEndYearMonth.setTime(dateFormat.parse(workEndYearMonth));

		    	//比较现场结束时间和搜索栏中的结束时间值
		    	int endTimeTDif = ((cSearchEndTime.get(Calendar.YEAR) - cWorkEndYearMonth.get(Calendar.YEAR)) * 12) + (cSearchEndTime.get(Calendar.MONTH) - cWorkEndYearMonth.get(Calendar.MONTH) + 1);
		    	if (endTimeTDif > 0) {
		    		cFinallyEndTime = cWorkEndYearMonth;
		    	}
		    }

			// 如果设置time 早于搜索start时间，则dif = end - start
		    if (cMoneySetTime.before(cSearchStartTime) || cMoneySetTime.compareTo(cSearchStartTime) == 0) {
		    	return ((cFinallyEndTime.get(Calendar.YEAR) - cSearchStartTime.get(Calendar.YEAR)) * 12) + (cFinallyEndTime.get(Calendar.MONTH) - cSearchStartTime.get(Calendar.MONTH) + 1);
		    }

			// searchEndTime=202304, moneySetStartTime=202304
			if (cFinallyEndTime.compareTo(cMoneySetTime) == 0) {
				return 1;
			}

			// 如果设置time 晚于搜索end时间，则dif = 0
			if (cMoneySetTime.after(cFinallyEndTime)) {
				return 0;
			}

			// 如果设置time 介于搜索start - end之间 ，则dif = end - 设置time
			if (cMoneySetTime.after(cSearchStartTime) && cMoneySetTime.before(cFinallyEndTime)) {
				return ((cFinallyEndTime.get(Calendar.YEAR) - cMoneySetTime.get(Calendar.YEAR)) * 12) + (cFinallyEndTime.get(Calendar.MONTH) - cMoneySetTime.get(Calendar.MONTH)+1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private String formatString(Float data) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(data);
	}

	public List<SalesInfoModel> getSalesInfoByPdf(@RequestBody SalesProfitModel salesProfitModel,
			List<SalesInfoModel> employeeSales, List<SalesEmployeeModel> customerName,
			List<SalesInfoModel> employeeNameToNo) {

		if (salesProfitModel.getEmployeeName() != null && salesProfitModel.getEmployeeName().equals(""))
			salesProfitModel.setEmployeeName(null);
		if (salesProfitModel.getEmployeeStatus() != null && salesProfitModel.getEmployeeStatus().equals(""))
			salesProfitModel.setEmployeeStatus(null);

		if (salesProfitModel.getEmployeeName() != null) {
			for (int i = 0; i < employeeNameToNo.size(); i++) {
				if (salesProfitModel.getEmployeeName().equals(employeeNameToNo.get(i).getEmployeeFristName()
						+ employeeNameToNo.get(i).getEmployeeLastName())) {
					salesProfitModel.setEmployeeName(employeeNameToNo.get(i).getEmployeeNo());
					break;
				}
			}
		}

		List<SalesInfoModel> siteList = salesProfitService.getSalesInfo(salesProfitModel);
		logger.info("SalesProfitController.getSalesPointInfo:" + "検索結束");
		if (siteList.size() > 0) {
			int siteRoleNameAll = 0;
			int profitAll = 0;
			for (int i = 0; i < siteList.size(); i++) {
				String yearAndMonth = siteList.get(i).getAdmissionStartDate().substring(0, 6);
				yearAndMonth = yearAndMonth.substring(0, 4) + "/" + yearAndMonth.substring(4, 6);
				siteList.get(i).setYearAndMonth(yearAndMonth);
				siteList.get(i).setCustomerName(
						siteList.get(i).getCustomerAbbreviation() == null ? siteList.get(i).getCustomerName()
								: siteList.get(i).getCustomerAbbreviation());
				String employeeName = (siteList.get(i).getEmployeeFristName() == null ? ""
						: siteList.get(i).getEmployeeFristName())
						+ (siteList.get(i).getEmployeeLastName() == null ? "" : siteList.get(i).getEmployeeLastName());
				siteList.get(i).setEmployeeName(employeeName);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
				String startTime = dateFormat.format(salesProfitModel.getStartDate()).toString();
				String endTime = dateFormat.format(salesProfitModel.getEndDate()).toString();
				String admissionStartDate = siteList.get(i).getAdmissionStartDate().substring(0, 6);
				String admissionEndDate = "";
				if (siteList.get(i).getAdmissionEndDate() != null)
					admissionEndDate = siteList.get(i).getAdmissionEndDate().substring(0, 6);
				String workDateStart = admissionStartDate;
				String workDateEnd = admissionEndDate;
				if (Integer.parseInt(startTime) > Integer.parseInt(admissionStartDate)) {
					workDateStart = startTime;
				}

				if (workDateEnd.equals(""))
					siteList.get(i)
							.setWorkDate(workDateStart.substring(0, 4) + "/" + workDateStart.substring(4, 6) + " ~ ");
				else
					siteList.get(i).setWorkDate(workDateStart.substring(0, 4) + "/" + workDateStart.substring(4, 6)
							+ " ~ " + workDateEnd.substring(0, 4) + "/" + workDateEnd.substring(4, 6));

				if (workDateEnd.equals(""))
					workDateEnd = endTime;
				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
				int months = Months.monthsBetween(
						formatter.parseDateTime(workDateStart.substring(0, 4) + "-" + workDateStart.substring(4, 6)),
						formatter.parseDateTime(workDateEnd.substring(0, 4) + "-" + workDateEnd.substring(4, 6)))
						.getMonths() + 1;
				siteList.get(i).setProfit(Integer.toString(Integer.parseInt(siteList.get(i).getUnitPrice()) * months));
				profitAll += Integer.parseInt(siteList.get(i).getUnitPrice()) * months;
				String startYandM = workDateStart;
				String endYandM = workDateEnd;
				List<String> getYandM = new ArrayList<String>();
				if (startYandM != "0" && startYandM != null && endYandM != "0" && endYandM != null) {
					int startY = Integer.parseInt(startYandM.substring(0, 4));
					int startM = Integer.parseInt(startYandM.substring(4, 6));
					int endY = Integer.parseInt(endYandM.substring(0, 4));
					int endM = Integer.parseInt(endYandM.substring(4, 6));
					int count = 0;
					for (int y = startY; y <= endY; y++) {
						if (y == startY && y == endY) {
							for (int m = startM; m <= endM; m++) {
								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(startY) + monthStr);
								count++;
							}
						} else if (y == startY) {
							for (int m = startM; m <= 12; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(startY) + monthStr);
								count++;
							}
						} else if (y != startY && y != endY) {
							for (int m = 1; m <= 12; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(y) + monthStr);
								count++;
							}
						} else if (y == endY) {
							for (int m = 1; m <= endM; m++) {

								String monthStr = Integer.toString(m);
								if (m < 10) {
									monthStr = "0" + Integer.toString(m);
								}
								getYandM.add(count, Integer.toString(endY) + monthStr);
								count++;
							}
						}

					}
				}
				int salary = 0;
				for (int z = 0; z < getYandM.size(); z++) {
					String employeeNo = siteList.get(i).getEmployeeNo();
					String yearMonth = getYandM.get(z);
					int salaryTemp = 0;

					for (int x = 0; x < employeeSales.size(); x++) {
						if (employeeNo.equals(employeeSales.get(x).getEmployeeNo())) {
							if (Integer.parseInt(employeeSales.get(x).getReflectYearAndMonth()) <= Integer
									.parseInt(yearMonth)) {
								if (!employeeSales.get(x).getSalary().equals(""))
									salaryTemp = Integer.parseInt(employeeSales.get(x).getSalary());
							}
						}

					}
					salary += salaryTemp;
				}
				siteList.get(i).setSalary(Integer.toString(salary));
				siteList.get(i).setRowNo(Integer.toString(i + 1));
				String employeeStatus = "";
				int bpSalary = 0;
				if (!(siteList.get(i).getEmployeeStatus() == null)) {
					if (siteList.get(i).getEmployeeStatus().equals("0")) {
						employeeStatus = "社員";
						siteRoleNameAll += Integer.parseInt(siteList.get(i).getProfit()) - salary;
						siteList.get(i).setSiteRoleName(
								Integer.toString(Integer.parseInt(siteList.get(i).getProfit()) - salary));
					} else if (siteList.get(i).getEmployeeStatus().equals("1")) {
						employeeStatus = "協力";
						if (siteList.get(i).getBpUnitPrice() != null) {
							if (!siteList.get(i).getBpUnitPrice().equals(""))
								bpSalary = Integer.parseInt(siteList.get(i).getBpUnitPrice()) * months * 10000;
							siteList.get(i).setSalary(Integer.toString(bpSalary));
							siteRoleNameAll += Integer.parseInt(siteList.get(i).getProfit()) - bpSalary;
						}
						siteList.get(i).setSiteRoleName(
								Integer.toString(Integer.parseInt(siteList.get(i).getProfit()) - bpSalary));
					} else
						employeeStatus = "";
				}
				siteList.get(i).setEmployeeStatus(employeeStatus);
				if (!(siteList.get(i).getBpBelongCustomerCode() == null
						|| siteList.get(i).getBpBelongCustomerCode().equals(""))) {
					for (int z = 0; z < customerName.size(); z++) {
						if (siteList.get(i).getBpBelongCustomerCode().equals(customerName.get(z).getCustomerNo())) {
							siteList.get(i).setEmployeeFrom(customerName.get(z).getCustomerName());
						}
					}

				}
				if (siteList.get(i).getEmployeeFrom() == null)
					siteList.get(i).setEmployeeFrom("BP");
				if (siteList.get(i).getSiteRoleName() != null)
					siteList.get(i)
							.setSiteRoleName(formatString((float) Integer.parseInt(siteList.get(i).getSiteRoleName())));
				siteList.get(i).setUnitPrice(formatString((float) Integer.parseInt(siteList.get(i).getUnitPrice())));
				siteList.get(i).setProfit(formatString((float) Integer.parseInt(siteList.get(i).getProfit())));
				siteList.get(i).setSalary(formatString((float) Integer.parseInt(siteList.get(i).getSalary())));

			}

			siteList.get(0).setProfitAll(formatString((float) profitAll));
			siteList.get(0).setSiteRoleNameAll(formatString((float) siteRoleNameAll));
		}
		return siteList;
	}
}
