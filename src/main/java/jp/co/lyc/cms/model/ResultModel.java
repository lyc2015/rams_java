package jp.co.lyc.cms.model;

public class ResultModel {
	/** 结果*/
	public Boolean result; // true 成功  false 失败

	/** 错误信息*/
	public String errMsg;
	
	public ResultModel() {
		this.result = false;
		this.errMsg = "失敗しました";
	}
	
	public void setUnKnowErr() {
		this.result = false;
		this.errMsg = "不明なエラー、管理者に連絡してください！";
	}
	
	public void setSuccess() {
		this.result = true;
		this.errMsg = "";
	}
	
	public void setErrMsg(String errMsg) {
		if( errMsg.equals("Invalid Addresses"))  this.errMsg = "メールアドレスが存在しません";
		else this.errMsg = errMsg;
		this.result = false;
	}


	public Boolean getResult() {
		return result;
	}

//	public void setResult(Boolean result) {
//		this.result = result;
//	}

	public String getErrMsg() {
		return errMsg;
	}

//	public void setErrMsg(String errMsg) {
//		this.errMsg = errMsg;
//	}
	
	
}
