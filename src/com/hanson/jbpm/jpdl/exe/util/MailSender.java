package com.hanson.jbpm.jpdl.exe.util;


import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.hanson.jbpm.log.CommonLogger;


public class MailSender {
	private String host = "xx"; 	// smtp������
	private String user = "xx"; 	// �û���
	private String pwd 	= "xx"; 	// ����
	private String from = "xx"; 	// �����˵�ַ

	public MailSender(String host) {
		this.host = host;
	}
	
	public void setFrom(String from, String pwd) {
		this.from = from;
		this.user = from.substring(0, from.indexOf("@"));
		this.pwd = pwd;
	}

	public void send(String to, String subject, String txt, String attachFilename) {
		
		Properties props = new Properties();
		// ���÷����ʼ����ʼ������������ԣ�����ʹ�����׵�smtp��������
		props.put("mail.smtp.host", host);
		// ��Ҫ������Ȩ��Ҳ�����л����������У�飬��������ͨ����֤��һ��Ҫ����һ����
		props.put("mail.smtp.auth", "true");
		// �øո����úõ�props���󹹽�һ��session
		Session session = Session.getDefaultInstance(props);
		// ������������ڷ����ʼ��Ĺ�������console����ʾ������Ϣ��������ʹ
		// �ã�������ڿ���̨��console)�Ͽ��������ʼ��Ĺ��̣�
		//session.setDebug(true);
		// ��sessionΪ����������Ϣ����
		MimeMessage message = new MimeMessage(session);
		try {
			// ���ط����˵�ַ
			message.setFrom(new InternetAddress(from));
			// �����ռ��˵�ַ
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// ���ر���
			message.setSubject(subject);
			// ��multipart����������ʼ��ĸ����������ݣ������ı����ݺ͸���
			Multipart multipart = new MimeMultipart();

			// �����ʼ����ı�����
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setText(txt);
			multipart.addBodyPart(contentPart);
			
			if (attachFilename != null) {
				MimeBodyPart bodyPart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(attachFilename);			
				bodyPart.setDataHandler(new DataHandler(fds));
				bodyPart.setFileName(MimeUtility.encodeText(fds.getName()));
				multipart.addBodyPart(bodyPart);								
			}
			message.setContent(multipart);
			// �����ʼ�
			message.saveChanges();
			// �����ʼ�
			Transport transport = session.getTransport("smtp");
			// ���ӷ�����������
			transport.connect(host, user, pwd);
			// ���ʼ����ͳ�ȥ
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
	}

	public static void main(String[] args) {
		System.setProperty("EAP_DEBUG", "true");
		System.setProperty("EAP_HOME", "f:/openeap318");
		MailSender cn = new MailSender("mail.suntektech.com");
		// ���÷����˵�ַ���ռ��˵�ַ���ʼ�����
		cn.setFrom("zt@suntektech.com", "eyx6nv"); 		
		cn.send("435437396@qq.com", "Դ����", "�ҾͲ����ļ������ˣ����ٶ���copy", null);
	}
}
