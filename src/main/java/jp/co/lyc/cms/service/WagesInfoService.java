package jp.co.lyc.cms.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jp.co.lyc.cms.mapper.EmployeeInfoMapper;
import jp.co.lyc.cms.mapper.ExpensesInfoMapper;
import jp.co.lyc.cms.mapper.WagesInfoMapper;
import jp.co.lyc.cms.model.WagesInfoModel;

@Component
public class WagesInfoService {

	@Autowired
	WagesInfoMapper wagesInfoMapper;

	@Autowired
	ExpensesInfoMapper expensesInfoMapper;

	@Autowired
	ExpensesInfoService expensesInfoService;

	@Autowired
	EmployeeInfoMapper employeeInfoMapper;

	/**
	 * 追加の場合
	 * 
	 * @param wagesInfoModel
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean insert(WagesInfoModel wagesInfoModel) {
		try {
			HashMap<String, String> sendMap = getSendMap(wagesInfoModel);
			wagesInfoMapper.insert(sendMap);
			Map<String, Object> employeeDetailMap = new HashMap<String, Object>();
			employeeDetailMap.put("employeeNo", wagesInfoModel.getEmployeeNo());
			employeeDetailMap.put("employeeFormCode", wagesInfoModel.getEmployeeStatus());
			employeeDetailMap.put("updateUser", wagesInfoModel.getUpdateUser());
			if (wagesInfoModel.getEmployeeStatus() != null && wagesInfoModel.getEmployeeStatus().equals("2")) {
				employeeDetailMap.put("socialInsuranceDate", wagesInfoModel.getReflectYearAndMonth() + "01");
			} else if (wagesInfoModel.getEmployeeStatus() != null) {
				employeeDetailMap.put("socialInsuranceDate", "");
			}
			employeeInfoMapper.updateEmployeeInfoDetail(employeeDetailMap);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新の場合
	 * 
	 * @param wagesInfoModel
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean update(WagesInfoModel wagesInfoModel) {
		try {
			HashMap<String, String> sendMap = getSendMap(wagesInfoModel);
			wagesInfoMapper.update(sendMap);
			Map<String, Object> employeeDetailMap = new HashMap<String, Object>();
			employeeDetailMap.put("employeeNo", wagesInfoModel.getEmployeeNo());
			employeeDetailMap.put("employeeFormCode", wagesInfoModel.getEmployeeStatus());
			employeeDetailMap.put("updateUser", wagesInfoModel.getUpdateUser());
			if (wagesInfoModel.getEmployeeStatus() != null && wagesInfoModel.getEmployeeStatus().equals("2")) {
				employeeDetailMap.put("socialInsuranceDate", wagesInfoModel.getReflectYearAndMonth() + "01");
			} else if (wagesInfoModel.getEmployeeStatus() != null) {
				employeeDetailMap.put("socialInsuranceDate", "");
			}
			employeeInfoMapper.updateEmployeeInfoDetail(employeeDetailMap);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 給料情報削除
	 * 
	 * @param customerNo
	 */

	public boolean delete(WagesInfoModel wagesInfoModel) {
		boolean result = true;
		try {
			wagesInfoMapper.delete(getSendMap(wagesInfoModel));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * 社員区分変更 データ更新
	 * 
	 * @param customerNo
	 */
	public void updateEmpInfo(String employeeNo, String newEmployeeNo) {
		wagesInfoMapper.updateEmployeeInfo("T001Employee", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T003AddressInfo", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T005WagesInfo", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T006EmployeeSiteInfo", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T010SalesSituation", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T013ExpensesInfo", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T015ResumeManagement", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T016EmployeeWorkTime", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T017BreakTime", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T018WorkTotalTime", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T019SalesSentence", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T020CostInfo", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateEmployeeInfo("T025WorkTotalTimeSupplement", employeeNo, newEmployeeNo);

		if (newEmployeeNo.substring(0, 2).equals("SP"))
			wagesInfoMapper.updateT002EmployeeDetail(employeeNo, newEmployeeNo);
		else
			wagesInfoMapper.updateEmployeeInfo("T002EmployeeDetail", employeeNo, newEmployeeNo);
		wagesInfoMapper.updateT004AccountInfo(employeeNo, newEmployeeNo);

		String bpRemark = null;
		if (employeeNo.startsWith("BP")) {
			// 获取当前年月日时
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
			String currentDate = LocalDateTime.now().format(formatter);
			
			// bpRemark的内容是 当前年月日 emp.getEmployeeNo()からemp.getNewEmployeeNo()に変更。
			bpRemark = currentDate + " " + employeeNo + "から" + newEmployeeNo + "に変更。";
		}
		wagesInfoMapper.updateBpInfoNo(employeeNo, newEmployeeNo, bpRemark);

	}

	/**
	 * 給料情報sendMapの作成
	 * 
	 * @param wagesInfoModel
	 * @return
	 */
	public HashMap<String, String> getSendMap(WagesInfoModel wagesInfoModel) {
		HashMap<String, String> sendMap = new HashMap<String, String>();
		sendMap.put("employeeNo", wagesInfoModel.getEmployeeNo());
		sendMap.put("reflectYearAndMonth", wagesInfoModel.getReflectYearAndMonth());
		sendMap.put("updatedReflectYearAndMonth", wagesInfoModel.getUpdatedReflectYearAndMonth());
		sendMap.put("socialInsuranceFlag", wagesInfoModel.getSocialInsuranceFlag());
		sendMap.put("salary", wagesInfoModel.getSalary());
		sendMap.put("waitingCost", wagesInfoModel.getWaitingCost());
		sendMap.put("welfarePensionAmount", wagesInfoModel.getWelfarePensionAmount());
		sendMap.put("healthInsuranceAmount", wagesInfoModel.getHealthInsuranceAmount());
		sendMap.put("insuranceFeeAmount", wagesInfoModel.getInsuranceFeeAmount());
		sendMap.put("lastTimeBonusAmount", wagesInfoModel.getLastTimeBonusAmount());
		sendMap.put("scheduleOfBonusAmount", wagesInfoModel.getScheduleOfBonusAmount());
		sendMap.put("bonusFlag", wagesInfoModel.getBonusFlag());
		sendMap.put("bonusNo", wagesInfoModel.getBonusNo());
		sendMap.put("nextBonusMonth", wagesInfoModel.getNextBonusMonth());
		sendMap.put("monthOfCompanyPay", wagesInfoModel.getMonthOfCompanyPay());
		sendMap.put("nextRaiseMonth", wagesInfoModel.getNextRaiseMonth());
		sendMap.put("totalAmount", wagesInfoModel.getTotalAmount());
		sendMap.put("employeeStatus", wagesInfoModel.getEmployeeStatus());
		sendMap.put("remark", wagesInfoModel.getRemark());
		sendMap.put("updateUser", wagesInfoModel.getUpdateUser());
		sendMap.put("workingConditionStatus", wagesInfoModel.getWorkingCondition());
		sendMap.put("fristTimeBonusAmount", wagesInfoModel.getFristTimeBonusAmount());
		sendMap.put("secondTimeBonusAmount", wagesInfoModel.getSecondTimeBonusAmount());
		sendMap.put("fristBonusMonth", wagesInfoModel.getFristBonusMonth());
		sendMap.put("secondBonusMonth", wagesInfoModel.getSecondBonusMonth());
		return sendMap;
	}
}
