package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.SalesSendLetterModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.service.SalesSendLetterService;

@Controller
@RequestMapping(value = "/salesSendLetters")
public class SalesSendLetterController  extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesSendLetterService salesSendLetterService;

	/**
	 * データを取得
	 * 
	 * @param なし
	 * @return List
	 */

	@RequestMapping(value = "/getCustomers", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSendLetterModel> getSalesSituation() {

		logger.info("getCustomers:" + "検索開始");
		List<SalesSendLetterModel> salesCustomersList = new ArrayList<SalesSendLetterModel>();
		try {
			salesCustomersList = salesSendLetterService.getSalesCustomers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getCustomers" + "検索結束");
		return salesCustomersList;
	}
	
	@RequestMapping(value = "/getSalesPersons", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSendLetterModel> getSalesPersons(@RequestBody SalesSendLetterModel model) {

		logger.info("getSalesPersons:" + "検索開始");
		List<SalesSendLetterModel> salesPersonsList = new ArrayList<SalesSendLetterModel>();
		try {
			salesPersonsList = salesSendLetterService.getSalesPersons(model.getCustomerNo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesPersons" + "検索結束");
		return salesPersonsList;
	}
	
	@RequestMapping(value = "/creatList", method = RequestMethod.POST)
	@ResponseBody
	public int creatList(@RequestBody SalesSendLetterModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("getSalesPersons:" + "検索開始");
		int index=0;
		try {
			index = salesSendLetterService.creatList(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesPersons" + "検索結束");
		return index;
	}
	
	@RequestMapping(value = "/getLists", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSendLetterModel> getLists() {

		List<SalesSendLetterModel> salesPersonsList = new ArrayList<SalesSendLetterModel>();
		try {
			salesPersonsList = salesSendLetterService.getLists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesPersons" + "検索結束");
		return salesPersonsList;
	}
	}
