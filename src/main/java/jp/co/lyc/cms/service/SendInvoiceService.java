package jp.co.lyc.cms.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.DutyManagementModel;
import jp.co.lyc.cms.mapper.SendInvoiceMapper;

@Component
public class SendInvoiceService {

	@Autowired
	SendInvoiceMapper sendInvoiceMapper;

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<DutyManagementModel> selectDutyManagement(HashMap<String, String> sendMap) {
		List<DutyManagementModel> resultMod = sendInvoiceMapper.selectDutyManagement(sendMap);
		return resultMod;
	}

	/**
	 * 費用詳細情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<CostRegistrationModel> selectCostRegistration(HashMap<String, String> sendMap) {
		List<CostRegistrationModel> resultMod = sendInvoiceMapper.selectCostRegistration(sendMap);
		return resultMod;
	}

	public List<DutyManagementModel> selectWorkTime(HashMap<String, String> dutyManagementModel) {
		return sendInvoiceMapper.selectWorkTime(dutyManagementModel);
	}
}
