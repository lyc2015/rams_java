package jp.co.lyc.cms.controller;

import java.util.ArrayList;
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

import jp.co.lyc.cms.model.SendInvoiceModel;
import jp.co.lyc.cms.model.SendInvoiceWorkTimeModel;
import jp.co.lyc.cms.service.SendInvoiceService;

@Controller
@RequestMapping(value = "/sendInvoice")
public class SendInvoiceController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	SendInvoiceService sendInvoiceService;

	/**
	 * データ検索
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectSendInvoice", method = RequestMethod.POST)
	@ResponseBody
	public List<SendInvoiceModel> selectSendInvoice(@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("DutyManagementController.selectDutyManagement:" + "検索開始");
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

		logger.info("DutyManagementController.selectDutyManagement:" + "検索終了");
		return returnList;
	}
}
