package eu.pinske.playground.playground.mail.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailService {
	private static final Logger logger = LoggerFactory.getLogger(MailService.class); 

	@Autowired
	private JavaMailSender mailSender;

	private List<String> mails = Collections.synchronizedList(new ArrayList<>());

	public List<String> getMails() {
		return new ArrayList<>(mails);
	}

	public void handleMessage(MimeMessage message) {
		try {
			message.writeTo(System.out);
			logger.info("got mail {}", message.getSubject());
			mails.add(message.getSubject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMail(String from, String to, String subject, String text) {
		try {
			MimeMessageHelper mail = new MimeMessageHelper(mailSender.createMimeMessage(), true);
			mail.setFrom(from);
			mail.setTo(to);
			mail.setSubject(subject);
			mail.setText(text);
			// mail.addAttachment("data.bin", new InputStreamDataSource(data.get()));
			mailSender.send(mail.getMimeMessage());
		} catch (Exception e) {
			ExceptionUtils.rethrow(e);
		}
	}
}
