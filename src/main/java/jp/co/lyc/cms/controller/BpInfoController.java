package jp.co.lyc.cms.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.co.lyc.cms.model.AccountInfoModel;
import org.jfree.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.service.BpInfoService;

@Controller
@RequestMapping(value = "/bpInfo")
public class BpInfoController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	BpInfoService bpInfoService;

	/**
	 * データを取得
	 * 
	 * @param bpInfoModel
	 * @return BpInfoModel
	 */

	@RequestMapping(value = "/getBpInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getBpInfo(@RequestParam(value = "bpEmployeeNo", required = false) String bpEmployeeNo) {
		logger.info("BpInfoController.getBpInfo:" + "検索開始");
		Map<String, Object> resultMap = new HashMap<>();// 戻す
		Map<String, Object> sendMap = new HashMap<String, Object>();

		sendMap.put("bpEmployeeNo", bpEmployeeNo);
		BpInfoModel model = new BpInfoModel();
		model = bpInfoService.getBpInfo(sendMap);
		resultMap.put("model", model);
		List<BpInfoModel> bpInfoList = new ArrayList<BpInfoModel>();
		bpInfoList = bpInfoService.getBpInfoList(sendMap);
		for (int i = 0; i < bpInfoList.size(); i++) {
			bpInfoList.get(i).setRowNo(String.valueOf(i + 1));
		}
		resultMap.put("bpInfoList", bpInfoList);
		logger.info("BpInfoController.getBpInfo:" + "検索結束");
		return resultMap;
	}

	/**
	 * 条件を取得
	 * 
	 * @param emp
	 * @return
	 */
	public Map<String, Object> getParam(BpInfoModel bpInfoModel) {
		Map<String, Object> sendMap = new HashMap<String, Object>();
		String bpEmployeeNo = bpInfoModel.getBpEmployeeNo();// 社員番号
		if (bpEmployeeNo != null && bpEmployeeNo.length() != 0) {
			sendMap.put("bpEmployeeNo", bpEmployeeNo);
		}
		return sendMap;
	}


    @RequestMapping(value = "/getPartnerBpInfo", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPartnerBpInfo(@RequestBody BpInfoModel bpInfoModel) {
        logger.info("BpInfoController.getPartnerBpInfo:" + "検索開始");
        Map<String, Object> resultMap = new HashMap<>();// 戻す
        List<BpInfoModel> bpInfoList = new ArrayList<BpInfoModel>();
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("givenDateTime",bpInfoModel.getUnitPriceStartMonth());
        sendMap.put("customerCode",bpInfoModel.getCustomerCode());
        bpInfoList = bpInfoService.getAllBpInfoList(sendMap);
        Map<String, BpInfoModel> resMap = new HashMap<>();
        Map<String, String> companyNames = new HashMap<>();

        for(BpInfoModel each: bpInfoList ) {
            String start = each.getAdmissionStartDate();
            String end = each.getAdmissionEndDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date1 = LocalDate.parse(end, formatter);
            LocalDate date2 = LocalDate.parse(start, formatter);
            long monthsBetween = ChronoUnit.MONTHS.between(
                    date2.withDayOfMonth(1),
                    date1.withDayOfMonth(1)
            );
            each.setManMonths((int)monthsBetween);
        }

        Map<String, List<BpInfoModel>> groupedMap =
                bpInfoList.stream()
                        .collect(Collectors.groupingBy(
                                BpInfoModel::getBpBelongCustomerCode
                        ));
        List<BpInfoModel> resList = new ArrayList<BpInfoModel>();
        for (Map.Entry<String, List<BpInfoModel>> entry : groupedMap.entrySet()) {
            String key = entry.getKey();
            List<BpInfoModel> value = entry.getValue();
            BpInfoModel model = value.get(0);
            String employeeName = "";
            for(int i=0;i<value.size();i++){
                int sum = i+1;
                BpInfoModel eachModel = value.get(i);
                model.setCountPeo(sum);
                String item = sum+"."+"("+eachModel.getEmployeeName()+","+eachModel.getAdminCustomerAbb()+","+eachModel.getUnitPriceStartMonth()+",<span>"+eachModel.getBpUnitPrice()+"</span>)";


                if(employeeName.isEmpty()){
                    employeeName = item;
                }else{
                    employeeName += ", "+item;
                }
            }
            model.setEmployeeStr(employeeName);
            int averUnitPrice = value.stream()
                    .mapToInt(bp -> {
                        try {
                            return Integer.parseInt(bp.getAverUnitPrice());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .sum();
            model.setAverUnitPrice(averUnitPrice+"");
            int totalUnitPrice = value.stream()
                    .mapToInt(bp -> {
                        try {
                            return Integer.parseInt(bp.getTotalUnitPrice());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .sum();
            model.setTotalUnitPrice(totalUnitPrice+"");

            int manMonths = value.stream()
                    .mapToInt(bp -> {
                        try {
                            return bp.getManMonths();
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .sum();
            model.setManMonths(manMonths);

            resList.add(model);
        }
        for (int i = 0; i < resList.size(); i++) {
            resList.get(i).setRowNo(String.valueOf(i + 1));
        }
        resultMap.put("bpInfoList", resList);
        resultMap.put("allbpInfoList", bpInfoList);

        Map<String, Object> sendMap1 = new HashMap<>();
        List<BpInfoModel> bpInfoCusList = bpInfoService.getAllBpInfoList(sendMap1);
        resultMap.put("bpInfoCusList", bpInfoCusList);

        logger.info("BpInfoController.getPartnerBpInfo:" + "検索結束");
        return resultMap;
    }


}
