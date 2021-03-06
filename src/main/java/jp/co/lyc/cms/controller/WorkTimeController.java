package jp.co.lyc.cms.controller;

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
import jp.co.lyc.cms.model.WorkTimeModel;
import jp.co.lyc.cms.service.WorkTimeService;

@Controller
@RequestMapping(value = "/workTimeSearch")
public class WorkTimeController extends BaseController { 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	WorkTimeService workTimeService;
	/**
	 * 
	 * 検索
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectWorkTime", method = RequestMethod.POST)
	@ResponseBody
	public List<WorkTimeModel> selectWorkTime(@RequestBody WorkTimeModel workTimeModel) {
		if(workTimeModel.getYearAndMonth1().equals("")) {
			workTimeModel.setYearAndMonth1("000000");
		}
		if(workTimeModel.getYearAndMonth2().equals("")) {
			workTimeModel.setYearAndMonth2("999999");
		}
		logger.info("CostRegistrationController.selectCostRegistration:" + "検索開始");
		workTimeModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		List<WorkTimeModel> checkMod = workTimeService.selectWorkTime(workTimeModel);
		logger.info("CostRegistrationController.selectCostRegistration:" + "検索終了");
		return checkMod;
	}
}
