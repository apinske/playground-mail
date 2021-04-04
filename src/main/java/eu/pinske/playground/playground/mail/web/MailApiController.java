package eu.pinske.playground.playground.mail.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
		List<String> mails = mailService.getMails();
		List<MailDto> mailDtos = new ArrayList<>(mails.size());
		for (String mail : mails) {
			MailDto mailDto = new MailDto();
			mailDto.setSubject(mail);
			mailDtos.add(mailDto);
		}
		return ResponseEntity.ok(mailDtos);
	}

	@Override
	public ResponseEntity<Void> sendMail(MailDto mailDto) {
		mailService.sendMail(mailDto.getSender(), "test@local", mailDto.getSubject(), "Hello!");
		return ResponseEntity.ok().build();
	}
}
