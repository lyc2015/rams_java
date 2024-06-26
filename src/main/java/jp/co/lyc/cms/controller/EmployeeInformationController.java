package jp.co.lyc.cms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.lyc.cms.model.EmployeeInformationModel;
import jp.co.lyc.cms.service.EmployeeInformationService;

@Controller
@RequestMapping(value = "/EmployeeInformation")
public class EmployeeInformationController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	String errorsMessage = "";
	@Autowired
	EmployeeInformationService employeeInformationService;

	@RequestMapping(value = "/getEmployeeInformation", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getEmployeeInformation() {
		logger.info("GetEmployeeInfoController.getEmployeeInfo:" + "検索開始");

		List<EmployeeInformationModel> employeeList = new ArrayList<EmployeeInformationModel>();
		employeeList = employeeInformationService.getEmployeeInformation();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		String nowTime = String.format("%0" + 2 + "d", cal.get(Calendar.MONTH) + 1)
				+ String.format("%0" + 2 + "d", cal.get(Calendar.DATE));
		String today = String.valueOf(cal.get(Calendar.YEAR)) + nowTime;
		// 日数計算
		for (int i = 0; i < employeeList.size(); i++) {
			if (!(employeeList.get(i).getStayPeriod() == null || employeeList.get(i).getStayPeriod().equals(""))) {
				employeeList.get(i).setStayPeriodDate(dateDiff(today, employeeList.get(i).getStayPeriod()));
			}
			if (!(employeeList.get(i).getPassportStayPeriod() == null
					|| employeeList.get(i).getPassportStayPeriod().equals(""))) {
				employeeList.get(i)
						.setPassportStayPeriodDate(dateDiff(today, employeeList.get(i).getPassportStayPeriod()));
			}

			if (!(employeeList.get(i).getContractDeadline() == null
					|| employeeList.get(i).getContractDeadline().equals(""))) {
				employeeList.get(i).setContractDeadlineDate(dateDiff(today, employeeList.get(i).getContractDeadline()));
			}

			if (!(employeeList.get(i).getBirthday() == null || employeeList.get(i).getBirthday().equals(""))) {
				if (Integer.parseInt(nowTime) > Integer.parseInt(employeeList.get(i).getBirthday())) {
					String year = Integer.toString(cal.get(Calendar.YEAR) + 1);
					employeeList.get(i).setBirthdayDate(dateDiff(today, year + employeeList.get(i).getBirthday()));
				} else if (Integer.parseInt(nowTime) < Integer.parseInt(employeeList.get(i).getBirthday())) {
					String year = Integer.toString(cal.get(Calendar.YEAR));
					employeeList.get(i).setBirthdayDate(dateDiff(today, year + employeeList.get(i).getBirthday()));
				}
			} else {
				employeeList.get(i).setBirthdayDate(-1);
			}
		}

		// 排序
		List<EmployeeInformationModel> newEmployeeList = new ArrayList<EmployeeInformationModel>();
		List<EmployeeInformationModel> TempList = new ArrayList<EmployeeInformationModel>();
		/*
		 * for (int i = 0; i < employeeList.size(); i++) { if
		 * (!employeeList.get(i).getDealDistinctioCode().equals("2") &&
		 * ((employeeList.get(i).getStayPeriod().equals("") ? false :
		 * Integer.parseInt(employeeList.get(i).getStayPeriod()) <= 90) ||
		 * (employeeList.get(i).getBirthday().equals("") ? false :
		 * Integer.parseInt(employeeList.get(i).getBirthday()) <= 7) ||
		 * (employeeList.get(i).getContractDeadline().equals("") ? false :
		 * Integer.parseInt(employeeList.get(i).getContractDeadline()) <= 60))) {
		 * newEmployeeList.add(employeeList.get(i)); employeeList.remove(i); i--; } }
		 */

		// 在留カード
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getDealDistinctioCode().equals("2")
					&& (employeeList.get(i).getStayPeriod() == null || employeeList.get(i).getStayPeriod().equals("")
							? false
							: (employeeList.get(i).getStayPeriodDate()) <= 90)) {
				TempList.add(employeeList.get(i));
				employeeList.remove(i);
				i--;
			}
		}

		for (int x = 0; x < TempList.size() - 1; x++) {
			for (int y = x + 1; y < TempList.size(); y++) {
				if ((TempList.get(x).getStayPeriodDate()) > (TempList.get(y).getStayPeriodDate())) {
					EmployeeInformationModel temp = TempList.get(x);
					TempList.set(x, TempList.get(y));
					TempList.set(y, temp);
				}
			}
		}

		for (int i = 0; i < TempList.size(); i++) {
			newEmployeeList.add(TempList.get(i));
		}
		TempList.clear();

		// 誕生日
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getDealDistinctioCode().equals("2")
					&& (employeeList.get(i).getBirthday().equals("") ? false
							: (employeeList.get(i).getBirthdayDate()) <= 7)) {
				TempList.add(employeeList.get(i));
				employeeList.remove(i);
				i--;
			}
		}
		for (int x = 0; x < TempList.size() - 1; x++) {
			for (int y = x + 1; y < TempList.size(); y++) {
				if ((TempList.get(x).getBirthdayDate()) > (TempList.get(y).getBirthdayDate())) {
					EmployeeInformationModel temp = TempList.get(x);
					TempList.set(x, TempList.get(y));
					TempList.set(y, temp);
				}
			}
		}

		// パスポート
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getDealDistinctioCode().equals("2")
					&& (employeeList.get(i).getPassportStayPeriod() == null
							|| employeeList.get(i).getPassportStayPeriod().equals("")
									? false
									: (employeeList.get(i).getPassportStayPeriodDate()) <= 90)) {
				TempList.add(employeeList.get(i));
				employeeList.remove(i);
				i--;
			}
		}

		for (int i = 0; i < TempList.size(); i++) {
			newEmployeeList.add(TempList.get(i));
		}
		TempList.clear();

		// 契約
		for (int i = 0; i < employeeList.size(); i++) {
			if (!employeeList.get(i).getDealDistinctioCode().equals("2")
					&& (employeeList.get(i).getContractDeadline() == null
							|| employeeList.get(i).getContractDeadline().equals("") ? false
									: (employeeList.get(i).getContractDeadlineDate()) <= 60)) {
				TempList.add(employeeList.get(i));
				employeeList.remove(i);
				i--;
			}
		}
		for (int x = 0; x < TempList.size() - 1; x++) {
			for (int y = x + 1; y < TempList.size(); y++) {
				if ((TempList.get(x).getContractDeadlineDate()) > (TempList.get(y).getContractDeadlineDate())) {
					EmployeeInformationModel temp = TempList.get(x);
					TempList.set(x, TempList.get(y));
					TempList.set(y, temp);
				}
			}
		}
		for (int i = 0; i < TempList.size(); i++) {
			newEmployeeList.add(TempList.get(i));
		}
		TempList.clear();

		for (int i = 0; i < employeeList.size(); i++) {
			newEmployeeList.add(employeeList.get(i));
		}

		for (int i = 0; i < newEmployeeList.size(); i++) {
			newEmployeeList.get(i).setRowNo(i + 1);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("data", newEmployeeList);
		logger.info("GetEmployeeInfoController.getEmployeeInfo:" + "検索結束");
		return result;
	}

	@RequestMapping(value = "/updateEmployeeInformation", method = RequestMethod.POST)
	@ResponseBody
	public void updateEmployeeInformation(@RequestBody EmployeeInformationModel model) {
		List<EmployeeInformationModel> list = new ArrayList<EmployeeInformationModel>();
		for (int i = 0; i < model.getEmployeeNos().length; i++) {
			EmployeeInformationModel tempModel = new EmployeeInformationModel();
			tempModel.setEmployeeNo(model.getEmployeeNos()[i]);
			tempModel.setDealDistinctioCode(
					model.getDealDistinctioCodes()[i].equals("") ? "0" : model.getDealDistinctioCodes()[i]);
			list.add(tempModel);
		}
		employeeInformationService.updateEmployeeInformation(list);
	}

	public static int dateDiff(String dateFromString, String dateToString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dateFrom = null;
		Date dateTo = null;

		// Date型に変換
		try {
			dateFrom = sdf.parse(dateFromString);
			dateTo = sdf.parse(dateToString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		// 差分の日数を計算する
		long dateTimeTo = dateTo.getTime();
		long dateTimeFrom = dateFrom.getTime();
		long dayDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60 * 60 * 24);

		// System.out.println("差分日数 : " + dayDiff);
		return (int) dayDiff;
	}
}
