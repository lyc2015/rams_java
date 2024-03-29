package jp.co.lyc.cms.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.ResumeModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.service.ResumeService;
import jp.co.lyc.cms.util.UtilsController;

@Controller
@RequestMapping(value = "/resume")
public class ResumeController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ResumeService resumeService;

	@Autowired
	S3Controller s3Controller;

	/**
	 * 検索
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectResume", method = RequestMethod.POST)
	@ResponseBody
	public List<ResumeModel> selectResume(ResumeModel resumeModel) {
		resumeModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		logger.info("ResumeController.selectResume:" + "検索開始");
		List<ResumeModel> returnList = new ArrayList<ResumeModel>();
		ResumeModel checkMod = resumeService.selectResume(resumeModel);
		ResumeModel tempMod1 = new ResumeModel();
		ResumeModel tempMod2 = new ResumeModel();
		tempMod1.setRowNo(1);
		tempMod1.setResumeInfo(checkMod.getResumeInfo1());
		tempMod1.setResumeName(checkMod.getResumeName1());
		tempMod1.setUpdateUser(checkMod.getUpdateUser());
		tempMod1.setUpdateTime(checkMod.getUpdateTime());
		tempMod2.setRowNo(2);
		tempMod2.setResumeInfo(checkMod.getResumeInfo2());
		tempMod2.setResumeName(checkMod.getResumeName2());
		tempMod2.setUpdateUser(checkMod.getUpdateUser());
		tempMod2.setUpdateTime(checkMod.getUpdateTime());

		returnList.add(tempMod1);
		returnList.add(tempMod2);
		logger.info("ResumeController.selectResume:" + "検索終了");
		return returnList;
	}

	@RequestMapping(value = "/selectEmployeeName", method = RequestMethod.POST)
	@ResponseBody
	public ResumeModel selectEmployeeName(ResumeModel resumeModel) {
		logger.info("CostRegistrationController.selectEmployeeName:" + "検索開始");
		String employeeName;
		employeeName = resumeService.selectEmployeeName(getSession().getAttribute("employeeNo").toString());
		resumeModel.setEmployeeName(employeeName);
		logger.info("CostRegistrationController.selectEmployeeName:" + "検索終了");
		return resumeModel;
	}

	/**
	 * アップデート
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public final static String UPLOAD_PATH_PREFIX_resumeInfo = "c:/file/履歴書/";

	@RequestMapping(value = "/insertResume", method = RequestMethod.POST)
	@ResponseBody
	public boolean insertResume(@RequestParam(value = "emp", required = false) String JSONEmp,
			@RequestParam(value = "filePath1", required = false) MultipartFile filePath1,
			@RequestParam(value = "filePath2", required = false) MultipartFile filePath2) throws Exception {
		// resumeInfo 旧ファイルパス resumeNameファイル名 filePath新ファイルパス
		// ファイル退避(旧ファイルパス/XXX履歴.ｘxxForChangeName)→新ファイルパス存在→新ファイルアップロード,旧ファイルパス削除→新ファイルパス存在しない→改名→SQL実行
		logger.info("ResumeController.insertResume:" + "追加開始");
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		ResumeModel resumeModel = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<ResumeModel>() {
		});
		resumeModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		resumeModel.setEmployeeName(getSession().getAttribute("employeeName").toString());
		String newFile1 = "";
		String newFile2 = "";
		S3Model s3Model = new S3Model();
		// file存在チェック
		String lv1Path = UPLOAD_PATH_PREFIX_resumeInfo + resumeModel.getEmployeeNo() + "_"
				+ resumeModel.getEmployeeName();
		File lv1File = new File(lv1Path);
		if (!lv1File.isDirectory()) {
			lv1File.mkdirs();
		}
		//
		if (filePath1 != null) {
			String resumeInfo = resumeModel.getResumeInfo1();
			String originalFilename = filePath1.getOriginalFilename();
			String filePath = new String(
					UPLOAD_PATH_PREFIX_resumeInfo + resumeModel.getEmployeeNo() + "_" + resumeModel.getEmployeeName())
					+ "/" + resumeModel.getResumeName1()
					+ originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
			newFile1 = filePath;
			filePath1.transferTo(new File(filePath));
			if (resumeInfo != null && resumeInfo != "") {
				if (resumeInfo.indexOf("/file/") != -1) {
					String deletefileKey = resumeInfo.split("/file/")[1];
					s3Model.setFileKey(deletefileKey);
					s3Controller.deleteFile(s3Model);
				}
				String fileKey = filePath.split("file/")[1];
				s3Model.setFileKey(fileKey);
				s3Model.setFilePath(filePath);
				s3Controller.uploadFile(s3Model);
			}
		}
		if (filePath2 != null) {
			String resumeInfo = resumeModel.getResumeInfo2();
			String originalFilename = filePath2.getOriginalFilename();
			String filePath = new String(
					UPLOAD_PATH_PREFIX_resumeInfo + resumeModel.getEmployeeNo() + "_" + resumeModel.getEmployeeName())
					+ "/" + resumeModel.getResumeName2()
					+ originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
			newFile2 = filePath;
			filePath2.transferTo(new File(filePath));
			if (resumeInfo != null && resumeInfo != "") {
				if (resumeInfo.indexOf("/file/") != -1) {
					String deletefileKey = resumeInfo.split("/file/")[1];
					s3Model.setFileKey(deletefileKey);
					s3Controller.deleteFile(s3Model);
				}
				String fileKey = filePath.split("file/")[1];
				s3Model.setFileKey(fileKey);
				s3Model.setFilePath(filePath);
				s3Controller.uploadFile(s3Model);
			}
		}
		// SQL実行
		if (!newFile1.equals("")) {
			resumeModel.setResumeInfo1(newFile1);
		} else {
			resumeModel.setResumeInfo1(null);
		}
		if (!newFile2.equals("")) {
			resumeModel.setResumeInfo2(newFile2);
		} else {
			resumeModel.setResumeInfo2(null);
		}
		boolean result = resumeService.insertResume(resumeModel);
		logger.info("ResumeController.insertResume:" + "追加結束");
		return result;
	}

	// ファイルリネーム
	private String rename(ResumeModel resumeModel, boolean flag, int fileNo) {
		String returnStr = "";
		if (flag) { // 旧ファイルを一時退避
			if (fileNo == 1) {
				String oldPath = new String(resumeModel.getResumeInfo1());
				File oldFile = new File(oldPath);
				try {
					oldFile.renameTo(new File(oldPath + "ForChangeName"));
				} catch (Exception e) {
					e.printStackTrace();
					return returnStr;
				}
			} else if (fileNo == 2) {
				String oldPath = new String(resumeModel.getResumeInfo2());
				File oldFile = new File(oldPath);
				try {
					oldFile.renameTo(new File(oldPath + "ForChangeName"));
				} catch (Exception e) {
					e.printStackTrace();
					return returnStr;
				}
			}
		} else { // ファイル変更なし、フロントの名前にする
			String realPath = new String(
					UPLOAD_PATH_PREFIX_resumeInfo + resumeModel.getEmployeeNo() + "_" + resumeModel.getEmployeeName());
			if (fileNo == 1) {
				String suffix = resumeModel.getResumeInfo1()
						.substring(resumeModel.getResumeInfo1().lastIndexOf(".") + 1);
				String newName = resumeModel.getResumeName1() + "." + suffix;
				String oldPath = new String(resumeModel.getResumeInfo1() + "ForChangeName");
				File oldFile = new File(oldPath);
				try {
					oldFile.renameTo(new File(realPath + "/" + newName));
					returnStr = realPath + "/" + newName;
				} catch (Exception e) {
					e.printStackTrace();
					return returnStr;
				}
			} else if (fileNo == 2) {
				String suffix = resumeModel.getResumeInfo2()
						.substring(resumeModel.getResumeInfo2().lastIndexOf(".") + 1);
				String newName = resumeModel.getResumeName2() + "." + suffix;
				String oldPath = new String(resumeModel.getResumeInfo2() + "ForChangeName");
				File oldFile = new File(oldPath);
				try {
					oldFile.renameTo(new File(realPath + "/" + newName));
					returnStr = realPath + "/" + newName;
				} catch (Exception e) {
					e.printStackTrace();
					return returnStr;
				}
			}
		}
		return returnStr;
	}

	public String upload(String resumeName, MultipartFile resumeFile, String realPath) {
		File file = new File(realPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		String fileName = resumeFile.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		String newName = resumeName + "." + suffix;
		try {
			File newFile = new File(file.getAbsolutePath() + "/" + newName);
			resumeFile.transferTo(newFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return realPath + "/" + newName;
	}

	public boolean delete(ResumeModel resumeModel, int fileNo) {
		File file = null;
		boolean flag = false;
		if (fileNo == 1) {
			file = new File(resumeModel.getResumeInfo1() + "ForChangeName");
		} else if (fileNo == 2) {
			file = new File(resumeModel.getResumeInfo2() + "ForChangeName");
		}
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
}
