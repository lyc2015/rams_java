package jp.co.lyc.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.AnnualSalesSituationConfirmMapper;
import jp.co.lyc.cms.model.AnnualSalesSituationConfirmModel;

@Component
public class AnnualSalesSituationConfirmService {

  @Autowired
  AnnualSalesSituationConfirmMapper annualSalesSituationConfirmMapper;
  
  public List<AnnualSalesSituationConfirmModel> getAnnualSalesSituationConfirmList(String year) {
    return annualSalesSituationConfirmMapper.getAnnualSalesSituationConfirmList(year);
  }
  
}
