package jp.co.lyc.cms.mapper;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.CompletePercetModal;

@Mapper
public interface CompletePercetMapper {
    public void upsertCompletePercet(CompletePercetModal completePercetModal);

    public CompletePercetModal getCompletePercetByYearAndMonth(CompletePercetModal completePercetModal);

}
