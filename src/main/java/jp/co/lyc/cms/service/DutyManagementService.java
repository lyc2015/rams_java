package jp.co.lyc.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.DutyManagementModel;
import jp.co.lyc.cms.mapper.DutyManagementMapper;

@Component
public class DutyManagementService {

	@Autowired
	DutyManagementMapper dutyManagementMapper;

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<DutyManagementModel> selectDutyManagement(HashMap<String, String> sendMap) {
		//List<DutyManagementModel> resultMod = dutyManagementMapper.selectDutyManagement(sendMap);
        List<DutyManagementModel> resultMod = new ArrayList<>();

        for(int i=0;i<30;i++) {
            DutyManagementModel mod = new DutyManagementModel();
            mod.setEmployeeNo("LYC005");
            mod.setEmployeeName("11111");
            mod.setCustomerName("customername");
            mod.setCost("100");
            mod.setStationName("东京");
            mod.setCheckSection("东京1");
            mod.setBpBelongCustomerAbbreviation("abbr");
            mod.setDeductionsAndOvertimePay("200");
            mod.setPayOffRange("300");
            mod.setDeductionsAndOvertimePay("500");
            mod.setUpdateTime("2025-12-16 09:18:56");
            mod.setWorkTime("2025-12-23 09:18:56");
            resultMod.add(mod);
        }

		return resultMod;
	}

	/**
	 * 費用詳細情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<CostRegistrationModel> selectCostRegistration(HashMap<String, String> sendMap) {
		List<CostRegistrationModel> resultMod = dutyManagementMapper.selectCostRegistration(sendMap);
		return resultMod;
	}

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */

	public boolean updateDutyManagement(HashMap<String, String> sendMap) {
		boolean result = true;
		try {
			dutyManagementMapper.updateDutyManagement(sendMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	public List<DutyManagementModel> selectWorkTime(HashMap<String, String> dutyManagementModel) {
		return dutyManagementMapper.selectWorkTime(dutyManagementModel);
	}

	public String getFirstName(String employeeNo) {
		return dutyManagementMapper.getFirstName(employeeNo);
	}
}
