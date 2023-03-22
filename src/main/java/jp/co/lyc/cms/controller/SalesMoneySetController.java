package jp.co.lyc.cms.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.MoneySetModel;
import jp.co.lyc.cms.service.SalesMoneySetService;

@Controller
@RequestMapping(value = "/salesMoneySet")
public class SalesMoneySetController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesMoneySetService salesMoneySetService;

	@RequestMapping(value = "/getMoneySetList", method = RequestMethod.POST)
	@ResponseBody
	public List<MoneySetModel> getMoneySetList(@RequestBody MoneySetModel model) throws ParseException {
		List<MoneySetModel> resultList = salesMoneySetService.getMoneySetList();
		for(int i = 0; i < resultList.size(); i ++) {
			MoneySetModel setModel = resultList.get(i);
			if (null != setModel && "1".equals(setModel.getWorkState())) {
				setModel.setFinalSiteFinish(true);
			}

			if (!TextUtils.isEmpty(resultList.get(i).getEmployeeNo())) {
				if (resultList.get(i).getEmployeeNo().substring(0, 3).equals("BPR")) {
					resultList.get(i).setEmployeeNameTitle("(BPR)");
					
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
					resultList.get(i).setEmployeeNameTitle("(" + resultList.get(i).getBelongCustomerName() + ")");
					
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
					resultList.get(i).setEmployeeNameTitle("(SP)");
					
				} else if (resultList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
					resultList.get(i).setEmployeeNameTitle("(SC)");
					
				} else {

					resultList.get(i).setEmployeeNameTitle("");
				}
			}
		}
		return resultList; 
	}

	@RequestMapping(value = "/insertMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertMoneySet(@RequestBody MoneySetModel model) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		
		if (TextUtils.isEmpty(model.getEmployeeName())) {
			result.put("errorsMessage", "社員名入力してください。");
			return result;
		} 

		if (TextUtils.isEmpty(model.getAdditionMoneyCode())) {
			result.put("errorsMessage", "加算金額を選択してください");
			return result;
		} 

		if (TextUtils.isEmpty(model.getAdditionNumberOfTimesStatus())) {
			result.put("errorsMessage", "回數を選択してください");
			return result;
		} 
		
		if (TextUtils.isEmpty(model.getStartYearAndMonth())) {
			result.put("errorsMessage", "開始年月を選択してください");
			return result;
		} 
		
		if (TextUtils.isEmpty(model.getAdditionMoneyResonCode())) {
			result.put("errorsMessage", "加算理由を選択してください");
			return result;
		} 
		
		int reultCount = salesMoneySetService.insertMoneySet(model);
		result.put("insertIndex", reultCount);
		return result;
	}

	@RequestMapping(value = "/updateMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateMoneySet(@RequestBody MoneySetModel model) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		
		if (TextUtils.isEmpty(model.getEmployeeName())) {
			result.put("errorsMessage", "社員名入力してください。");
			return result;
		} 
		if (TextUtils.isEmpty(model.getAdditionMoneyCode())) {
			result.put("errorsMessage", "加算金額を選択してください");
			return result;
		} 

		if (TextUtils.isEmpty(model.getAdditionNumberOfTimesStatus())) {
			result.put("errorsMessage", "回數を選択してください");
			return result;
		} 
		
		if (TextUtils.isEmpty(model.getStartYearAndMonth())) {
			result.put("errorsMessage", "開始年月を選択してください");
			return result;
		} 
		
		if (TextUtils.isEmpty(model.getAdditionMoneyResonCode())) {
			result.put("errorsMessage", "加算理由を選択してください");
			return result;
		} 
		
		int reultCount = salesMoneySetService.updateMoneySet(model);
		result.put("updateIndex", reultCount);
		return result;
	}

	@RequestMapping(value = "/deleteMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteMoneySet(@RequestBody MoneySetModel model) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		int reultCount = salesMoneySetService.deleteMoneySet(model);
		result.put("deleteIndex", reultCount);
		return result;
	}
}
