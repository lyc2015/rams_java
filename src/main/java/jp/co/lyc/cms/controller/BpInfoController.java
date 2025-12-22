package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.lyc.cms.model.AccountInfoModel;
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
        bpInfoList = bpInfoService.getBpInfoList(sendMap);

        Map<String, BpInfoModel> resMap = new HashMap<>();
        Map<String, String> companyNames = new HashMap<>();

        for(BpInfoModel each:bpInfoList) {
            if(resMap.containsKey(each.getBpBelongCustomerCode())){
                BpInfoModel cu = resMap.get(each.getBpBelongCustomerCode());
                String averUnitPrice = cu.getAverUnitPrice();
                int sum1 = Integer.parseInt(averUnitPrice) + Integer.parseInt(each.getAverUnitPrice());
                sum1 = (int) (sum1 / 10000);
                resMap.get(each.getBpBelongCustomerCode()).setAverUnitPrice(sum1+"");
                String bpUnitPrice = cu.getBpUnitPrice();
                int sum2 = Integer.parseInt(bpUnitPrice) + Integer.parseInt(each.getBpUnitPrice());
                resMap.get(each.getBpBelongCustomerCode()).setTotalUnitPrice(sum2+"");
                cu.setCountPeo(cu.getCountPeo()+1);
                String tempName = resMap.get(each.getBpBelongCustomerCode()).getEmployeeName();
                resMap.get(each.getBpBelongCustomerCode()).setEmployeeName(tempName+","+each.getEmployeeName());
            }else{
                resMap.put(each.getBpBelongCustomerCode(),each);
                companyNames.put(each.getBpBelongCustomerCode(),each.getEmployeeName());
            }
        }
        for (int i = 0; i < bpInfoList.size(); i++) {
            bpInfoList.get(i).setRowNo(String.valueOf(i + 1));
        }
        resultMap.put("bpInfoList", bpInfoList);
        logger.info("BpInfoController.getBpInfo:" + "検索結束");
        return resultMap;
    }


}
