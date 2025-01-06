package jp.co.lyc.cms.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.lyc.cms.model.PayrollAveragedModel;
import jp.co.lyc.cms.model.PayrollModel;
import jp.co.lyc.cms.service.PayrollService;

@Controller
@RequestMapping(value = "/subMenu")
public class PayrollController {
	@Autowired
	PayrollService payrollService;
	/**
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	@RequestMapping(value = "/getPayroll", method = RequestMethod.POST)
	@ResponseBody
	public List<PayrollModel> getPayroll(@RequestBody PayrollModel payroll) throws CloneNotSupportedException {
		
		List<PayrollModel> result = payrollService.getPayroll(payroll.getEmployeeNo());
		
		return result;
	}
		
	/**
	 * 精算新加
	 * 
	 * @param PayrollAveragedModel
	 * @return
	 */
	@RequestMapping(value = "/insertAverage", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> insertAverage(@RequestBody PayrollAveragedModel PayrollAveragedModel) {
	    HashMap<String, Object> resultMap = new HashMap<>();
	    try {
	        payrollService.insertAverage(PayrollAveragedModel);
	        resultMap.put("status", "success");
	        resultMap.put("message", "数据插入成功");
	    } catch (Exception e) {
	        resultMap.put("status", "error");
	        resultMap.put("message", "数据插入失败: " + e.getMessage());
	    }
	    return resultMap;
	}
	/**
	 * 精算更改
	 * 
	 * @param PayrollAveragedModel
	 * @return
	 */
	@RequestMapping(value = "/updateAverage", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> updateAverage(@RequestBody PayrollAveragedModel PayrollAveragedModel) {
	    HashMap<String, Object> resultMap = new HashMap<>();
	    try {
	        payrollService.updateAverage(PayrollAveragedModel);
	        resultMap.put("status", "success");
	        resultMap.put("message", "数据插入成功");
	    } catch (Exception e) {
	        resultMap.put("status", "error");
	        resultMap.put("message", "数据插入失败: " + e.getMessage());
	    }
	    return resultMap;
	}
}
