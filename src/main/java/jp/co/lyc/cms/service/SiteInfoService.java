package jp.co.lyc.cms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jp.co.lyc.cms.controller.SiteInfoController;
import jp.co.lyc.cms.mapper.SiteInfoMapper;
import jp.co.lyc.cms.model.SalesSituationModel;
import jp.co.lyc.cms.model.SiteModel;
import jp.co.lyc.cms.util.UtilsController;

@Component
public class SiteInfoService {

	@Autowired
	SiteInfoMapper siteInfoMapper;
	@Autowired
	SalesSituationService salesSituationService;

	@Transactional(rollbackFor = Exception.class)
	public boolean insertSiteInfo(Map<String, Object> sendMap) {
		try {
			siteInfoMapper.siteInsert(sendMap);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateSiteInfo(Map<String, Object> sendMap) {
		try {
			if (sendMap.get("workState").equals("2")) {
				List<SiteModel> lastData = siteInfoMapper.getSiteInfo((String) sendMap.get("employeeNo"));
				SiteModel lastOne = lastData.get(lastData.size() - 1);
				String levelCode = (String) sendMap.get("levelCode");
				String admissionEndDate = (String) sendMap.get("admissionEndDate");
				lastOne.setRemark((String) sendMap.get("remark"));
				lastOne.setAdmissionEndDate(admissionEndDate);
				lastOne.setAdmissionStartDate((String) sendMap.get("admissionStartDate"));
				lastOne.setWorkDate((String) sendMap.get("admissionStartDate"));
				lastOne.setEmployeeNo((String) sendMap.get("employeeNo"));
				int year = Integer.parseInt(admissionEndDate.substring(0, 4));
				int month = Integer.parseInt(admissionEndDate.substring(4, 6));
				month += 1;
				if (month > 12) {
					month -= 12;
					year += 1;
				}
				admissionEndDate = year + "" + (month > 9 ? month : "0" + month) + "01";
				sendMap.replace("remark", "");
				sendMap.replace("levelCode", "");
				sendMap.replace("workState", "0");
				sendMap.replace("admissionStartDate", admissionEndDate);
				sendMap.replace("holidayStartDate", "");
				sendMap.replace("admissionEndDate", null);
				siteInfoMapper.siteInsert(sendMap);
				lastOne.setLevelCode(levelCode);
				lastOne.setWorkState("2");
				lastOne.setScheduledEndDate("");
				lastOne.setUpdateUser((String) sendMap.get("updateUser"));
				lastOne.setTypteOfContractCode((String) sendMap.get("typteOfContractCode"));
				Map<String, Object> sendMapForUp = formateData(lastOne);
				siteInfoMapper.siteUpdate(sendMapForUp);
				
				//lxf 单金调整后t010插入一条数据
				List<SalesSituationModel> salesSituationList = salesSituationService.getT010SalesSituationLatestByemployeeNo((String)sendMap.get("employeeNo"));
				if(salesSituationList!=null  && salesSituationList.size()>0) {
					SalesSituationModel temp=salesSituationList.get(0);
					temp.setAdmissionEndDate(null);
					temp.setAdmissionStartDate(admissionEndDate.substring(0,6));
					temp.setSalesYearAndMonth(admissionEndDate.substring(0,6));
					temp.setSalesDateUpdate(admissionEndDate.substring(0,6));
					temp.setRemark("ignore");
					temp.setUpdateUser((String) sendMap.get("updateUser"));
					temp.setSalesProgressCode(null);
					temp.setPrice(sendMap.get("unitPrice").toString());
					salesSituationService.insertSalesSituation(temp);
					//更新用户信息
					temp.setCustomer(temp.getConfirmCustomer());
					salesSituationService.updateDataStatus(temp);
				}

			} else {
				if (sendMap.get("workState").equals("3")) {
					List<SiteModel> lastData = siteInfoMapper.getSiteInfo((String) sendMap.get("employeeNo"));
					SiteModel lastOne = lastData.get(lastData.size() - 1);
					if (!"3".equals(lastOne.getWorkState())) {
						// old workState is not holiday status, update holidayStartDate
						try {
							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							String curDate = sdf.format(date);
							sendMap.put("holidayStartDate", curDate);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				siteInfoMapper.siteUpdate(sendMap);
				if(sendMap.get("workState").equals("1")) {
					siteInfoMapper.salesSentenceUpdate(sendMap);
				}

				// 工作状态由终了变成稼动中的时候，删除T010表中无用的数据
				if ((sendMap.get("prevWorkState").equals("1") || sendMap.get("prevWorkState").equals("2")) && sendMap.get("workState").equals("0") && sendMap.get("prevAdmissionEndDate") != null) {
					salesSituationService.deleteUselessSalesSituationRecord(sendMap.get("employeeNo").toString(), sendMap.get("prevAdmissionEndDate").toString());
					// 存在预定终了日期的时候，删除T010表中无用的数据
				} else if (sendMap.get("scheduledEndDate") == null && sendMap.get("prevScheduledEndDate") != null) {
					salesSituationService.deleteUselessSalesSituationRecord(sendMap.get("employeeNo").toString(), sendMap.get("prevScheduledEndDate").toString());
					// 当工作状态变为休假中的时候，删除T010表中无用的数据
				} else if (sendMap.get("workState").equals("3") && sendMap.get("admissionStartDate") != null) {
					salesSituationService.deleteUselessSalesSituationRecord(sendMap.get("employeeNo").toString(), sendMap.get("admissionStartDate").toString().substring(0, 6));
				}
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Map<String, Object> formateData(SiteModel siteModel) {
		Map<String, Object> sendMap = new HashMap<String, Object>();
		sendMap.put("nonSiteMonths", "");
		sendMap.put("nonSitePeriod", "");
		if (siteModel.getEmployeeNo() != null && siteModel.getEmployeeNo().length() != 0) {
			sendMap.put("employeeNo", siteModel.getEmployeeNo());
		}
		if (siteModel.getCustomerNo() != null && siteModel.getCustomerNo().length() != 0) {
			sendMap.put("customerNo", siteModel.getCustomerNo());
		}
		if (siteModel.getTopCustomerNo() != null && siteModel.getTopCustomerNo().length() != 0) {
			sendMap.put("topCustomerNo", siteModel.getTopCustomerNo());
		}
		if (siteModel.getAdmissionStartDate() != null && siteModel.getAdmissionStartDate().length() != 0) {
			sendMap.put("admissionStartDate", siteModel.getAdmissionStartDate());
		}
		if (siteModel.getHolidayStartDate() != null && siteModel.getHolidayStartDate().length() != 0) {
			sendMap.put("holidayStartDate", siteModel.getHolidayStartDate());
		}
		if (siteModel.getStationCode() != null && siteModel.getStationCode().length() != 0) {
			sendMap.put("location", siteModel.getStationCode());
		}
		if (siteModel.getSiteManager() != null && siteModel.getSiteManager().length() != 0) {
			sendMap.put("siteManager", siteModel.getSiteManager());
		}
		if (siteModel.getAdmissionEndDate() != null && siteModel.getAdmissionEndDate().length() != 0) {
			sendMap.put("admissionEndDate", siteModel.getAdmissionEndDate());
		}
		if (siteModel.getUnitPrice() != null && siteModel.getUnitPrice().length() != 0) {
			sendMap.put("unitPrice", siteModel.getUnitPrice());
		}
		if (siteModel.getSiteRoleCode() != null && siteModel.getSiteRoleCode().length() != 0) {
			sendMap.put("siteRoleCode", siteModel.getSiteRoleCode());
		}
		if (siteModel.getPayOffRange1() != null && siteModel.getPayOffRange1().length() != 0) {
			sendMap.put("payOffRange1", siteModel.getPayOffRange1());
		}
		if (siteModel.getPayOffRange2() != null && siteModel.getPayOffRange2().length() != 0) {
			sendMap.put("payOffRange2", siteModel.getPayOffRange2());
		}
		if (siteModel.getSystemName() != null && siteModel.getSystemName().length() != 0) {
			sendMap.put("systemName", siteModel.getSystemName());
		}
		if (siteModel.getDevelopLanguageCode() != null && siteModel.getDevelopLanguageCode().length() != 0) {
			sendMap.put("developLanguageCode", siteModel.getDevelopLanguageCode());
		}
		if (siteModel.getRelatedEmployees() != null && siteModel.getRelatedEmployees().length() != 0) {
			sendMap.put("relatedEmployees", siteModel.getRelatedEmployees());
		}
		if (siteModel.getScheduledEndDate() != null && siteModel.getScheduledEndDate().length() != 0) {
			sendMap.put("scheduledEndDate", siteModel.getScheduledEndDate());
		}
		if (siteModel.getLevelCode() != null && siteModel.getLevelCode().length() != 0) {
			sendMap.put("levelCode", siteModel.getLevelCode());
		}
		if (siteModel.getRemark() != null && siteModel.getRemark().length() != 0) {
			sendMap.put("remark", siteModel.getRemark());
		}
		if (siteModel.getTypeOfIndustryCode() != null && siteModel.getTypeOfIndustryCode().length() != 0) {
			sendMap.put("typeOfIndustryCode", siteModel.getTypeOfIndustryCode());
		}
		if (siteModel.getWorkDate() != null && siteModel.getWorkDate().length() != 0) {
			sendMap.put("workDate", siteModel.getWorkDate());
		}
		if (siteModel.getWorkState() != null && siteModel.getWorkState().length() != 0) {
			sendMap.put("workState", siteModel.getWorkState());
		}
		if (siteModel.getDailyCalculationStatus() != null && siteModel.getDailyCalculationStatus().length() != 0) {
			sendMap.put("dailyCalculationStatus", siteModel.getDailyCalculationStatus());
		} else {
			sendMap.put("dailyCalculationStatus", "1");
		}
		sendMap.put("updateUser", siteModel.getUpdateUser());
		return sendMap;
	}

	public List<SiteModel> getSiteInfo(String employeeNo) {

		List<SiteModel> siteList = siteInfoMapper.getSiteInfo(employeeNo);
		return siteList;
	}

	/**
	 * 現場情報を削除
	 * 
	 * @param emp
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSiteInfo(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			siteInfoMapper.deleteSiteInfo(sendMap);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	public List<SiteModel> getDevelopLanguage() {
		return siteInfoMapper.getDevelopLanguage();
	}

}
