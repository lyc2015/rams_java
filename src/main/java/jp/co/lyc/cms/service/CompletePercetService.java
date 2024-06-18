package jp.co.lyc.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.CompletePercetMapper;
import jp.co.lyc.cms.model.CompletePercetModal;

@Component
public class CompletePercetService {

    @Autowired
    private CompletePercetMapper completePercetMapper;

    public void upsertCompletePercet(CompletePercetModal completePercetModal) {
        completePercetMapper.upsertCompletePercet(completePercetModal);
    }

    public CompletePercetModal getCompletePercetByYearAndMonth(CompletePercetModal completePercetModal) {
        CompletePercetModal result = completePercetMapper
                .getCompletePercetByYearAndMonth(completePercetModal);
        return result;
    }
}
