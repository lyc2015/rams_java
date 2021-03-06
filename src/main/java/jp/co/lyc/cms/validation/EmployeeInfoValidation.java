package jp.co.lyc.cms.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.util.StatusCodeToMsgMap;
import jp.co.lyc.cms.util.UtilsCheckMethod;

public class EmployeeInfoValidation implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return EmployeeModel.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		EmployeeModel p = (EmployeeModel) obj;

		StackTraceElement elements[] = Thread.currentThread().getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			StackTraceElement stackTraceElement = elements[i];
			String methodName = stackTraceElement.getMethodName();
			if (methodName.equals("getEmployeeInfo")) {
				if (!UtilsCheckMethod.isNullOrEmpty(p.getAgeFrom()) && !UtilsCheckMethod.isNullOrEmpty(p.getAgeTo())) {
					if (Integer.parseInt(p.getAgeFrom()) < Integer.parseInt(p.getAgeTo())) {
						errors.rejectValue("ageFrom", "", StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG009", "年齢"));
					}
				}
			} else if (methodName.equals("insertEmployee") || methodName.equals("updateEmployee")) {
				if (UtilsCheckMethod.isNullOrEmpty(p.getEmployeeFristName())) {
					errors.rejectValue("employeeFristName", "社員名",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "社員名"));
				}
				if (UtilsCheckMethod.isNullOrEmpty(p.getEmployeeLastName())) {
					errors.rejectValue("employeeLastName", "社員氏",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "社員氏"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getPhoneNo()) && !UtilsCheckMethod.checkPhoneNo(p.getPhoneNo())) {
					errors.rejectValue("phoneNo", "電話番号", StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "電話番号"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getCompanyMail())
						&& !UtilsCheckMethod.checkMail(p.getCompanyMail())) {
					errors.rejectValue("companyMail", "社内メール",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "社内メール"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getFurigana1() + p.getFurigana2())
						&& !UtilsCheckMethod.checkKatakana(p.getFurigana1() + p.getFurigana2())) {
					errors.rejectValue("furigana", "カタカナ", StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "カタカナ"));
				}
				if (UtilsCheckMethod.isNullOrEmpty(p.getGenderStatus())) {
					errors.rejectValue("genderStatus", "性別", StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "性別"));
				}
				if (UtilsCheckMethod.isNullOrEmpty(p.getNationalityCode())) {
					errors.rejectValue("nationalityCode", "出身地",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "出身地"));
				}
				if (UtilsCheckMethod.isNullOrEmpty(p.getIntoCompanyYearAndMonth())) {
					errors.rejectValue("intoCompanyYearAndMonth", "入社年月",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "入社年月"));

				}
				/**
				 * 社員の場合
				 */
				if (p.getEmployeeStatus() == "0") {
					if (UtilsCheckMethod.isNullOrEmpty(p.getCompanyMail())) {
						errors.rejectValue("companyMail", "社内メール",
								StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "社内メール"));
					}
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getAlphabetName1())
						&& !UtilsCheckMethod.alphabetFormat(p.getAlphabetName1())) {
					errors.rejectValue("alphabetName1", "ローマ字",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "ローマ字"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getAlphabetName2())
						&& !UtilsCheckMethod.alphabetFormat(p.getAlphabetName2())) {
					errors.rejectValue("alphabetName2", "ローマ字",
							StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "ローマ字"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getPostcode())
						&& !UtilsCheckMethod.alphabetFormat(p.getPostcode())
						&& UtilsCheckMethod.isNullOrEmpty(p.getFirstHalfAddress())) {
					errors.rejectValue("postcode", "郵便番号", StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG003", "郵便番号"));
				}
				if (!UtilsCheckMethod.isNullOrEmpty(p.getResumeInfo1())) {
					if (UtilsCheckMethod.isNullOrEmpty(p.getResumeName1())) {
						errors.rejectValue("resumeName1", "履歴書1名",
								StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "履歴書1名"));
					}
				}

			}
		}

	}

}
