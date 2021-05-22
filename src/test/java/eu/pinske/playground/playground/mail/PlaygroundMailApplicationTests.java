package eu.pinske.playground.playground.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.icegreen.greenmail.spring.GreenMailBean;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserManager;
import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.MultipartReport;

@SpringBootTest("spring.profiles.active=test")
class PlaygroundMailApplicationTests {

	@Autowired
	private GreenMailBean greenMail;

	@Test
	void contextLoads(@Autowired WebApplicationContext ctx) throws Exception {
		UserManager userManager = greenMail.getUserManager();
		userManager.setMessageDeliveryHandler((msg, mailAddress) -> {
			String email = mailAddress.getEmail();
			GreenMailUser user = userManager.getUserByEmail(email);
			if (user == null) {
				user = userManager.getUserByEmail(msg.getReturnPath().getEmail());
				if (user != null) {
					MimeMessage dsnMessage = new MimeMessage(msg.getMessage().getSession());
					dsnMessage.setSubject("Delivery Status Report");
					DeliveryStatus dsn = new DeliveryStatus();
					dsn.setMessageDSN(new InternetHeaders());
					dsn.getMessageDSN().addHeader("Reporting-MTA", "greenmail.local");
					dsn.addRecipientDSN(new InternetHeaders());
					dsn.getRecipientDSN(0).addHeader("Final-Recipient", email);
					dsn.getRecipientDSN(0).addHeader("Action", "failed");
					dsn.getRecipientDSN(0).addHeader("Status", "5.1.1");
					dsnMessage.setContent(new MultipartReport("", dsn, msg.getMessage()));
					msg.setMimeMessage(dsnMessage);
				} else {
					user = userManager.createUser(email, email, email);
				}
			}
			return user;
		});

		MockMvc mvc = webAppContextSetup(ctx).build();
		mvc.perform(post("/playground-api/mail").contentType(MediaType.APPLICATION_JSON)
				.content("{\"sender\":\"test1@local\",\"recipient\":\"test2@local\",\"subject\":\"Test 1\"}"))
				.andExpect(status().isOk());
		Thread.sleep(2000);
		assertThat(mvc.perform(get("/playground-api/mail")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].status", containsString("5.1.1"))));
	}
}
