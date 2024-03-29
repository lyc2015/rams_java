package jp.co.lyc.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jp.co.lyc.cms.mapper.AccountInfoMapper;
import jp.co.lyc.cms.mapper.BpInfoMapper;
import jp.co.lyc.cms.mapper.EmployeeInfoMapper;
import jp.co.lyc.cms.mapper.SiteInfoMapper;
import jp.co.lyc.cms.model.AccountInfoModel;
import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.model.EmployeeInfoCsvModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.SalesContent;
import jp.co.lyc.cms.util.UtilsController;

@Component
public class EmployeeInfoService {

	@Autowired
	EmployeeInfoMapper employeeInfoMapper;

	@Autowired
	AccountInfoMapper accountInfoMapper;

	@Autowired
	BpInfoMapper bpInfoMapper;

	@Autowired
	SiteInfoMapper siteInfoMapper;
	
	@Autowired
	SalesSituationService salesSituationService;

	/**
	 * ログイン
	 * 
	 * @param sendMap
	 * @return
	 */

	public EmployeeModel getEmployeeModel(Map<String, String> sendMap) {
		// TODO Auto-generated method stub
		EmployeeModel employeeModel = employeeInfoMapper.getEmployeeModel(sendMap);
		return employeeModel;
	}

	/**
	 * 社員情報を取得
	 * 
	 * @param sendMap
	 * @return List
	 */
	public List<EmployeeModel> getEmployeeInfo(Map<String, Object> sendMap) {
		List<EmployeeModel> employeeListTemp = employeeInfoMapper.getEmployeesInfo(sendMap);
		List<EmployeeModel> employeeList = new ArrayList<EmployeeModel>();

		if (sendMap.get("kadou") != null) {
			List<String> employeeAdmissionList = employeeInfoMapper.getEmployeeWithAdmission();

			if (sendMap.get("kadou").equals("0")) {
				for (int i = 0; i < employeeListTemp.size(); i++) {
					for (int j = 0; j < employeeAdmissionList.size(); j++) {
						if (employeeListTemp.get(i).getEmployeeNo().equals(employeeAdmissionList.get(j))) {
							employeeList.add(employeeListTemp.get(i));
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < employeeListTemp.size(); i++) {
					for (int j = 0; j < employeeAdmissionList.size(); j++) {
						if (employeeListTemp.get(i).getEmployeeNo().equals(employeeAdmissionList.get(j))) {
							employeeListTemp.remove(i);
							i--;
							break;
						}
					}
				}
				employeeList = employeeListTemp;
			}
		} else {
			employeeList = employeeListTemp;
		}

		if (sendMap.get("employeeStatus") != null && sendMap.get("employeeStatus").equals("1")) {
			List<EmployeeModel> bpfromList = employeeInfoMapper.getBpfrom();
			for (int i = 0; i < employeeList.size(); i++) {
				for (int j = 0; j < bpfromList.size(); j++) {
					if (employeeList.get(i).getEmployeeNo().equals(bpfromList.get(j).getEmployeeNo())) {
						employeeList.get(i).setBpfrom(bpfromList.get(j).getBpfrom());
					}
				}
			}
		}

		if (sendMap.get("customer") != null && sendMap.get("customer").equals(""))
			sendMap.put("customer", null);

		if (sendMap.get("customer") != null) {
			List<EmployeeModel> customerNoList = employeeInfoMapper.getcustomerNo();
			for (int i = 0; i < employeeList.size(); i++) {
				boolean deleteFlag = true;
				for (int j = 0; j < customerNoList.size(); j++) {
					if (employeeList.get(i).getEmployeeNo().equals(customerNoList.get(j).getEmployeeNo())) {
						if (sendMap.get("customer").toString().equals(customerNoList.get(j).getCustomerNo())) {
							deleteFlag = false;
						}
					}
				}
				if (deleteFlag) {
					employeeList.remove(i);
					i--;
				}
			}
		}

		if (sendMap.get("developLanguage1") != null && sendMap.get("developLanguage1").equals(""))
			sendMap.put("developLanguage1", null);
		if (sendMap.get("developLanguage2") != null && sendMap.get("developLanguage2").equals(""))
			sendMap.put("developLanguage2", null);

		if (sendMap.get("developLanguage1") != null || sendMap.get("developLanguage2") != null) {
			List<EmployeeModel> employeeDevelopLanguageList = employeeInfoMapper.getEmployeesDevelopLanguage();
			for (int i = 0; i < employeeList.size(); i++) {
				for (int j = 0; j < employeeDevelopLanguageList.size(); j++) {
					if (employeeList.get(i).getEmployeeNo()
							.equals(employeeDevelopLanguageList.get(j).getEmployeeNo())) {
						if (employeeDevelopLanguageList.get(j).getDevelopLanguage1() != null
								&& employeeDevelopLanguageList.get(j).getDevelopLanguage2() != null
								&& employeeDevelopLanguageList.get(j).getDevelopLanguage3() != null
								&& employeeDevelopLanguageList.get(j).getDevelopLanguage4() != null
								&& employeeDevelopLanguageList.get(j).getDevelopLanguage5() != null) {
							if (sendMap.get("developLanguage1") != null && sendMap.get("developLanguage2") != null) {
								if ((sendMap.get("developLanguage1")
										.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage1())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage2())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage3())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage4())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage5()))
										&& (sendMap.get("developLanguage2")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage1())
												|| sendMap.get("developLanguage2").equals(
														employeeDevelopLanguageList.get(j).getDevelopLanguage2())
												|| sendMap.get("developLanguage2").equals(
														employeeDevelopLanguageList.get(j).getDevelopLanguage3())
												|| sendMap.get("developLanguage2").equals(
														employeeDevelopLanguageList.get(j).getDevelopLanguage4())
												|| sendMap.get("developLanguage2").equals(
														employeeDevelopLanguageList.get(j).getDevelopLanguage5()))) {
									break;
								}
							} else if (sendMap.get("developLanguage1") != null) {
								if (sendMap.get("developLanguage1")
										.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage1())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage2())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage3())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage4())
										|| sendMap.get("developLanguage1")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage5())) {
									break;
								}
							} else if (sendMap.get("developLanguage2") != null) {
								if (sendMap.get("developLanguage2")
										.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage1())
										|| sendMap.get("developLanguage2")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage2())
										|| sendMap.get("developLanguage2")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage3())
										|| sendMap.get("developLanguage2")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage4())
										|| sendMap.get("developLanguage2")
												.equals(employeeDevelopLanguageList.get(j).getDevelopLanguage5())) {
									break;
								}
							}
							employeeList.remove(i);
							if (employeeList.size() > 0)
								i = 0;
							else
								break;
						}
					}
				}
			}
		}

		List<EmployeeModel> admissionStartDateList = employeeInfoMapper.getAdmissionStartDate();
		for (int i = 0; i < employeeList.size(); i++) {
			for (int j = 0; j < admissionStartDateList.size(); j++) {
				if (employeeList.get(i).getEmployeeNo().equals(admissionStartDateList.get(j).getEmployeeNo())) {
					employeeList.get(i).setAdmissionTime(
							UtilsController.dateAddOblique(admissionStartDateList.get(j).getAdmissionTime()));
				}
			}
		}
		return employeeList;
	}

	/**
	 * 社員情報を確認
	 * 
	 * @param sendMap
	 * @return List
	 */
	public List<String> verificationEmployeeInfo() {
		List<String> employeeList = employeeInfoMapper.verificationEmployeeInfo();
		return employeeList;
	}

	/**
	 * 社員情報を追加
	 * 
	 * @param sendMap
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean insertEmployee(HashMap<String, Object> sendMap) {
		boolean result = true;
		try {
			employeeInfoMapper.insertEmployeeInfo(sendMap);
			employeeInfoMapper.insertEmployeeInfoDetail(sendMap);
			employeeInfoMapper.insertAddressInfo(sendMap);
			if (sendMap.get("bpInfoModel") != null) {// BP情報
				bpInfoMapper.insertBp(getParamBpModel(sendMap));
			}
			if (sendMap.get("bankInfoModel") != null) {// 口座情報
				accountInfoMapper.insertAccount(getParamBankInfoModel(sendMap));
			}
			employeeInfoMapper.insertResumeManagement(sendMap);// 履歴書を追加
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * 社員情報を削除
	 * 
	 * @param emp
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteEmployeeInfo(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			employeeInfoMapper.deleteEmployeeInfo(sendMap);
			employeeInfoMapper.deleteEmployeeInfoDetail(sendMap);
			employeeInfoMapper.deleteEmployeeSalesSituation(sendMap);
			employeeInfoMapper.deleteBpInfoSupplement(sendMap);
			siteInfoMapper.deleteEmployeeSiteInfo(sendMap);
			employeeInfoMapper.deleteAddressInfo(sendMap);
			bpInfoMapper.deleteBpInfo(sendMap);
			employeeInfoMapper.deleteResumeManagement(sendMap);
			employeeInfoMapper.deleteSalesSentence(sendMap);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * EmployeeNoによると、社員情報を取得
	 * 
	 * @param emp
	 * @return EmployeeModel
	 */
	public EmployeeModel getEmployeeByEmployeeNo(Map<String, Object> sendMap) {
		EmployeeModel model = employeeInfoMapper.getEmployeeByEmployeeNo(sendMap);
		if (null != model && model.getAlphabetName() != null && !(model.getAlphabetName().equals(" "))) {
			model.setAlphabetName1(model.getAlphabetName().split(" ")[0]);
			if (model.getAlphabetName().split(" ").length > 1) {
				model.setAlphabetName2(model.getAlphabetName().split(" ")[1]);
			}
			if (model.getAlphabetName().split(" ").length > 2) {
				model.setAlphabetName3(model.getAlphabetName().split(" ")[2]);
			}
		}
		return model;
	}

	/**
	 * 社員情報を修正
	 * 
	 * @param emp
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEmployee(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			employeeInfoMapper.updateEmployeeInfo(sendMap);
			employeeInfoMapper.updateEmployeeInfoDetail(sendMap);
			employeeInfoMapper.updateAddressInfo(sendMap);
			//個人情報の更新時にビジネス記事の駅情報を同期 lxf-20230412
			SalesContent model=new SalesContent();
			model.setEmployeeNo((String)sendMap.get("employeeNo"));
			model.setStationCode((String)sendMap.get("stationCode"));
			salesSituationService.updateSalesSentenceByemployeeNo(model);
			
			if (sendMap.get("bankInfoModel") != null) {// 口座情報
				accountInfoMapper.replaceAccount(getParamBankInfoModel(sendMap));
			}
			if (sendMap.get("bpInfoModel") != null) {
				int row = bpInfoMapper.updateBp(getParamBpModel(sendMap));
				if (row == 0) {
					bpInfoMapper.insertBp(getParamBpModel(sendMap));
				}
			}
			employeeInfoMapper.updateResumeManagement(sendMap);// 履歴書を追加
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * パスポートを修正
	 * 
	 * @param emp
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updatePassword(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			employeeInfoMapper.updateEmployeeInfo(sendMap);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	// 口座情報のパラメータをセットします。
	public HashMap<String, String> getParamBankInfoModel(Map<String, Object> sendMap) {
		HashMap<String, String> bankInfoModelSendMap = new HashMap<String, String>();
		AccountInfoModel accountInfoModel = (AccountInfoModel) sendMap.get("bankInfoModel");
		String employeeNo = (String) sendMap.get("employeeNo");
		if (employeeNo.substring(0, 3).equals("LYC")) {
			bankInfoModelSendMap.put("accountBelongsStatus", "0");
		} else {
			bankInfoModelSendMap.put("accountBelongsStatus", "1");
		}
		bankInfoModelSendMap.put("bankCode", accountInfoModel.getBankCode());
		bankInfoModelSendMap.put("accountName", accountInfoModel.getAccountName());
		bankInfoModelSendMap.put("accountNo", accountInfoModel.getAccountNo());
		bankInfoModelSendMap.put("bankBranchCode", accountInfoModel.getBankBranchCode());
		bankInfoModelSendMap.put("accountTypeStatus", accountInfoModel.getAccountTypeStatus());
		bankInfoModelSendMap.put("updateUser", sendMap.get("updateUser").toString());
		bankInfoModelSendMap.put("employeeOrCustomerNo", sendMap.get("employeeNo").toString());
		return bankInfoModelSendMap;
	}

	private Map<String, Object> getParamBpModel(Map<String, Object> sendMap) {
		Map<String, Object> pbModelSendMap = new HashMap<String, Object>();
		BpInfoModel pbModel = (BpInfoModel) sendMap.get("bpInfoModel");
		pbModelSendMap.put("bpEmployeeNo", pbModel.getBpEmployeeNo());
		pbModelSendMap.put("bpBelongCustomerCode", pbModel.getBpBelongCustomerCode());
		pbModelSendMap.put("bpUnitPrice", pbModel.getBpUnitPrice());
		pbModelSendMap.put("bpSalesProgressCode", pbModel.getBpSalesProgressCode());
		pbModelSendMap.put("bpRemark", pbModel.getBpRemark());
		pbModelSendMap.put("bpOtherCompanyAdmissionEndDate", pbModel.getBpOtherCompanyAdmissionEndDate());
		pbModelSendMap.put("updateUser", sendMap.get("updateUser").toString());
		return pbModelSendMap;
	}

	/**
	 * ログイン認証番号の電話番号存在チェック
	 * 
	 * @param employeeNo
	 * @return
	 */

	public String getEmployeePhoneNo(String employeeNo) {
		return employeeInfoMapper.getEmployeePhoneNo(employeeNo);
	}

	public List<EmployeeInfoCsvModel> getEmployeesCSV(List<String> employeeNo) {
		return employeeInfoMapper.getEmployeesCSV(employeeNo);
	}

	/**
	 * BP情報更新
	 * 
	 * @param sendMap
	 */
	public void updatebpInfo(BpInfoModel bpInfoModel) {
		employeeInfoMapper.updatebpInfo(bpInfoModel);
		if (bpInfoModel.getOldUnitPriceStartMonth() != null
				&& !bpInfoModel.getOldUnitPriceStartMonth().equals(bpInfoModel.getUnitPriceStartMonth())) {
			employeeInfoMapper.deletebpInfo(bpInfoModel.getBpEmployeeNo(), bpInfoModel.getOldUnitPriceStartMonth());
		}
		employeeInfoMapper.updatebpInfoAll(bpInfoModel);
	}

	/**
	 * BP情報削除
	 * 
	 * @param sendMap
	 */
	public void deletebpInfo(BpInfoModel bpInfoModel) {
		employeeInfoMapper.deletebpInfo(bpInfoModel.getBpEmployeeNo(), bpInfoModel.getOldUnitPriceStartMonth());
	}
}