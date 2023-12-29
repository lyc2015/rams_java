package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.AnnualSalesSituationConfirmModel;
import jp.co.lyc.cms.service.AnnualSalesSituationConfirmService;


@Controller
@RequestMapping(value = "/annualSalesSituationConfirm")
public class AnnualSalesSituationConfirmController extends BaseController {

  @Autowired
  AnnualSalesSituationConfirmService annualSalesSituationConfirmService;

  @RequestMapping(value = "/getAnnualSalesSituationConfirmList", method = RequestMethod.POST)
  @ResponseBody
  public List<AnnualSalesSituationConfirmModel> getAnnualSalesSituationConfirmList(@RequestBody AnnualSalesSituationConfirmModel data) {
    List<AnnualSalesSituationConfirmModel> annualSalesSituationConfirmList = new ArrayList<AnnualSalesSituationConfirmModel>();
    annualSalesSituationConfirmList = annualSalesSituationConfirmService.getAnnualSalesSituationConfirmList(data.getSalesYearAndMonth());

    return annualSalesSituationConfirmList;
  }
}
