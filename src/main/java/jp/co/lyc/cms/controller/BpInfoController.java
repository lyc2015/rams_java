package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
