package jp.co.lyc.cms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.SendInvoiceModel;
import jp.co.lyc.cms.model.SendInvoiceWorkTimeModel;
import jp.co.lyc.cms.service.SendInvoiceService;
import jp.co.lyc.cms.service.UtilsService;

@Controller
@RequestMapping(value = "/sendInvoice")
public class SendInvoiceController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	SendInvoiceService sendInvoiceService;
	@Autowired
	UtilsService utilsService;

	/**
	 * データ検索
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectSendInvoice", method = RequestMethod.POST)
	@ResponseBody
	public List<SendInvoiceModel> selectSendInvoice(@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("SendInvoiceController.selectSendInvoice:" + "検索開始");
		List<SendInvoiceModel> returnList = new ArrayList<SendInvoiceModel>();
		List<SendInvoiceModel> sendInvoiceList = sendInvoiceService.selectSendInvoice(dutyManagementModel);
		List<SendInvoiceWorkTimeModel> sendInvoiceWorkTimeList = new ArrayList<SendInvoiceWorkTimeModel>();
		int rowNo = 0;
		for (int i = 0; i < sendInvoiceList.size(); i++) {
			SendInvoiceWorkTimeModel sendInvoiceWorkTimeModel = new SendInvoiceWorkTimeModel();
			rowNo++;
			sendInvoiceWorkTimeModel.setRowNo(rowNo);
			sendInvoiceWorkTimeModel.setEmployeeNo(sendInvoiceList.get(i).getEmployeeNo());
			sendInvoiceWorkTimeModel.setEmployeeName(sendInvoiceList.get(i).getEmployeeName());
			sendInvoiceWorkTimeModel.setSystemName(sendInvoiceList.get(i).getSystemName());
			sendInvoiceWorkTimeModel.setUnitPrice(sendInvoiceList.get(i).getUnitPrice());
			sendInvoiceWorkTimeModel.setPayOffRange1(sendInvoiceList.get(i).getPayOffRange1());
			sendInvoiceWorkTimeModel.setPayOffRange2(sendInvoiceList.get(i).getPayOffRange2());
			sendInvoiceWorkTimeModel.setSumWorkTime(sendInvoiceList.get(i).getSumWorkTime());
			sendInvoiceWorkTimeModel.setDeductionsAndOvertimePayOfUnitPrice(
					sendInvoiceList.get(i).getDeductionsAndOvertimePayOfUnitPrice());
			sendInvoiceWorkTimeList.add(sendInvoiceWorkTimeModel);

			if (i != sendInvoiceList.size() - 1
					&& !sendInvoiceList.get(i).getCustomerNo().equals(sendInvoiceList.get(i + 1).getCustomerNo())) {
				sendInvoiceList.get(i).setSendInvoiceWorkTimeModel(sendInvoiceWorkTimeList);
				returnList.add(sendInvoiceList.get(i));
				sendInvoiceWorkTimeList = new ArrayList<SendInvoiceWorkTimeModel>();
				rowNo = 0;
			} else if (i == sendInvoiceList.size() - 1) {
				sendInvoiceList.get(i).setSendInvoiceWorkTimeModel(sendInvoiceWorkTimeList);
				returnList.add(sendInvoiceList.get(i));
			}
		}

		for (int i = 0; i < returnList.size(); i++) {
			returnList.get(i).setRowNo(i + 1);
		}

		logger.info("SendInvoiceController.selectSendInvoice:" + "検索終了");
		return returnList;
	}

	/**
	 * データ検索
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectSendInvoiceByCustomerNo", method = RequestMethod.POST)
	@ResponseBody
	public List<SendInvoiceWorkTimeModel> selectSendInvoiceByCustomerNo(
			@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("SendInvoiceController.selectSendInvoiceByCustomerNo:" + "検索開始");
		List<SendInvoiceWorkTimeModel> selectSendInvoiceByCustomerNoList = sendInvoiceService
				.selectSendInvoiceByCustomerNo(dutyManagementModel);

		if (!(selectSendInvoiceByCustomerNoList.size() > 0)) {
			List<SendInvoiceWorkTimeModel> returnList = new ArrayList<SendInvoiceWorkTimeModel>();
			List<SendInvoiceWorkTimeModel> sendInvoiceWorkTimeModelList = new ArrayList<SendInvoiceWorkTimeModel>();
			List<SendInvoiceModel> sendInvoiceList = sendInvoiceService.selectSendInvoice(dutyManagementModel);
			for (int i = 0; i < sendInvoiceList.size(); i++) {
				SendInvoiceWorkTimeModel sendInvoiceWorkTimeModel = new SendInvoiceWorkTimeModel();
				sendInvoiceWorkTimeModel.setEmployeeNo(sendInvoiceList.get(i).getEmployeeNo());
				sendInvoiceWorkTimeModel.setEmployeeName(sendInvoiceList.get(i).getEmployeeName());
				sendInvoiceWorkTimeModel.setCustomerNo(sendInvoiceList.get(i).getCustomerNo());
				sendInvoiceWorkTimeModel.setCustomerName(sendInvoiceList.get(i).getCustomerName());
				sendInvoiceWorkTimeModel.setSystemName(sendInvoiceList.get(i).getSystemName());
				sendInvoiceWorkTimeModel.setUnitPrice(sendInvoiceList.get(i).getUnitPrice());
				sendInvoiceWorkTimeModel.setPayOffRange1(sendInvoiceList.get(i).getPayOffRange1());
				sendInvoiceWorkTimeModel.setPayOffRange2(sendInvoiceList.get(i).getPayOffRange2());
				sendInvoiceWorkTimeModel.setSumWorkTime(sendInvoiceList.get(i).getSumWorkTime());
				sendInvoiceWorkTimeModel.setDeductionsAndOvertimePayOfUnitPrice(
						sendInvoiceList.get(i).getDeductionsAndOvertimePayOfUnitPrice());
				sendInvoiceWorkTimeModel.setRequestUnitCode("0");
				sendInvoiceWorkTimeModelList.add(sendInvoiceWorkTimeModel);
			}

			List<CostRegistrationModel> costRegistrationList = sendInvoiceService
					.selectCostRegistration(dutyManagementModel);
			List<ModelClass> list = utilsService.getStation();

			for (int i = 0; i < costRegistrationList.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					if (costRegistrationList.get(i).getOriginCode() != null
							&& costRegistrationList.get(i).getOriginCode().equals(list.get(j).getCode())) {
						costRegistrationList.get(i).setOriginCode(list.get(j).getName());
					}
					if (costRegistrationList.get(i).getDestinationCode() != null
							&& costRegistrationList.get(i).getDestinationCode().equals(list.get(j).getCode())) {
						costRegistrationList.get(i).setDestinationCode(list.get(j).getName());
					}
				}
			}

			for (int i = 0; i < sendInvoiceWorkTimeModelList.size(); i++) {
				returnList.add(sendInvoiceWorkTimeModelList.get(i));

				List<SendInvoiceWorkTimeModel> tempList = new ArrayList<SendInvoiceWorkTimeModel>();
				for (int j = 0; j < costRegistrationList.size(); j++) {
					if (sendInvoiceWorkTimeModelList.get(i).getEmployeeNo()
							.equals(costRegistrationList.get(j).getEmployeeNo())) {
						SendInvoiceWorkTimeModel sendInvoiceWorkTimeModel = new SendInvoiceWorkTimeModel();
						sendInvoiceWorkTimeModel.setEmployeeNo(sendInvoiceWorkTimeModelList.get(i).getEmployeeNo());
						sendInvoiceWorkTimeModel.setEmployeeName(sendInvoiceWorkTimeModelList.get(i).getEmployeeName());
						sendInvoiceWorkTimeModel.setCustomerNo(sendInvoiceWorkTimeModelList.get(i).getCustomerNo());
						sendInvoiceWorkTimeModel.setCustomerName(sendInvoiceWorkTimeModelList.get(i).getCustomerName());
						switch (costRegistrationList.get(j).getCostClassificationCode()) {
						case "1":
							sendInvoiceWorkTimeModel
									.setSystemName(costRegistrationList.get(j).getCostClassificationName() + "("
											+ costRegistrationList.get(j).getOriginCode() + "-"
											+ costRegistrationList.get(j).getDestinationCode() + ")");
							break;
						default:
							sendInvoiceWorkTimeModel
									.setSystemName(costRegistrationList.get(j).getCostClassificationName() + "("
											+ costRegistrationList.get(j).getDetailedNameOrLine() + ")");
							break;
						}
						sendInvoiceWorkTimeModel.setUnitPrice(costRegistrationList.get(j).getCost());
						sendInvoiceWorkTimeModel.setPayOffRange1("");
						sendInvoiceWorkTimeModel.setPayOffRange2("");
						sendInvoiceWorkTimeModel.setSumWorkTime("");
						sendInvoiceWorkTimeModel.setDeductionsAndOvertimePayOfUnitPrice("");
						sendInvoiceWorkTimeModel.setRequestUnitCode("1");
						sendInvoiceWorkTimeModel.setWorkPeriod(costRegistrationList.get(j).getHappendDate());
						tempList.add(sendInvoiceWorkTimeModel);
					}
				}
				for (int j = 0; j < tempList.size(); j++) {
					returnList.add(tempList.get(j));
				}
			}

			for (int i = 0; i < returnList.size(); i++) {
				returnList.get(i).setRowNo(i + 1);
				HashMap<String, String> model = new HashMap<String, String>();
				model.put("customerNo", returnList.get(i).getCustomerNo());
				model.put("yearAndMonth", dutyManagementModel.get("yearAndMonth"));
				model.put("workContents",
						returnList.get(i).getWorkContents() == null
								? (returnList.get(i).getSystemName() == null ? "" : returnList.get(i).getSystemName())
								: returnList.get(i).getWorkContents());
				model.put("customerName", returnList.get(i).getCustomerName());
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
				model.put("invoiceDate", dateFormat.format(date));
				model.put("employeeNo", returnList.get(i).getEmployeeNo());
				model.put("workPeriod", returnList.get(i).getWorkPeriod());
				model.put("workingTime", returnList.get(i).getWorkingTime());
				model.put("requestUnitCode", returnList.get(i).getRequestUnitCode());
				model.put("unitPrice", returnList.get(i).getUnitPrice());
				model.put("updateUser", getSession().getAttribute("employeeName").toString());
				sendInvoiceService.insertInvoiceData(model);
			}

			logger.info("SendInvoiceController.selectSendInvoiceByCustomerNo:" + "検索終了");
			return returnList;
		} else {
			logger.info("SendInvoiceController.selectSendInvoiceByCustomerNo:" + "検索終了");
			for (int i = 0; i < selectSendInvoiceByCustomerNoList.size(); i++) {
				selectSendInvoiceByCustomerNoList.get(i).setRowNo(i + 1);
			}
			return selectSendInvoiceByCustomerNoList;
		}
	}

	/**
	 * アップデート
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateInvoiceData", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateInvoiceData(@RequestBody HashMap<String, String> model) {
		logger.info("SendInvoiceController.updateInvoiceData:" + "アップデート開始");
		model.put("updateUser", getSession().getAttribute("employeeName").toString());
		boolean result = false;
		try {
			if (model.get("oldWorkContents") == null)
				sendInvoiceService.insertNewInvoiceData(model);
			else
				sendInvoiceService.updateInvoiceData(model);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		logger.info("SendInvoiceController.updateInvoiceData:" + "アップデート終了");
		return result;
	}

	/**
	 * 削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/deleteInvoiceData", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteInvoiceData(@RequestBody HashMap<String, String> model) {
		logger.info("SendInvoiceController.deleteInvoiceData:" + "削除開始");
		boolean result = false;
		try {
			sendInvoiceService.deleteInvoiceData(model);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		logger.info("SendInvoiceController.deleteInvoiceData:" + "削除終了");
		return result;
	}

	/**
	 * アップデート
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateAllInvoiceData", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateAllInvoiceData(@RequestBody HashMap<String, String> model) {
		logger.info("SendInvoiceController.updateAllInvoiceData:" + "アップデート開始");
		model.put("updateUser", getSession().getAttribute("employeeName").toString());
		boolean result = false;
		try {
			sendInvoiceService.updateAllInvoiceData(model);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		logger.info("SendInvoiceController.updateAllInvoiceData:" + "アップデート終了");
		return result;
	}

	/**
	 * 銀行データ取得
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectBankAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<ModelClass> selectBankAccountInfo() {
		logger.info("SendInvoiceController.selectBankAccountInfo:" + "銀行データ取得開始");
		return sendInvoiceService.selectBankAccountInfo();
	}
}
