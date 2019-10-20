/**
 * 
 */
package com.hanson.jbpm.dev.action;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.util.time.CurrentDateTime;

/**
 * @author tl
 *
 */
public class AttachAction {
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	
	
	/**
	 * ��Ӹ���д�봦�����
	 * @param userId
	 * @param taskId
	 * @param instId
	 * @param fileNames
	 * @param filePathes
	 */
	public void addAttachComments(String userId, String instId, String taskId, String[] fileNames, String[] filePathes) { 
		try {
			StringBuffer comments = new StringBuffer();
			StringBuffer imgComments = new StringBuffer("<span class=\"comments-img-bar\">");
			for(int i=0;i<fileNames.length;i++) {
				String fileName = fileNames[i];
				String filePath = filePathes[i];
				if(isImage(fileName)) {
					imgComments.append("<img src=\"/jbpm/template/ShowImage.jsp?path=").append(filePath)
							.append("\" alt=\"���Ԥ����ͼƬ\" onclick=window.open(\"/jbpm/template/ShowImage.jsp?path=")
							.append(filePath).append("\") class=\"comments-img\">");
				}else {
					//��ͨ����
					comments.append("�ϴ����� ��<a target=\"hiddenFrame\" title=\"").append("������ظø���")
						.append("\" style=\"color:blue;text-decoration:underline\" href=\"/jbpm/action/DownloadAttach.jsp?instId=")
						.append(instId).append("&fileName=").append(fileName).append("&path=")
						.append(filePath).append("\" >").append(fileName).append("</a>�� \n");
				}
			}
			comments.insert(0, imgComments.append("</span>\n"));
			CommonLogger.logger.debug("��ʼ��Ӹ����������..." );
			String sql = new RecordTaskTrackAction().getExecuteSql(userId, instId, taskId, "�����޸�", 
					comments.toString(), "", CurrentDateTime.getCurrentDateTime(), isHis(instId));
			CommonLogger.logger.debug("��Ӹ���������̣�" + sql);
			jdbc.execute(sql);
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
		
	}
	
	
	public String isHis(String instId) {
		String sql = "select count(1) from BPM_INST_HIS where INST_ID=?";
		int count = jdbc.queryForInt(sql, new String[]{instId});
		if(count > 0) {
			return "_HIS";
		}else {
			return "";
		}
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isImage(String fileName) {
		if(fileName.length() > 4) {
			String suffix = fileName.substring(fileName.length()-4, fileName.length());
			if(".jpg.png.bmp.gif".indexOf(suffix.toLowerCase()) >= 0) {
				return true;
			}
		}
		return false;
	}

}
