package eu.pinske.playground.playground.mail;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class PlaygroundMailApplicationTests {

	@Test
	void contextLoads(@Autowired WebApplicationContext ctx) throws Exception {
		MockMvc mvc = webAppContextSetup(ctx).build();
		mvc.perform(post("/playground-api/mail").contentType(MediaType.APPLICATION_JSON)
				.content("{\"sender\":\"test@local\",\"subject\":\"test\"}")).andExpect(status().isOk());
		Thread.sleep(60000);
	}
}
