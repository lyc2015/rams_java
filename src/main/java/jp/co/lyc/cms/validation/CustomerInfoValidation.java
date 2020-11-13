package jp.co.lyc.cms.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.co.lyc.cms.model.CustomerDepartmentInfoModel;
import jp.co.lyc.cms.model.CustomerInfoModel;
import jp.co.lyc.cms.util.StatusCodeToMsgMap;
import jp.co.lyc.cms.util.UtilsCheckMethod;

public class CustomerInfoValidation implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return CustomerInfoModel.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		// TODO Auto-generated method stub
		CustomerInfoModel p = (CustomerInfoModel) obj;

		StackTraceElement elements[] = Thread.currentThread().getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			StackTraceElement stackTraceElement = elements[i];
			String methodName = stackTraceElement.getMethodName();
			if (methodName.equals("toroku")) {
				if (p.getCustomerDepartmentList() == null) {
					if (UtilsCheckMethod.isNullOrEmpty(p.getCustomerName())) {
						errors.rejectValue("customerName", "",
								StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "お客様名"));
					}
					if (!UtilsCheckMethod.isNullOrEmpty(p.getPurchasingManagersMail())) {
						if (!UtilsCheckMethod.checkMail(p.getPurchasingManagersMail())) {
							errors.rejectValue("purchasingManagersMail", "",
									StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG0011", "お客様メール"));
						}
					}
					if (!UtilsCheckMethod.isNullOrEmpty(p.getUrl())) {
						if (UtilsCheckMethod.checkUrl(p.getUrl())) {
							errors.rejectValue("url", "",
									StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG007", "お客様URL"));
						}
					}
					if (!UtilsCheckMethod.isNullOrEmpty(p.getCapitalStock())) {
						if (!UtilsCheckMethod.numberFormat(p.getCapitalStock())) {
							errors.rejectValue("capitalStock", "",
									StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG007", "資本金"));
						}
					}
					if (p.getTopCustomerInfo() != null
							&& !UtilsCheckMethod.isNullOrEmpty(p.getTopCustomerInfo().getUrl())) {
						if (!UtilsCheckMethod.checkUrl(p.getTopCustomerInfo().getUrl())) {
							errors.rejectValue("topCustomerInfo.url", "",
									StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG0011", "上位お客様のURL"));
						}
					}
				} else {
					for (CustomerDepartmentInfoModel a : p.getCustomerDepartmentList()) {
						if (UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentCode())
								&& UtilsCheckMethod.isNullOrEmpty(a.getPositionCode())
								&& (!UtilsCheckMethod.isNullOrEmpty(a.getResponsiblePerson())
										|| !UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentMail())
										|| !UtilsCheckMethod.isNullOrEmpty(a.getTypeOfIndustryCode())
										|| !UtilsCheckMethod.isNullOrEmpty(a.getStationCode())
										|| !UtilsCheckMethod.isNullOrEmpty(a.getDevelopLanguageCode1())
										|| !UtilsCheckMethod.isNullOrEmpty(a.getDevelopLanguageCode2()))) {
							errors.rejectValue("customerDepartmentList", "",
									StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "お客様部門と職位"));
						} else if (!UtilsCheckMethod.isNullOrEmpty(a.getResponsiblePerson())
								&& !UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentMail())
								&& !UtilsCheckMethod.isNullOrEmpty(a.getTypeOfIndustryCode())
								&& !UtilsCheckMethod.isNullOrEmpty(a.getStationCode())
								&& !UtilsCheckMethod.isNullOrEmpty(a.getDevelopLanguageCode1())
								&& !UtilsCheckMethod.isNullOrEmpty(a.getDevelopLanguageCode2())) {
							if (UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentCode())
									&& !UtilsCheckMethod.isNullOrEmpty(a.getPositionCode())) {
								errors.rejectValue("customerDepartmentList", "",
										StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "お客様部門"));
							} else if (!UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentCode())
									&& UtilsCheckMethod.isNullOrEmpty(a.getPositionCode())) {
								errors.rejectValue("customerDepartmentList", "",
										StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG001", "職位"));
							}
						}
						if (!UtilsCheckMethod.isNullOrEmpty(a.getCustomerDepartmentMail())) {
							if (!UtilsCheckMethod.checkMail(a.getCustomerDepartmentMail())) {
								errors.rejectValue("customerDepartmentList", "",
										StatusCodeToMsgMap.getErrMsgbyCodeReplace("MSG007", "お客様部門メール"));
							}
						}
					}
				}
			}
		}
	}

}
