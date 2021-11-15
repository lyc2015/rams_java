package jp.co.lyc.cms.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.model.SendInvoiceModel;
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

	public List<SendInvoiceModel> selectSendInvoice(HashMap<String, String> dutyManagementModel) {
		List<SendInvoiceModel> resultMod = sendInvoiceMapper.selectSendInvoice(dutyManagementModel);
		return resultMod;
	}
}
