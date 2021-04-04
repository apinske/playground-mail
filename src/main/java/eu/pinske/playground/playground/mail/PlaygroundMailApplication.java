package eu.pinske.playground.playground.mail;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.swing.JFrame;

import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.samples.tree.MessageTree;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.messaging.MessageHeaders;

import com.icegreen.greenmail.spring.GreenMailBean;

@SpringBootApplication
public class PlaygroundMailApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaygroundMailApplication.class, args);
	}
	
	@Bean
	public GreenMailBean greenMail() {
		GreenMailBean mail = new GreenMailBean();
		mail.setImapProtocol(true);
		mail.setPop3Protocol(true);
		mail.setUsers(Arrays.asList("test:test@local"));
		return mail;
	}

	@Bean
	public IntegrationFlow mailFlow(MailProperties props) {
		return IntegrationFlows.from(Mail.imapIdleAdapter("imap:INBOX").cancelIdleInterval(29 * 60)
				.javaMailProperties(asProperties(props.getProperties())).javaMailAuthenticator(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(props.getUsername(), props.getPassword());
					}
				})).handle(new GenericHandler<MimeMessage>() {
					@Override
					public Object handle(MimeMessage payload, MessageHeaders headers) {
						try {
							payload.writeTo(System.out);
							if (!GraphicsEnvironment.isHeadless()) {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								payload.writeTo(baos);
								visualizeMessage(new ByteArrayInputStream(baos.toByteArray()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				}).get();
	}

	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}

	private void visualizeMessage(InputStream messageStream) throws IOException {
		org.apache.james.mime4j.dom.Message message = new DefaultMessageBuilder().parseMessage(messageStream);
		javax.swing.SwingUtilities.invokeLater(() -> {
			MessageTree messageTree = new MessageTree(message);
			messageTree.setOpaque(true);
			JFrame frame = new JFrame("MessageTree");
			frame.setContentPane(messageTree);
			frame.pack();
			frame.setVisible(true);
		});
	}
}
