package jp.co.lyc.cms.model;

import java.math.BigDecimal;

public class CompletePercetModal {
    private String yearAndMonthOfPercent;
    private BigDecimal completePercet;
    private String createTime;
    private String updateTime;
    private String updateUser;

    // Getters and Setters
    public BigDecimal getCompletePercet() {
        return completePercet;
    }

    public void setCompletePercet(BigDecimal completePercet) {
        this.completePercet = completePercet;
    }

    public String getYearAndMonthOfPercent() {
        return yearAndMonthOfPercent;
    }

    public void setYearAndMonthOfPercent(String yearAndMonthOfPercent) {
        this.yearAndMonthOfPercent = yearAndMonthOfPercent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
