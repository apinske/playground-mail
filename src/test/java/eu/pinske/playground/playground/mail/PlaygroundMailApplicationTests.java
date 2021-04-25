package eu.pinske.playground.playground.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
				.content("{\"sender\":\"sender@local\",\"recipient\":\"test2@local\",\"subject\":\"Test 1\"}"))
				.andExpect(status().isOk());
		Thread.sleep(1000);
		assertThat(mvc.perform(get("/playground-api/mail")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString()).containsIgnoringCase("Status");
	}
}
