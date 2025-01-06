package jp.co.lyc.cms.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.PayrollModel;
import jp.co.lyc.cms.model.PayrollAveragedModel;
import jp.co.lyc.cms.model.PayrollCustomerModel;

@Mapper
public interface PayrollMapper {
	/**
	 * 給料情報の取得
	 * 
	 * @return
	 */
	public ArrayList<PayrollModel> getPayroll(String employeeNo);
	/**
	 * 現場情報の取得
	 * 
	 * @return
	 */
	public ArrayList<PayrollCustomerModel> getCustomer(String employeeNo);
	/**
	 * 精算平均値の取得
	 * 
	 * @return
	 */
	public ArrayList<PayrollAveragedModel> getAveraged(String employeeNo);
	/**
	 * 精算平均値のデポジット
	 * 
	 * @return
	 */
	public boolean insertAverage(PayrollAveragedModel payrollAveragedModel);
	/**
	 * 精算平均値のアップデート
	 * 
	 * @return
	 */
	public void updateAverage(PayrollAveragedModel payrollAveragedModel);
}

