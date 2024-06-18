package jp.co.lyc.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.CompletePercetModal;
import jp.co.lyc.cms.service.CompletePercetService;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/completePercet")
public class CompletePercetController extends BaseController {
    @Autowired
    private CompletePercetService completePercetService;

    public void upsertCompletePercet(CompletePercetModal completePercetModal) {
        completePercetService.upsertCompletePercet(completePercetModal);

    }

    public CompletePercetModal getCompletePercetByYearAndMonth(CompletePercetModal completePercetModal) {
        CompletePercetModal result = completePercetService
                .getCompletePercetByYearAndMonth(completePercetModal);
        return result;
    }

}
