package jp.co.lyc.cms.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.model.SalesContent;
import jp.co.lyc.cms.model.SalesSituationCsvModel;
import jp.co.lyc.cms.model.SalesSituationModel;

@Mapper
public interface SalesSituationMapper {

	/**
	 * ログイン
	 * 
	 * @param sendMap
	 * @return
	 */

	public List<SalesSituationModel> getSalesSituationModel(String sysDate, String curDate, String salesDate);

	public List<SalesSituationModel> getSalesSituationInfo(String sysDate, String curDate, String salesDate);

	public List<SalesSituationModel> getSalesSituationInfoAfterToday(String sysDate, String curDate, String salesDate);

	public List<SalesSituationModel> getDevelopLanguage();

	public String getEmpNextAdmission(String employeeNo);

	public void insertEmpNextAdmission(SalesSituationModel model);

	public void updateEmpNextAdmission(SalesSituationModel model);

	public List<String> getT010SalesSituation(String sysDate, String curDate, String salesDate);

	public int insertSalesSituation(SalesSituationModel model);

	public int updateEmployeeSiteInfo(SalesSituationModel model);

	public int updateSalesSituation(SalesSituationModel model);

	public List<SalesSituationModel> getPersonalSalesInfo(String empNo);

	public List<SalesSituationModel> getT010SalesSituationLatestByemployeeNo(String empNo);

	public List<SalesSituationModel> getPersonalSalesInfoFromT019(String empNo);

	public int updateEmployeeAddressInfo(SalesSituationModel model);

	public int updateSalesSentence(SalesContent model);

	public int getCount(String empNo);

	public List<SalesSituationModel> checkEmpNoAndYM(SalesSituationModel model);

	public int insertDataStatus(SalesSituationModel model);

	public int updateDataStatus(SalesSituationModel model);

	public int updateEMPInfo(SalesSituationModel model);

	public int updateBPAllEMPInfo(SalesSituationModel model);

	@Update("UPDATE\r\n"
			+ "		T019SalesSentence\r\n"
			+ "		SET\r\n"
			+ "		stationCode = #{model.stationCode},\r\n"
			+ "		updateTime= date_add(now(), interval 9 hour)\r\n"
			+ "		WHERE\r\n"
			+ "		employeeNo = #{model.employeeNo}")
	public void updateSalesSentenceByemployeeNo(@Param("model") SalesContent model);

	public int updateBPEMPInfo(SalesSituationModel model);

	public List<BpInfoModel> getT011BpInfoSupplement();

	public List<String> getEmployeeNoList(String salesYearAndMonth, String salesDate);

	public List<String> getBpNoList(String salesYearAndMonth, String salesDate);

	public List<SalesSituationModel> getSalesSituationList(List<String> employeeNoList,String salesYearAndMonth);

	public List<SalesSituationModel> getBpSalesSituationList(List<String> BpNoList);

	public List<String> getEmployeeNoListBefore(String salesDate);

	public List<SalesSituationModel> getT010SalesSituationBefore(String sysDate, String curDate, String salesDate);

	public void updateBPR(SalesSituationModel model);

	public void updateBPRSiteInfo(SalesSituationModel model);

	public List<SalesSituationModel> getSiteRoleCode();

	public List<SalesSituationModel> getT010SalesSituationByEmployeeNo(List<String> employeeNoList, String salesDate);

	public List<SalesSituationModel> getInterviewLists(List<String> employeeNoList);

	public int updateInterviewLists(SalesSituationModel model);

	public List<SalesSituationModel> getEmployeeHoliday(String date);

	public List<SalesSituationModel> getEmployeeHolidayRecentSite(String employeeNo);

	public List<SalesSituationModel> getEmployeeRetire(String date);

	public List<SalesSituationModel> getEmployeeRetireSiteInfo(List<String> employeeNoList);

	public List<SalesSituationModel> getBpEmployeeConfirmNoList();

	public List<SalesSituationModel> getBpEmployeeConfirm(List<String> employeeNoList, String date);

	public List<SalesSituationModel> getEmployeeSiteWorkTermList(String salesYearAndMonth);

	public void deleteUselessSalesSituationRecord(String employeeNo, String admissionEndDate);

	// 営業csvダウンロード
	public List<SalesSituationCsvModel> getSalesSituationCSV(List<String> empList);
}
