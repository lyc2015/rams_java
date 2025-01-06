package jp.co.lyc.cms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jp.co.lyc.cms.mapper.PayrollMapper;
import jp.co.lyc.cms.model.PayrollModel;
import jp.co.lyc.cms.model.PayrollCustomerModel;
import jp.co.lyc.cms.model.PayrollAveragedModel;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class PayrollService {
	@Autowired
	PayrollMapper payrollMapper;
	
	 // 获取员工工资列表信息
	public List<PayrollModel> getPayroll(String employeeNo) {
	    // 获取员工工资列表
	    List<PayrollModel> payrollList = payrollMapper.getPayroll(employeeNo);
	    if (payrollList.isEmpty()) {
	        return payrollList; // 如果为空，直接返回空列表
	    }
	    
	    //定义输出的列表
	    List<PayrollModel> outPutList = new ArrayList<PayrollModel>();

	    // 定义日期格式
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
	    
	    // 定义默认起始日期
	    YearMonth globalStartDate =  YearMonth.parse("202201", formatter);
	    
        // 获取进入公司的年月	
        String date = payrollList.get(0).getIntoCompanyYearAndMonth().substring(0,6); 
        YearMonth intoCompanyDate = YearMonth.parse(date, formatter);
        // 定义需要插入数据的开始日期
        YearMonth startDate = globalStartDate;
        // 如果进入公司时间晚于全局起始日期，从进入公司的时间开始插入，否则从202201开始
        if (intoCompanyDate.isAfter(globalStartDate)) {
            startDate = intoCompanyDate;
        }
        
        //最后一个月的薪资时间
        YearMonth lastMonth = YearMonth.parse(payrollList.get(payrollList.size()-1).getReflectYearAndMonth(), formatter);
        //如果这个时间就在上个月，不给他加上一个月的数据
        //如果时间早于上个月，增加上一个月的数据 
        // 获取当前日期的年份和上一个月份
	    YearMonth nearMonth = YearMonth.now().minusMonths(1);
	    if(nearMonth.isAfter(lastMonth)) {
	    	PayrollModel payrollModel1=format(nearMonth,payrollList.get(payrollList.size()-1),true);
	    	payrollList.add(payrollModel1);
//	    	System.out.println(payrollModel1);
//	    	System.out.println(payrollList.size());
	    }	
        
    	//开始遍历员工的薪资信息
	    for (int j = 0; j < payrollList.size(); j++) {
	    	//当前角标数据的时间
//	    	System.out.println("............reflectDate...................");
//	    	System.out.println(payrollList.get(j).getReflectYearAndMonth());
	    	YearMonth reflectDate = YearMonth.parse(payrollList.get(j).getReflectYearAndMonth(), formatter);
//	    	System.out.println(reflectDate);
//	    	System.out.println("............reflectDate...................");
	  	  	//[2021.6，2021.7，2021.8,2022.1,2022.5,2022.9] [2024.9]
	    	if(startDate.isAfter(reflectDate)) {
    	    	//1、查出来的数据月份比计算时间早  不要这个数据，不往outlist推送
//		    	System.out.println("............不做处理...................");
	    	}else if(reflectDate.isAfter(startDate)){
	    		//2、查出来的数据月份比计算时间晚，有工资的时间晚于计算时间时
	    		if(j==0) {
	    			//[2022.3,2022.5,2022.9] 
	    			outPutList.add(format(reflectDate,payrollList.get(j),false));
	    			startDate = reflectDate.plusMonths(1);
	    		}else {
	    			//[2021.6，2021.7，2021.8,2022.1,2022.5,2022.9,2024.12] 
	    	    	long monthsBetween = startDate.until(reflectDate, ChronoUnit.MONTHS);
//	    	    	System.out.println(monthsBetween);
//	    	    	System.out.println("............monthsBetween...................");
//	    	    	System.out.println(startDate);
//	    	    	System.out.println("............startDate...................");
//	    	    	System.out.println(reflectDate);
//	    	    	System.out.println("............reflectDate...................");
	    	    	
	    	    	for(int i = 0; i < monthsBetween; i++) {
	                   //2022.1~2022.5
	    	    		outPutList.add(format(startDate,payrollList.get(j-1),false));
	    	    		startDate = startDate.plusMonths(1);
//	    	    		System.out.println(startDate);
//	    	    	   	System.out.println("............startDate...................");
	    	    	}
	    	    	outPutList.add(format(reflectDate,payrollList.get(j),false));
	    	    	startDate = startDate.plusMonths(1);
	    		}
    	    }else {
    	    	//3、查出来的数据刚好等于开始计算的时间   [2022.1,2022.5,2022.9] 
    	    	outPutList.add(format(reflectDate,payrollList.get(j),false));
    	    	startDate = startDate.plusMonths(1);
//    	    	System.out.println("............刚好等于  startDate...................");
//    	    	System.out.println(startDate);
    	    }
	    }   
	    outPutList = mergeFormatList(employeeNo, outPutList);
	    outPutList = mergeAveragedList(employeeNo, outPutList);
	    
	    return outPutList;
	}
	
	// 获取现场情况列表与员工工资列表整合到一起
	public List<PayrollModel> mergeFormatList(String employeeNo, List<PayrollModel> array) {
	    // 现场情况列表
	    List<PayrollCustomerModel> customerList = payrollMapper.getCustomer(employeeNo);
	    if (customerList.isEmpty()) {
	        return array; // 如果为空，直接返回输入的列表
	    }

	    // 定义输出的列表，初始为传入的 array
	    List<PayrollModel> mergeList = new ArrayList<>(array);

	    // 定义日期格式
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

	    // 遍历现场情况列表
	    for (PayrollModel payroll : mergeList) {
	    	String yearAndMonth = payroll.getYearAndMonth();
            if (yearAndMonth == null || yearAndMonth.isEmpty()) {
                continue; // 如果年份和月份为空，跳过当前记录
            }
            YearMonth mergeStartDate = YearMonth.parse(yearAndMonth, formatter);

	        // 遍历 mergeList
	        for (PayrollCustomerModel customer : customerList) {
	        	//现场开始时间
	        	String admissionStartDate = customer.getAdmissionStartDate();
	        	//现场结束时间
		        String admissionEndDate = customer.getAdmissionEndDate();
	        
		        // 获取当前日期的年份和上一个月份
			    YearMonth nearMonth = YearMonth.now().minusMonths(1);
		        
		        if (admissionStartDate == null || admissionStartDate.isEmpty()) {
		            continue; // 如果起始日期为空，跳过当前记录
		        }
		        if (admissionEndDate == null || admissionEndDate.isEmpty()) {
		        	admissionEndDate = nearMonth.format(formatter).toString();
		        }
		        
		        String _admissionStartDate = admissionStartDate.substring(0, 6); // 截取前6位 "201909"
		        String _admissionEndDate = admissionEndDate.substring(0, 6); // 截取前6位 "201909"
		        YearMonth startDate = YearMonth.parse(_admissionStartDate, formatter);
		        YearMonth endDate = YearMonth.parse(_admissionEndDate, formatter);

	            // 如果日期匹配，则更新对应字段
	            if (startDate.equals(mergeStartDate)) {
//	            	System.out.println(mergeStartDate + " 开始时间" + startDate);
	                payroll.setCustomerName(customer.getCustomerName());
	                payroll.setCustomerNo(customer.getCustomerNo());
	                payroll.setUnitPrice(customer.getUnitPrice());
	                continue;
	            }else if (endDate.equals(mergeStartDate)) {
//	            	System.out.println(mergeStartDate + " 结束时间" + endDate);
	                payroll.setCustomerName(customer.getCustomerName());
	                payroll.setCustomerNo(customer.getCustomerNo());
	                payroll.setUnitPrice(customer.getUnitPrice());
	                continue;
	            }else if (!mergeStartDate.isBefore(startDate) && !mergeStartDate.isAfter(endDate)) {
//	                System.out.println(mergeStartDate + " 在时间段内");
	                payroll.setCustomerName(customer.getCustomerName());
	                payroll.setCustomerNo(customer.getCustomerNo());
	                payroll.setUnitPrice(customer.getUnitPrice());
	                continue;
	            }
	        }
	    }

	    return mergeList;
	}

	public PayrollModel format(YearMonth startDate, PayrollModel ipayroll, boolean isAdd ) {
		PayrollModel newPayroll = new PayrollModel();
        newPayroll.setYearAndMonth(startDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
        newPayroll.setEmployeeNo(ipayroll.getEmployeeNo());
        newPayroll.setEmployeeStatus(ipayroll.getEmployeeStatus());
        newPayroll.setIntoCompanyYearAndMonth(ipayroll.getIntoCompanyYearAndMonth());
        newPayroll.setUnitPrice(ipayroll.getUnitPrice());
        newPayroll.setSalary(ipayroll.getSalary());
        newPayroll.setBonusPayoff(ipayroll.getBonusPayoff());
        newPayroll.setSocialInsurancePayoff(ipayroll.getSocialInsurancePayoff());
        newPayroll.setOthersPayoff(ipayroll.getOthersPayoff());
        newPayroll.setIsSalaryIncrease(ipayroll.getIsSalaryIncrease());
        newPayroll.setCustomerNo(ipayroll.getCustomerNo());
        if(isAdd) {
        	newPayroll.setReflectYearAndMonth(startDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
        }else {
        	newPayroll.setReflectYearAndMonth(ipayroll.getReflectYearAndMonth());	 
        }
        newPayroll.setCustomerName(ipayroll.getCustomerName());
        newPayroll.setAverageSalary(ipayroll.getAverageSalary());
        newPayroll.setAverageBonusPayoff(ipayroll.getAverageBonusPayoff());
        newPayroll.setAverageTraffic(ipayroll.getAverageTraffic());
        newPayroll.setAverageSocialInsurancePayoff(ipayroll.getAverageSocialInsurancePayoff());
        newPayroll.setAverageOther(ipayroll.getAverageOther());
        newPayroll.setAverageValue(ipayroll.getAverageValue());	  
        newPayroll.setIsWaiting(ipayroll.getIsWaiting());	
	    return newPayroll;

	}
	
	public static List<PayrollModel> reverseArray(List<PayrollModel> array) {
	    int left = 0;
	    int right = array.size() - 1;

	    while (left < right) {
	        // 获取首尾元素
	        PayrollModel temp = array.get(left);
	        // 交换元素
	        array.set(left, array.get(right));
	        array.set(right, temp);

	        // 移动指针
	        left++;
	        right--;
	    }
	    return array;
	}

	
	// 获取现场情况列表与员工工资列表整合到一起
	public List<PayrollModel> mergeAveragedList(String employeeNo, List<PayrollModel> array) {
	    // 现场情况列表
	    List<PayrollAveragedModel> averagedList = payrollMapper.getAveraged(employeeNo);
	    
	    if (averagedList.isEmpty()) {
	        return array; // 如果为空，直接返回输入的列表
	    }

	    // 定义输出的列表，初始为传入的 array
	    List<PayrollModel> mergeList = new ArrayList<>(array);

	    // 定义日期格式
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

	    // 遍历现场情况列表
	    for (PayrollModel payroll : mergeList) {
	    	String yearAndMonth = payroll.getYearAndMonth();
            if (yearAndMonth == null || yearAndMonth.isEmpty()) {
                continue; // 如果年份和月份为空，跳过当前记录
            }
            YearMonth mergeStartDate = YearMonth.parse(yearAndMonth, formatter);

	        // 遍历 mergeList
	        for (PayrollAveragedModel averaged: averagedList) {
	        	//现场开始时间
	        	String salaryStartYear = averaged.getSalaryStartYear();
	        	//现场结束时间
		        String salaryEndYear = averaged.getSalaryEndYear();
	        
		        // 获取当前日期的年份和上一个月份
			    YearMonth nearMonth = YearMonth.now().minusMonths(1);
		        
		        if (salaryStartYear == null || salaryEndYear.isEmpty()) {
		            continue; // 如果起始日期为空，跳过当前记录
		        }
		        if (salaryEndYear == null || salaryEndYear.isEmpty()) {
		        	salaryEndYear = nearMonth.format(formatter).toString();
		        }
		        
		        YearMonth startDate = YearMonth.parse(salaryStartYear, formatter);
		        YearMonth endDate = YearMonth.parse(salaryEndYear, formatter);

	            // 如果日期匹配，则更新对应字段
	            if (startDate.equals(mergeStartDate)) {
//		            System.out.println(mergeStartDate + " 开始时间" + startDate);
	                payroll.setAverageBonusPayoff(averaged.getBonusPayoffAmount());
	                payroll.setAverageTraffic(averaged.getTrafficPayoffAmount());
	                payroll.setAverageSocialInsurancePayoff(averaged.getSocialInsurancePayoffAmount());
	                payroll.setAverageOther(averaged.getOthersPayoffAmount());
	                payroll.setAverageValue(averaged.getTotalPayoffAmount());
	                continue;
	            }else if (endDate.equals(mergeStartDate)) {
//		            System.out.println(mergeStartDate + " 结束时间" + endDate);
	                payroll.setAverageBonusPayoff(averaged.getBonusPayoffAmount());
	                payroll.setAverageTraffic(averaged.getTrafficPayoffAmount());
	                payroll.setAverageSocialInsurancePayoff(averaged.getSocialInsurancePayoffAmount());
	                payroll.setAverageOther(averaged.getOthersPayoffAmount());
	                payroll.setAverageValue(averaged.getTotalPayoffAmount());
	                continue;
	            }else if (!mergeStartDate.isBefore(startDate) && !mergeStartDate.isAfter(endDate)) {
//		            System.out.println(mergeStartDate + " 在时间段内");
	                continue;
	            }
	        }
	    }

	    return mergeList;
	}
	
	/**
	 * 精算新加
	 * 
	 * @param PayrollAveragedModel
	 * @return
	 */
	public boolean insertAverage(PayrollAveragedModel payrollAveragedModel) {
		
		// 检查现有记录
        List<PayrollAveragedModel> averagedList = payrollMapper.getAveraged(payrollAveragedModel.getEmployeeNo());
        for (PayrollAveragedModel existingModel : averagedList) {
            // 如果找到匹配的记录，则更新
            if (existingModel.getEmployeeNo().equals(payrollAveragedModel.getEmployeeNo())
                && existingModel.getSalaryStartYear().equals(payrollAveragedModel.getSalaryStartYear())
                && existingModel.getSalaryEndYear().equals(payrollAveragedModel.getSalaryEndYear())) {
                updateAverage(payrollAveragedModel);
                return true;
            }
        }
		try {
			payrollMapper.insertAverage(payrollAveragedModel);
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 精算更改
	 * 
	 * @param PayrollAveragedModel
	 * @return
	 */
	public boolean updateAverage(PayrollAveragedModel payrollAveragedModel) {
		try {
			payrollMapper.updateAverage(payrollAveragedModel);
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 給料情報sendMapの作成
	 * 
	 * @param wagesInfoModel
	 * @return
	 */
	public HashMap<String, String> getSendMap(PayrollAveragedModel payrollAveragedModel) {
		HashMap<String, String> sendMap = new HashMap<String, String>();
		sendMap.put("employeeNo", payrollAveragedModel.getEmployeeNo());
		sendMap.put("salaryStartYear", payrollAveragedModel.getSalaryStartYear());
		sendMap.put("salaryEndYear", payrollAveragedModel.getSalaryEndYear());
		sendMap.put("salary", payrollAveragedModel.getSalary());
		sendMap.put("bonusPayoffAmount", payrollAveragedModel.getBonusPayoffAmount());
		sendMap.put("trafficPayoffAmount", payrollAveragedModel.getTrafficPayoffAmount());
		sendMap.put("socialInsurancePayoffAmount", payrollAveragedModel.getSocialInsurancePayoffAmount());
		sendMap.put("othersPayoffAmount", payrollAveragedModel.getOthersPayoffAmount());
		sendMap.put("totalPayoffAmount", payrollAveragedModel.getTotalPayoffAmount());
		
		sendMap.put("updateUser", payrollAveragedModel.getUpdateUser());
		
		return sendMap;
	}
}
