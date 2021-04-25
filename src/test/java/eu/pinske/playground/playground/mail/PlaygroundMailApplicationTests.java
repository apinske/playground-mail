package eu.pinske.playground.playground.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.mail.Session;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class PlaygroundMailApplicationTests {
	private static final Logger logger = LoggerFactory.getLogger(PlaygroundMailApplicationTests.class);

	@Test
	void contextLoads(@Autowired WebApplicationContext ctx, @Autowired Session mailSession) throws Exception {
		MockMvc mvc = webAppContextSetup(ctx).build();
		logger.info("sending mail...");
		mvc.perform(post("/playground-api/mail").contentType(MediaType.APPLICATION_JSON)
				.content("{\"sender\":\"test@local\",\"subject\":\"test\"}")).andExpect(status().isOk());
		Thread.sleep(1000);
		assertThat(mvc.perform(get("/playground-api/mail")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString()).contains("test");
	}
}
