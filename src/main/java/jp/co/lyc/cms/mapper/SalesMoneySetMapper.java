package jp.co.lyc.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.MoneySetModel;


@Mapper
public interface SalesMoneySetMapper {

	public List<MoneySetModel> getMoneySetList();

	public int insertMoneySet(MoneySetModel model);

	public int updateMoneySet(MoneySetModel model);
	
	public int deleteMoneySet(String id);
	
}
