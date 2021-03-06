package jp.co.lyc.cms.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.SalesSendLetterModel;
import jp.co.lyc.cms.model.SalesSendLettersListName;

@Mapper
public interface SalesSendLetterMapper {

	/**
	 * ログイン
	 * 
	 * @param sendMap
	 * @return
	 */

	public List<SalesSendLetterModel> getSalesCustomers();

	public List<String> getBusinessCount();

	public List<SalesSendLetterModel> getSalesCustomerByNo(String customerNo);

	public List<SalesSendLetterModel> getSalesPersons(String customerNo);
	
	public List<SalesSendLetterModel> getSalesPersonsCC(String customerNo);

	public int creatList(SalesSendLetterModel model);

	public List<SalesSendLetterModel> getLists();

	public int listNameUpdate(SalesSendLettersListName model);

	public int deleteCustomerList(SalesSendLetterModel model);

	public List<SalesSendLetterModel> getSalesCustomersByNos(String[] ctmNos);

	public int deleteList(String storageListName);

	public void customerListUpdate(String storageListName, String customerList);

	public String getCustomerList(String storageListName);

	public String getMaxStorageListName();

	public void deleteCustomerListByNo(SalesSendLetterModel model);

	public SalesSendLetterModel getMainChargeList(String storageListName);

	public void customerSendMailStorageListUpdate(String storageListName, String mainChargeList,
			String departmentCodeList);

	public String getSendLetterMonth();

	public void updateCustomers(String nowMonth);
}
