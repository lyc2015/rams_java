package jp.co.lyc.cms.controller;

import java.text.ParseException;
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
		return resultList; 
	}

	@RequestMapping(value = "/insertMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertMoneySet(@RequestBody MoneySetModel model) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		int reultCount = salesMoneySetService.insertMoneySet(model);
		result.put("insertIndex", reultCount);
		return result;
	}

	@RequestMapping(value = "/updateMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateMoneySet(@RequestBody MoneySetModel model) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		int reultCount = salesMoneySetService.updateMoneySet(model);
		result.put("updateIndex", reultCount);
		return result;
	}

	@RequestMapping(value = "/deleteMoneySet", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteMoneySet(@RequestBody String id) throws ParseException {
		Map<String, Object> result = new HashMap<>();
		int reultCount = salesMoneySetService.deleteMoneySet(id);
		result.put("deleteIndex", reultCount);
		return result;
	}
}
