package eu.pinske.playground.playground.mail.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.pinske.playground.playground.mail.model.Mail;
import eu.pinske.playground.playground.mail.service.MailService;
import eu.pinske.playground.web.api.MailApi;
import eu.pinske.playground.web.api.model.MailDto;

@Controller
@RequestMapping("/playground-api")
public class MailApiController implements MailApi {

	@Autowired
	private MailService mailService;

	@Override
	public ResponseEntity<List<MailDto>> getMails(String sender) {
		List<Mail> mails = mailService.getMails();
		List<MailDto> mailDtos = new ArrayList<>(mails.size());
		for (Mail mail : mails) {
			MailDto mailDto = new MailDto();
			mailDto.setId(mail.getUuid());
			mailDto.setSender(mail.getSender());
			mailDto.setRecipient(mail.getRecipient());
			mailDto.setSubject(mail.getSubject());
			mailDto.setStatus(mail.getStatus());
			mailDtos.add(mailDto);
		}
		return ResponseEntity.ok(mailDtos);
	}

	@Override
	public ResponseEntity<Resource> getMailReport(String id) {
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("message/rfc822"))
				.body(new ByteArrayResource(mailService.getMailReportData(id)));
	}

	@Override
	public ResponseEntity<Void> sendMail(MailDto mailDto) {
		mailService.sendMail(mailDto.getSender(), mailDto.getRecipient(), mailDto.getSubject(),
				"This is a test mail to determine what kind of Delivery Status Notification (DSN) your mail server will send.");
		return ResponseEntity.ok().build();
	}
}
