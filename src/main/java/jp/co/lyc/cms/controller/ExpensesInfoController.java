package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.mapper.ExpensesInfoMapper;
import jp.co.lyc.cms.model.ExpensesInfoModel;
import jp.co.lyc.cms.model.WagesInfoModel;
import jp.co.lyc.cms.service.ExpensesInfoService;
import jp.co.lyc.cms.validation.ExpensesInfoValidation;

@Controller
@RequestMapping(value = "/expensesInfo")
public class ExpensesInfoController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ExpensesInfoService expensesInfoService;
	@Autowired
	ExpensesInfoMapper expensesInfoMapper;

	String errorsMessage = "";

	/**
	 * 画面初期化
	 * 
	 * @return
	 */
	@RequestMapping(value = "/init", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> init() {
		Map<String, Object> result = new HashMap<String, Object>();
		return result;
	}

	/**
	 * 画面初期化
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getExpensesInfoModels", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getExpensesInfoModels(@RequestBody ExpensesInfoModel expensesInfoModel) {
		logger.info("ExpensesInfoController.getExpensesInfoModels:" + "取得開始");
		HashMap<String, String> sendMap = new HashMap<String, String>();
		sendMap.put("employeeNo", expensesInfoModel.getEmployeeNo());
		if (!expensesInfoModel.getExpensesReflectYearAndMonth().equals("")) {
			sendMap.put("start", expensesInfoModel.getExpensesReflectYearAndMonth().split("~")[0]);
			if (expensesInfoModel.getExpensesReflectYearAndMonth().split("~").length > 1) {
				sendMap.put("end", expensesInfoModel.getExpensesReflectYearAndMonth().split("~")[1]);
			}
		}
		ArrayList<ExpensesInfoModel> expensesInfoModelsList = expensesInfoMapper.getExpensesInfoModels(sendMap);
		for (int i = 0; i < expensesInfoModelsList.size(); i++) {
			if (sendMap.get("end") != null) {
				if (Integer.parseInt(expensesInfoModelsList.get(i).getExpensesReflectYearAndMonth()) > Integer
						.parseInt(sendMap.get("end"))) {
					expensesInfoModelsList.remove(i);
					break;
				}
			}
			String expensesPeriod = expensesInfoModelsList.get(i).getExpensesReflectYearAndMonth() + "~";
			if (i + 1 < expensesInfoModelsList.size()) {
				int endYearAndMonth = 0;
				if (expensesInfoModelsList.get(i + 1).getExpensesReflectYearAndMonth().substring(4, 6).equals("01")) {
					endYearAndMonth = Integer.parseInt(String.valueOf(Integer.parseInt(
							expensesInfoModelsList.get(i + 1).getExpensesReflectYearAndMonth().substring(0, 4)) - 1
							+ "12"));
				} else {
					endYearAndMonth = Integer
							.parseInt(expensesInfoModelsList.get(i + 1).getExpensesReflectYearAndMonth()) - 1;
				}
				expensesPeriod += String.valueOf(endYearAndMonth);
			}
			expensesInfoModelsList.get(i).setExpensesPeriod(expensesPeriod);
		}
		logger.info("ExpensesInfoController.getExpensesInfoModels:" + "取得終了");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("expensesInfoModels", expensesInfoModelsList);
		return result;
	}

	/**
	 * 登録ボタン
	 * 
	 * @param expensesInfoModel
	 * @return
	 */
	@RequestMapping(value = "/toroku", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> toroku(@RequestBody ExpensesInfoModel expensesInfoModel) {
		logger.info("ExpensesInfoController.onloadPage:" + "登録開始");
		Map<String, Object> result = new HashMap<String, Object>();
		errorsMessage = "";
		DataBinder binder = new DataBinder(expensesInfoModel);
		binder.setValidator(new ExpensesInfoValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		if (results.hasErrors()) {
			results.getAllErrors().forEach(o -> {
				FieldError error = (FieldError) o;
				errorsMessage += error.getDefaultMessage();// エラーメッセージ
			});
			result.put("errorsMessage", errorsMessage);// エラーメッセージ
			logger.info("ExpensesInfoController.onloadPage:" + "登録終了");
			return result;
		}
		HashMap<String, String> checkMap = new HashMap<String, String>();
		checkMap.put("employeeNo", expensesInfoModel.getEmployeeNo());
		ArrayList<ExpensesInfoModel> expenseInfoList = expensesInfoMapper.getExpensesInfo(checkMap);
		if (expenseInfoList.size() != 0) {
			int lastEnd = Integer
					.parseInt(expenseInfoList.get(expenseInfoList.size() - 1).getExpensesReflectYearAndMonth());
			int nowStart = Integer.parseInt(expensesInfoModel.getExpensesReflectYearAndMonth());
			if (nowStart < lastEnd) {
				errorsMessage += "新規諸費用情報の反映年月は既存データ以降にしてください";
				result.put("errorsMessage", errorsMessage);// エラーメッセージ
				return result;
			}
		}
		expensesInfoModel.setUpdateUser((String) getSession().getAttribute("employeeName"));
		if (expensesInfoModel.getActionType().equals("insert")) {// 插入场合
			HashMap<String, String> sendMap = new HashMap<String, String>();
			sendMap.put("employeeNo", expensesInfoModel.getEmployeeNo());
			sendMap.put("expensesReflectYearAndMonth", expensesInfoModel.getExpensesReflectYearAndMonth());
			ArrayList<ExpensesInfoModel> checkList = expensesInfoMapper.getExpensesInfo(sendMap);
			if (checkList.size() > 0) {
				result.put("errorsMessage", "該当社員の該当反映年月のデータがデータベースに存在するため、追加はできません！");// エラーメッセージ
				return result;
			}
			if (expensesInfoService.insert(expensesInfoModel)) {
				result.put("message", "追加成功");
			} else {
				result.put("errorsMessage", "追加失败");
			}
		} else if (expensesInfoModel.getActionType().equals("update")) {// 插入场合
			HashMap<String, String> sendMap = new HashMap<String, String>();
			sendMap.put("employeeNo", expensesInfoModel.getEmployeeNo());
			ArrayList<ExpensesInfoModel> lastList = expensesInfoMapper.getExpensesInfo(sendMap);
			if (!lastList.get(lastList.size() - 1).getExpensesReflectYearAndMonth()
					.equals(expensesInfoModel.getExpensesReflectYearAndMonth())) {
				sendMap.put("expensesReflectYearAndMonth", expensesInfoModel.getExpensesReflectYearAndMonth());
				ArrayList<ExpensesInfoModel> checkList = expensesInfoMapper.getExpensesInfo(sendMap);
				if (checkList.size() > 0) {
					result.put("errorsMessage", "更新した反映年月はデータベースにあるため、反映年月を確認してください！");// エラーメッセージ
					return result;
				}
			}
			expensesInfoModel.setUpdateExpensesReflectYearAndMonth(
					lastList.get(lastList.size() - 1).getExpensesReflectYearAndMonth());
			if (expensesInfoService.update(expensesInfoModel)) {
				result.put("message", "更新成功");
			} else {
				result.put("errorsMessage", "更新失败");
			}
		}
		return result;
	}

	/**
	 * 諸費用削除
	 * 
	 * @param deleteMod
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(@RequestBody ExpensesInfoModel deleteMod) {
		logger.info("ExpensesInfoController.delete:" + "削除開始");
		Map<String, Object> result = new HashMap<String, Object>();
		errorsMessage = "";
		DataBinder binder = new DataBinder(deleteMod);
		binder.setValidator(new ExpensesInfoValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		if (results.hasErrors()) {
			results.getAllErrors().forEach(o -> {
				FieldError error = (FieldError) o;
				errorsMessage += error.getDefaultMessage();// エラーメッセージ
			});
			result.put("errorsMessage", errorsMessage);// エラーメッセージ
			logger.info("ExpensesInfoController.onloadPage:" + "登録終了");
			return result;
		}
		boolean deleteResult = expensesInfoService.delete(deleteMod);
		if (!deleteResult) {
			result.put("errorsMessage", "削除失敗");
		}
		return result;
	}
}
