package jp.co.lyc.cms.mapper;

import java.util.HashMap;
import java.util.List;

import jp.co.lyc.cms.model.SendInvoiceModel;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SendInvoiceMapper {
	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<SendInvoiceModel> selectSendInvoice(HashMap<String, String> dutyManagementModel);
}
