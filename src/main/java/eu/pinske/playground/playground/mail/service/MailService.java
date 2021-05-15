package eu.pinske.playground.playground.mail.service;

import static java.util.Collections.list;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.MultipartReport;

import eu.pinske.playground.playground.mail.dao.DataRepository;
import eu.pinske.playground.playground.mail.dao.MailRepository;
import eu.pinske.playground.playground.mail.model.Data;
import eu.pinske.playground.playground.mail.model.Mail;

@Component
@Transactional
public class MailService {
	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private MailRepository mailDao;

	@Autowired
	private DataRepository dataDao;

	@Autowired
	private JavaMailSender mailSender;

	public List<Mail> getMails() {
		return mailDao.findAll();
	}
	
	public byte[] getMailReportData(String uuid) {
		return mailDao.findById(uuid).get().getReportData().getData();
	}

	public void handleMessage(MimeMessage message) throws Exception {
		ByteArrayOutputStream messageDataStream = new ByteArrayOutputStream();
		message.writeTo(messageDataStream);
		Data messageData = dataDao.save(new Data(messageDataStream.toByteArray()));

		log.info("got mail subject: {}", message.getSubject());
		Object content = message.getContent();
		if (content instanceof MultipartReport) {
			MultipartReport report = (MultipartReport) content;
			log.info("report.text: {}", report.getText());
			DeliveryStatus status = (DeliveryStatus) report.getReport();
			InternetHeaders recipientStatus = status.getRecipientDSN(0);
			log.info("report.status.recipient[0]: {}", list(recipientStatus.getAllHeaderLines()));

			MimeMessage returnedMessage = report.getReturnedMessage();
			String uuid = returnedMessage.getHeader("X-PLGR-ID", null);
			log.info("report.message: {}", uuid);
			Mail mail = mailDao.findById(uuid).get();
			mail.setReportData(messageData);
			mail.setStatus(recipientStatus.getHeader("Status", null));
		} else {
			Matcher m = Pattern
					.compile("X-PLGR-ID: ([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})")
					.matcher(new String(messageData.getData()));
			if (m.find()) {
				String uuid = m.group(1);
				log.info("text.message: {}", uuid);
				Mail mail = mailDao.findById(uuid).get();
				mail.setReportData(messageData);
				mail.setStatus("bounced");
			} else {
				log.warn("saved as {}", messageData);
			}
		}
	}

	public void sendMail(String from, String to, String subject, String text) {
		try {
			MimeMessageHelper message = new MimeMessageHelper(mailSender.createMimeMessage(), true);
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			String uuid = UUID.randomUUID().toString();
			Mail mail = new Mail();
			mail.setUuid(uuid);
			mail.setSender(from);
			mail.setRecipient(to);
			mail.setSubject(subject);
			mail.setStatus("sent");
			mailDao.save(mail);
			log.info("sending {}", mail);
			message.getMimeMessage().setHeader("X-PLGR-ID", uuid);
			mailSender.send(message.getMimeMessage());
		} catch (Exception e) {
			ExceptionUtils.rethrow(e);
		}
	}
}
