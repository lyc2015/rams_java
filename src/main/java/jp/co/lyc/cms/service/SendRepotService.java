package jp.co.lyc.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.SendRepotMapper;
import jp.co.lyc.cms.model.SendRepotsListName;
import jp.co.lyc.cms.model.SendRepotModel;


@Component
public class SendRepotService {

	@Autowired
	SendRepotMapper sendRepotMapper;
	
	public List<SendRepotModel> getSalesCustomers(){
		return sendRepotMapper.getSalesCustomers();
	};
	
	public List<SendRepotModel> getSalesPersons(String customerNo){
		return sendRepotMapper.getSalesPersons(customerNo);
	};
	
	public int creatList(SendRepotModel model){
		return sendRepotMapper.creatList(model);
	};
	
	public List<SendRepotModel> getLists(){
		return sendRepotMapper.getLists();
	};
	
	public int listNameUpdate(SendRepotsListName model){
		return sendRepotMapper.listNameUpdate(model);
	};
	
	public List<SendRepotModel> getSalesCustomersByNos(String[] ctmNos){
		return sendRepotMapper.getSalesCustomersByNos(ctmNos);
	};
	
	public int deleteList(String storageListName){
		return sendRepotMapper.deleteList(storageListName);
	};
}
