package jp.co.lyc.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.SalesMoneySetMapper;
import jp.co.lyc.cms.model.MoneySetModel;

@Component
public class SalesMoneySetService {

	@Autowired
	SalesMoneySetMapper salesMoneySetMapper;

	public List<MoneySetModel> getMoneySetList() {
		return salesMoneySetMapper.getMoneySetList();
	}

	public int insertMoneySet(MoneySetModel model) {
		return salesMoneySetMapper.insertMoneySet(model);
	}

	public int updateMoneySet(MoneySetModel model) {
		return salesMoneySetMapper.updateMoneySet(model);
	}
	
	public int deleteMoneySet(MoneySetModel model) {
		return salesMoneySetMapper.deleteMoneySet(model);
	}

}
