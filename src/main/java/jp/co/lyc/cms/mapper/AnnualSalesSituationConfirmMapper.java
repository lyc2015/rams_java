package jp.co.lyc.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.AnnualSalesSituationConfirmModel;

@Mapper
public interface AnnualSalesSituationConfirmMapper {

  public List<AnnualSalesSituationConfirmModel> getAnnualSalesSituationConfirmList(String year);
  
}
