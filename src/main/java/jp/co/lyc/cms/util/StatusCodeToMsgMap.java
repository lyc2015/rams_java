package jp.co.lyc.cms.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class StatusCodeToMsgMap {
	private static Map<String, String> statusCodeToMsg = new HashMap<String, String>();

	static {
		statusCodeToMsg.put("MSG001", "[0]入力してください。");
		statusCodeToMsg.put("MSG002", "ユーザー名またはパースワード入力が間違いました。");
		statusCodeToMsg.put("MSG003", "[0]が間違っているので、もう一度確認してください");
		statusCodeToMsg.put("MSG004", "データ存在してないです、検索条件を確認してください。");
		statusCodeToMsg.put("MSG005", "[0]を確認してください。");
		statusCodeToMsg.put("MSG006", "削除できない、[0]を確認してください。");
		statusCodeToMsg.put("MSG007", "正しい[0]を入力してください");
		statusCodeToMsg.put("MSG008", "登録データが存在していますので、もう一度確認してください。");
		statusCodeToMsg.put("MSG009", "正しい[0]範囲を入力してください。");
		statusCodeToMsg.put("MSG010", "入場時間が間違って、前回の退場時間より大きくしください。");
		statusCodeToMsg.put("MSG011", "前回現場がまだ終わらないので、登録できない");
		statusCodeToMsg.put("MSG012", "データが登録するので、単金調整を選択不可");
		statusCodeToMsg.put("MSG013", "");
	}

	public static String getErrMsgbyCode(String ecode) {
		if (!StringUtils.hasText(ecode)) {
			return "";
		}

		return statusCodeToMsg.get(ecode);
	}

	public static String getErrMsgbyCodeReplace(String ecode, String replaceName) {
		if (!StringUtils.hasText(ecode)) {
			return "";
		}
		return statusCodeToMsg.get(ecode).replace("[0]", replaceName);
	}

}