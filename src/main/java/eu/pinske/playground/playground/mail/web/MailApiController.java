package eu.pinske.playground.playground.mail.web;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.pinske.playground.web.api.MailApi;
import eu.pinske.playground.web.api.model.MailDto;

@Controller
@RequestMapping("/playground-api")
public class MailApiController implements MailApi {


	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public ResponseEntity<List<MailDto>> getMails(String sender) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResponseEntity<Void> sendMail(MailDto mailDto) {
		try {
			MimeMessageHelper mail = new MimeMessageHelper(mailSender.createMimeMessage(), true);
			mail.setFrom(mailDto.getSender());
			mail.setTo("test@local");
			mail.setSubject(mailDto.getSubject());
			mail.setText("Hello!");
			//mail.addAttachment("data.bin", new InputStreamDataSource(data.get()));
			mailSender.send(mail.getMimeMessage());
		} catch (Exception e) {
			return ExceptionUtils.rethrow(e);
		}
		return ResponseEntity.ok().build();
	}
}
