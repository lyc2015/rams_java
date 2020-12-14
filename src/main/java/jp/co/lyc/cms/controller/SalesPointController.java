package jp.co.lyc.cms.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Comparator;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.io.path.Path;

import edu.emory.mathcs.backport.java.util.Collections;
import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.BreakTimeModel;
import jp.co.lyc.cms.model.DutyRegistrationModel;
import jp.co.lyc.cms.model.EmployeeWorkTimeModel;
import jp.co.lyc.cms.service.DutyRegistrationService;
import jp.co.lyc.cms.util.UtilsController;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
@RequestMapping(value = "/SalesPointController")
public class SalesPointController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 */
	@RequestMapping(value = "/downloadPDF", method = RequestMethod.POST)
	@ResponseBody
	public String downloadPDF(@RequestBody String fileName) {
		logger.info("SalesPointController.downloadPDF:" + "開始");
		File nowFile = new File(".").getAbsoluteFile();
		File inputFile = new File(nowFile.getParentFile(), "src/main/resources/PDFTemplate/salesPoint.jrxml");
		File outputFile = new File(UtilsController.DOWNLOAD_PATH_BASE + "dutyReport/", fileName + ".pdf");
		outputFile.getParentFile().mkdirs();
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			JasperReport report = JasperCompileManager.compileReport(inputFile.getAbsolutePath());
			JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());
		} catch (JRException e) {
			logger.error(e.getMessage());
		}
		logger.info("SalesPointController.downloadPDF:" + "終了");
		return outputFile.getAbsolutePath();
	}
}
