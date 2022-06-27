package jp.co.lyc.cms.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.emory.mathcs.backport.java.util.Collections;
import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.AccountInfoModel;
import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.EmailModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.ResultModel;
import jp.co.lyc.cms.model.SendInvoiceModel;
import jp.co.lyc.cms.model.SendInvoiceWorkTimeModel;
import jp.co.lyc.cms.service.SendInvoiceService;
import jp.co.lyc.cms.service.UtilsService;
import jp.co.lyc.cms.util.UtilsController;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.tabulator.SplitCell;

@Controller
@RequestMapping(value = "/sendInvoice")
public class SendInvoiceController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	SendInvoiceService sendInvoiceService;
	@Autowired
	UtilsService utilsService;
	@Autowired
	UtilsController utils;

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
		if (dutyManagementModel.get("employeeNo") == null) {
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
				sendInvoiceWorkTimeModel.setWorkingTimeReport(sendInvoiceList.get(i).getWorkingTimeReport());

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
		} else {
			// 根据employeeNo封装对应的要员列表数据
			SendInvoiceModel sendInvoiceModelBySelectedEmployeeNo = new SendInvoiceModel();
			List<SendInvoiceModel> sameCustomerNo = new ArrayList<SendInvoiceModel>();
			for (int i = 0; i < sendInvoiceList.size(); i++) {
				if (sendInvoiceList.get(i).getEmployeeNo().equals(dutyManagementModel.get("employeeNo"))) {
					sendInvoiceModelBySelectedEmployeeNo = sendInvoiceList.get(i);
					break;
				}
			}
			if (sendInvoiceModelBySelectedEmployeeNo != null) {
				for (int i = 0; i < sendInvoiceList.size(); i++) {
					if (sendInvoiceList.get(i).getCustomerNo()
							.equals(sendInvoiceModelBySelectedEmployeeNo.getCustomerNo())) {
						sameCustomerNo.add(sendInvoiceList.get(i));
					}
				}
				int rowNo2 = 0;
				for (int i = 0; i < sameCustomerNo.size(); i++) {
					SendInvoiceWorkTimeModel sendInvoiceWorkTimeModel = new SendInvoiceWorkTimeModel();
					rowNo2++;
					sendInvoiceWorkTimeModel.setRowNo(rowNo2);
					sendInvoiceWorkTimeModel.setEmployeeNo(sameCustomerNo.get(i).getEmployeeNo());
					sendInvoiceWorkTimeModel.setEmployeeName(sameCustomerNo.get(i).getEmployeeName());
					sendInvoiceWorkTimeModel.setSystemName(sameCustomerNo.get(i).getSystemName());
					sendInvoiceWorkTimeModel.setUnitPrice(sameCustomerNo.get(i).getUnitPrice());
					sendInvoiceWorkTimeModel.setPayOffRange1(sameCustomerNo.get(i).getPayOffRange1());
					sendInvoiceWorkTimeModel.setPayOffRange2(sameCustomerNo.get(i).getPayOffRange2());
					sendInvoiceWorkTimeModel.setSumWorkTime(sameCustomerNo.get(i).getSumWorkTime());
					sendInvoiceWorkTimeModel.setDeductionsAndOvertimePayOfUnitPrice(
							sameCustomerNo.get(i).getDeductionsAndOvertimePayOfUnitPrice());
					sendInvoiceWorkTimeModel.setWorkingTimeReport(sameCustomerNo.get(i).getWorkingTimeReport());

					sendInvoiceWorkTimeList.add(sendInvoiceWorkTimeModel);

				}
				sendInvoiceModelBySelectedEmployeeNo.setSendInvoiceWorkTimeModel(sendInvoiceWorkTimeList);
				returnList.add(sendInvoiceModelBySelectedEmployeeNo);
			}

		}

		for (int i = 0; i < returnList.size(); i++) {
			returnList.get(i).setRowNo(i + 1);
			File file = new File("C:\\file\\certificate\\" +getPDFUrl(returnList.get(i).getCustomerName(),dutyManagementModel.get("yearAndMonth")));
			if (file.exists()) {
				returnList.get(i).setHavePDF("true");
			} else {
				returnList.get(i).setHavePDF("false");
			}
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
				sendInvoiceWorkTimeModel.setWorkContents(sendInvoiceList.get(i).getSystemName() == null ? "技術支援"
						: sendInvoiceList.get(i).getSystemName());
				sendInvoiceWorkTimeModel.setOldWorkContents(sendInvoiceList.get(i).getSystemName() == null ? "技術支援"
						: sendInvoiceList.get(i).getSystemName());
				sendInvoiceWorkTimeModel.setUnitKey(sendInvoiceWorkTimeModel.getYearAndMonth()+'-'+sendInvoiceWorkTimeModel.getCustomerNo()+'-'+sendInvoiceWorkTimeModel.getEmployeeNo()+'-'+sendInvoiceWorkTimeModel.getWorkContents());
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
							String systemName = costRegistrationList.get(j).getCostClassificationName() + "("
									+ costRegistrationList.get(j).getOriginCode() + "-"
									+ costRegistrationList.get(j).getDestinationCode() + ")";
							sendInvoiceWorkTimeModel.setSystemName(systemName);
							sendInvoiceWorkTimeModel.setWorkContents(systemName);
							sendInvoiceWorkTimeModel.setOldWorkContents(systemName);

							break;
						default:
							String systemName2 = costRegistrationList.get(j).getCostClassificationName() + "("
									+ costRegistrationList.get(j).getDetailedNameOrLine() + ")";
							sendInvoiceWorkTimeModel.setSystemName(systemName2);
							sendInvoiceWorkTimeModel.setSystemName(systemName2);
							sendInvoiceWorkTimeModel.setWorkContents(systemName2);
							sendInvoiceWorkTimeModel.setOldWorkContents(systemName2);
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

			int showNo = 0;
			for (int i = 0; i < returnList.size(); i++) {
				returnList.get(i).setSystemNameFlag(0);
				returnList.get(i).setWorkTimeFlag(0);
				returnList.get(i).setEmployeeNameFlag(0);
				returnList.get(i).setRowNo(i + 1);
				if (i < 1 || returnList.get(i).getEmployeeNo() == null
						|| returnList.get(i).getEmployeeNo().equals("")) {
					showNo++;
					returnList.get(i).setShowNo(String.valueOf(showNo));
				} else {
					if (!returnList.get(i).getEmployeeNo().equals(returnList.get(i - 1).getEmployeeNo())) {
						showNo++;
						returnList.get(i).setShowNo(String.valueOf(showNo));
					} else {
						returnList.get(i).setShowNo("");
					}
				}
				// insertInvoiceData
				HashMap<String, String> model = new HashMap<String, String>();
				model.put("customerNo", returnList.get(i).getCustomerNo());
				model.put("yearAndMonth", dutyManagementModel.get("yearAndMonth"));
				model.put("workContents", returnList.get(i).getWorkContents() == null
						? (returnList.get(i).getSystemName() == null ? "技術支援" : returnList.get(i).getSystemName())
						: returnList.get(i).getWorkContents());
				model.put("customerName", returnList.get(i).getCustomerName());
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
				model.put("invoiceDate", dutyManagementModel.get("invoiceDate"));
				// model.put("invoiceDate", dateFormat.format(date));
				model.put("employeeNo", returnList.get(i).getEmployeeNo());
				model.put("invoiceNo", dutyManagementModel.get("invoiceNo"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				Date thisDate = new Date();
				try {
					thisDate = sdf.parse(dutyManagementModel.get("yearAndMonth"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar ca = Calendar.getInstance();
				ca.setTime(thisDate);
				ca.add(Calendar.MONTH, 1);
				ca.set(Calendar.DAY_OF_MONTH, 0);
				String lastDay = dateFormat.format(ca.getTime());
				lastDay = lastDay.substring(lastDay.length() - 2, lastDay.length());
				model.put("workPeriod",
						returnList.get(i).getWorkPeriod() == null
								? (dutyManagementModel.get("yearAndMonth") + "01~"
										+ dutyManagementModel.get("yearAndMonth") + lastDay)
								: returnList.get(i).getWorkPeriod());
				model.put("workingTime", returnList.get(i).getWorkingTime());
				model.put("requestUnitCode", returnList.get(i).getRequestUnitCode());
				model.put("unitPrice", returnList.get(i).getUnitPrice());
				model.put("systemNameFlag","0");
				model.put("workTimeFlag","0");
				model.put("employeeNameFlag","0");
				model.put("updateUser", getSession().getAttribute("employeeName").toString());
				sendInvoiceService.insertInvoiceData(model);
			}

			logger.info("SendInvoiceController.selectSendInvoiceByCustomerNo:" + "検索終了");
			return returnList;
		} else {
			logger.info("SendInvoiceController.selectSendInvoiceByCustomerNo:" + "検索終了");
			int showNo = 0;
			for (int i = 0; i < selectSendInvoiceByCustomerNoList.size(); i++) {
				selectSendInvoiceByCustomerNoList.get(i).setRowNo(i + 1);
				selectSendInvoiceByCustomerNoList.get(i).setUnitKey(selectSendInvoiceByCustomerNoList.get(i).getYearAndMonth()+'-'+selectSendInvoiceByCustomerNoList.get(i).getCustomerNo()+'-'+selectSendInvoiceByCustomerNoList.get(i).getEmployeeNo()+'-'+selectSendInvoiceByCustomerNoList.get(i).getWorkContents());
				if (i < 1 || selectSendInvoiceByCustomerNoList.get(i).getEmployeeNo() == null
						|| selectSendInvoiceByCustomerNoList.get(i).getEmployeeNo().equals("")) {
					showNo++;
					selectSendInvoiceByCustomerNoList.get(i).setShowNo(String.valueOf(showNo));
				} else {
					if (!selectSendInvoiceByCustomerNoList.get(i).getEmployeeNo()
							.equals(selectSendInvoiceByCustomerNoList.get(i - 1).getEmployeeNo())) {
						showNo++;
						selectSendInvoiceByCustomerNoList.get(i).setShowNo(String.valueOf(showNo));
					} else {
						selectSendInvoiceByCustomerNoList.get(i).setShowNo("");
					}
				}
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
	public Map<String, Object> updateInvoiceData(@RequestBody HashMap<String, String> model) {
		logger.info("SendInvoiceController.updateInvoiceData:" + "アップデート開始");
		model.put("updateUser", getSession().getAttribute("employeeName").toString());
		Map<String, Object> resulterr = new HashMap<>();

		if (model.get("oldWorkContents") == null || model.get("oldWorkContents").equals("")) {
			try {
				sendInvoiceService.insertNewInvoiceData(model);
				resulterr.put("code", 0);
			} catch (Exception e) {
				if (e instanceof DuplicateKeyException) {
					resulterr.put("code", 1000);
					resulterr.put("errorsMessage", "作業内容は重複した");
				} else {
					resulterr.put("code", 1000);
					resulterr.put("errorsMessage", "送信エラー発生しました。");
				}
			}
		} else {
			try {
				sendInvoiceService.updateInvoiceData(model);
				resulterr.put("code", 0);
			} catch (Exception e) {
				if (e instanceof DuplicateKeyException) {
					resulterr.put("code", 1000);
					resulterr.put("errorsMessage", "作業内容は重複した");
				} else {
					resulterr.put("code", 1000);
					resulterr.put("errorsMessage", "送信エラー発生しました。");
				}

			}
		}

		logger.info("SendInvoiceController.updateInvoiceData:" + "アップデート終了");
		return resulterr;
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
	 * すべて削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/deleteInvoiceDataAll", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteInvoiceDataAll(@RequestBody HashMap<String, String> model) {
		logger.info("SendInvoiceController.deleteInvoiceDataAll:" + "削除開始");
		boolean result = false;
		try {
			sendInvoiceService.deleteInvoiceDataAll(model);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		logger.info("SendInvoiceController.deleteInvoiceDataAll:" + "削除終了");
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

	/**
	 * PDF作成
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/downloadPDF", method = RequestMethod.POST)
	@ResponseBody
	public String downloadPDF(@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("SendInvoiceController.downloadPDF:" + "PDF作成開始");
		List<SendInvoiceWorkTimeModel> dataList = selectSendInvoiceByCustomerNo(dutyManagementModel);
		ArrayList<Map<String, Object>> newDataList = dataSelect(dataList, dutyManagementModel.get("taxRate"));
		DecimalFormat df = new DecimalFormat("#,###");

		File nowFile = new File(".").getAbsoluteFile();
		File inputFile = new File(nowFile.getParentFile(), "src/main/resources/PDFTemplate/invoicePDF.jrxml");
		File outputFile = new File(UtilsController.DOWNLOAD_PATH_BASE + "certificate/"+
				this.getPDFUrl(dutyManagementModel.get("customerName"),dutyManagementModel.get("yearAndMonth")));
		outputFile.getParentFile().mkdirs();
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();

			ArrayList<Map<String, ?>> tableData = new ArrayList<>();
			Map<String, Object> rowData = new Hashtable<>();
			for (int i = 0; i < newDataList.size(); i++) {
				rowData = newDataList.get(i);
				tableData.add(rowData);
			}
			JRDataSource ds = new JRBeanCollectionDataSource(tableData);
			parameters.put("dataTableResource", ds);
			String systemNameFlag = dutyManagementModel.get("systemNameFlag");
			parameters.put("systemNameFlag", systemNameFlag.equals("1") ? true : false);
			String workTimeFlag = dutyManagementModel.get("workTimeFlag");
			parameters.put("workTimeFlag", workTimeFlag.equals("1") ? true : false);
			String employeeNameFlag = dutyManagementModel.get("employeeNameFlag");
			parameters.put("employeeNameFlag", employeeNameFlag.equals("1") ? true : false);
			parameters.put("customerName", dataList.get(0).getCustomerName());
			parameters.put("invoiceDate",
					dataList.get(0).getInvoiceDate() == null || dataList.get(0).getInvoiceDate().equals("") ? ""
							: (dataList.get(0).getInvoiceDate().substring(0, 4) + "年"
									+ dataList.get(0).getInvoiceDate().substring(4, 6) + "月"
									+ dataList.get(0).getInvoiceDate().substring(6, 8) + "日"));
			parameters.put("deadLine",
					dataList.get(0).getDeadLine() == null || dataList.get(0).getDeadLine().equals("") ? ""
							: (dataList.get(0).getDeadLine().substring(0, 4) + "年"
									+ dataList.get(0).getDeadLine().substring(4, 6) + "月"
									+ dataList.get(0).getDeadLine().substring(6, 8) + "日"));
			parameters.put("invoiceNo", dutyManagementModel.get("invoiceNo"));
			parameters.put("remark", dataList.get(0).getRemark());
			parameters.put("subTotalAmount", df
					.format(Integer.parseInt((String) newDataList.get(newDataList.size() - 1).get("subTotalAmount"))));
			parameters.put("consumptionTax", dutyManagementModel.get("taxRate") + "%");
			parameters.put("consumptionTaxAmount", df.format(
					Integer.parseInt((String) newDataList.get(newDataList.size() - 1).get("consumptionTaxAmount"))));
			parameters.put("totalAmount", "￥"
					+ df.format(Integer.parseInt((String) newDataList.get(newDataList.size() - 1).get("totalAmount"))));

			AccountInfoModel accountInfoModel = sendInvoiceService.getAccountInfo(dataList.get(0).getBankCode());
			if (accountInfoModel == null) {
				parameters.put("bankInfo", "銀行情報存在しません。");
			} else {
				parameters.put("bankInfo",
						"振込先銀行　" + accountInfoModel.getBankNameString() + "　" + accountInfoModel.getBankBranchName()
								+ "支店\n普通預金　店番 　" + accountInfoModel.getBankBranchCode() + "\n口座番号 "
								+ accountInfoModel.getAccountNo() + "　口座名　" + accountInfoModel.getAccountName());
			}

			JasperReport report = JasperCompileManager.compileReport(inputFile.getAbsolutePath());
			JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());
		} catch (JRException e) {
			logger.error(e.getMessage());
		}
		logger.info("SendInvoiceController.downloadPDF:" + "PDF作成終了");
		return outputFile.getAbsolutePath();
	}

	private ArrayList<Map<String, Object>> dataSelect(List<SendInvoiceWorkTimeModel> dataList, String taxRate) {
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		int subTotalAmount = 0;
		int subTotalAmountNoTax = 0;
		DecimalFormat df = new DecimalFormat("#,###");
		for (int i = 0; i < dataList.size(); i++) {
			tempMap = new HashMap<String, Object>();
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			String day = "";
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = "31";
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = "30";
				break;
			default:
				day = "28";
				break;
			}
			String workPeriod = String.valueOf(year) + (month < 10 ? "0" + month : String.valueOf(month)) + "01~"
					+ String.valueOf(year) + (month < 10 ? "0" + month : String.valueOf(month)) + day;

			tempMap.put("rowNo", String.valueOf(dataList.get(i).getShowNo()));

			tempMap.put("workContents", dataList.get(i).getWorkContents());
			tempMap.put("employeeName", dataList.get(i).getEmployeeName());
			tempMap.put("workPeriod", dataList.get(i).getWorkPeriod());
			tempMap.put("sumWorkTime", dataList.get(i).getSumWorkTime());

			tempMap.put("requestUnit", dataList.get(i).getRequestUnitCode().equals("1") ? "件" : "人月");
			tempMap.put("quantity", dataList.get(i).getQuantity());
			tempMap.put("unitPrice", df.format(Integer
					.parseInt(dataList.get(i).getUnitPrice().equals("") ? "0" : dataList.get(i).getUnitPrice())));
			String deductionsAndOvertimePayOfUnitPrice = dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice();
			String deductionsAndOvertimePayOfUnitPrice1 = "";
			String deductionsAndOvertimePayOfUnitPrice2 = "";
			String payOffRange1 = dataList.get(i).getPayOffRange1();
			switch (payOffRange1) {
			case "0":
				payOffRange1 = "固定";
				break;
			case "1":
				payOffRange1 = "出勤日";
				break;
			default:
				deductionsAndOvertimePayOfUnitPrice1 = String
						.valueOf((Integer.parseInt(dataList.get(i).getUnitPrice()) + Integer.parseInt(dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice()))  / Integer.parseInt(payOffRange1));
				deductionsAndOvertimePayOfUnitPrice1 = deductionsAndOvertimePayOfUnitPrice1.substring(0,
						deductionsAndOvertimePayOfUnitPrice1.length() - 1) + "0";
				payOffRange1 += "H";
				break;
			}
			String payOffRange2 = dataList.get(i).getPayOffRange2();
			switch (payOffRange2) {
			case "0":
				payOffRange2 = "固定";
				break;
			case "1":
				payOffRange2 = "出勤日";
				break;
			default:
				deductionsAndOvertimePayOfUnitPrice2 = String
						.valueOf((Integer.parseInt(dataList.get(i).getUnitPrice()) + Integer.parseInt(dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice())) / Integer.parseInt(payOffRange2));
				deductionsAndOvertimePayOfUnitPrice2 = deductionsAndOvertimePayOfUnitPrice2.substring(0,
						deductionsAndOvertimePayOfUnitPrice2.length() - 1) + "0";
				payOffRange2 += "H";
				break;
			}
			/*
			 * tempMap.put("payOffRange1", deductionsAndOvertimePayOfUnitPrice == null ? ""
			 * : payOffRange1 + (Integer.parseInt(deductionsAndOvertimePayOfUnitPrice) >= 0
			 * ? "" : ("\n" + "￥" +
			 * df.format(Integer.parseInt(deductionsAndOvertimePayOfUnitPrice)) + "/H")));
			 */
			tempMap.put("payOffRange1", deductionsAndOvertimePayOfUnitPrice == null ? ""
					: payOffRange1 + (deductionsAndOvertimePayOfUnitPrice1.equals("") ? ""
							: ("\n" + "￥" + df.format(Integer.parseInt(deductionsAndOvertimePayOfUnitPrice1)) + "/H")));
			tempMap.put("payOffRange2", deductionsAndOvertimePayOfUnitPrice == null ? ""
					: payOffRange2 + (deductionsAndOvertimePayOfUnitPrice2.equals("") ? ""
							: ("\n" + "￥" + df.format(Integer.parseInt(deductionsAndOvertimePayOfUnitPrice2))) + "/H"));
			int sum = Integer.parseInt(dataList.get(i).getUnitPrice().equals("") ? "0" : dataList.get(i).getUnitPrice())
					* Integer.parseInt(dataList.get(i).getQuantity() == null ? "0" : dataList.get(i).getQuantity())
					+ Integer.parseInt(dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice() == null
							|| dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice().equals("") ? "0"
									: dataList.get(i).getDeductionsAndOvertimePayOfUnitPrice());
			tempMap.put("sum", df.format(sum) + (dataList.get(i).getRequestUnitCode().equals("0") ? "" : "(税込)"));
			if (dataList.get(i).getRequestUnitCode().equals("0"))
				subTotalAmount += sum;
			else
				subTotalAmountNoTax += sum;
			tempMap.put("subTotalAmount", String.valueOf(subTotalAmount + subTotalAmountNoTax));
			tempMap.put("consumptionTaxAmount",
					String.valueOf((int) (subTotalAmount * (Double.parseDouble(taxRate) / 100))));
			tempMap.put("totalAmount", String.valueOf((int) (subTotalAmount * (Double.parseDouble(taxRate) / 100))
					+ subTotalAmount + subTotalAmountNoTax));

			result.add(tempMap);
		}
		return result;
	}

	/**
	 * 送信
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/sendLetter", method = RequestMethod.POST)
	@ResponseBody
	public ResultModel sendLetter(@RequestBody HashMap<String, String> dutyManagementModel) {
		ResultModel resultModel = new ResultModel();// 戻す
		try {
			logger.info("SendInvoiceController.sendLetter:" + "送信開始");
			EmailModel emailModel = new EmailModel();
			// 受信人のメール
			emailModel.setMailTitle(dutyManagementModel.get("mailTitle"));
			emailModel.setMailConfirmContont(dutyManagementModel.get("mailConfirmContont"));
			emailModel.setMailFrom(dutyManagementModel.get("mailFrom"));
			if (dutyManagementModel.get("selectedMailCC") != null) {
				emailModel.setSelectedMailCC(dutyManagementModel.get("selectedMailCC").split(","));
			}
			emailModel.setSelectedmail(dutyManagementModel.get("mail").replaceAll(";", ","));
			emailModel.setUserName(getSession().getAttribute("employeeName").toString());
			emailModel.setPassword("Lyc2020-0908-");
			emailModel.setContextType("text/html;charset=utf-8");
			String file = this.getPDFUrl(dutyManagementModel.get("customerName"),dutyManagementModel.get("yearAndMonth"));
			String path = "c:/file/certificate/" + file+";;";

			String report = dutyManagementModel.get("reportFile");
			String[] reports = report.split(";;");
			for (int i = 0; i < reports.length; i++) {
				file += reports[i].split("/")[reports[i].split("/").length - 1] + ";;";
				path += reports[i].replaceAll("\\\\", "/") + ";;";
			}

			String[] names = file.split(";;");
			String[] paths = path.split(";;");
			emailModel.setNames(names);
			emailModel.setPaths(paths);
			// 送信
			ResultModel  sendMailResult= utils.sendMailWithFile(emailModel);
			if(sendMailResult.getResult()) {
				// データ更新
				sendInvoiceService.updateSendLetter(dutyManagementModel);
			}
			resultModel = sendMailResult;
		}catch(Exception e) {
			resultModel.setUnKnowErr();
			e.printStackTrace();
		}
		return resultModel; 
	}
	
	
	public String getPDFUrl(String customerName,String yearAndMonth) {
		return "【LYC】" +"【"+ customerName +"】"+"様へ請求書" + yearAndMonth+ ".pdf";
	}
}
