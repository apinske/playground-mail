package eu.pinske.playground.playground.mail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.swing.JFrame;

import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.samples.tree.MessageTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.dsl.ImapIdleChannelAdapterSpec;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.icegreen.greenmail.spring.GreenMailBean;

@SpringBootApplication
@EnableConfigurationProperties(MailProperties.class)
public class PlaygroundMailApplication {

	public static void main(String[] args) throws Exception {
		if (args.length == 2 && "show".equals(args[0])) {
			visualizeMessage(new FileInputStream(args[1]));
		} else {
			SpringApplication.run(PlaygroundMailApplication.class, args);
		}
	}

	@Bean
	public GreenMailBean greenMail(@Value("${greenmail.users}") String[] users) {
		GreenMailBean mail = new GreenMailBean();
		mail.setImapProtocol(true);
		mail.setPop3Protocol(false);
		mail.setUsers(Arrays.asList(users));
		return mail;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		return executor;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(5);
		return scheduler;
	}

	@Bean
	public Session mailSession(MailProperties props, TaskExecutor taskExecutor) {
		Properties properties = new Properties();
		properties.putAll(props.getProperties());
		properties.put("mail.event.executor", taskExecutor);
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getDefaultUserName(),
						properties.getProperty("mail." + getRequestingProtocol() + ".password"));
			}
		});
		return session;
	}

	@Bean
	public JavaMailSender mailSender(Session mailSession) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setSession(mailSession);
		return mailSender;
	}

	@Bean
	public IntegrationFlow mailFlow(Session mailSession, TaskExecutor taskExecutor, @Value("${mail.url}") String url) {
		ImapMailReceiver mailReceiver = new ImapMailReceiver(url);
		mailReceiver.setSession(mailSession);
		mailReceiver.setSimpleContent(true);
		mailReceiver.setShouldDeleteMessages(false);
		mailReceiver.setCancelIdleInterval(29 * 60);
		mailReceiver.setUserFlag("PLGR");
		ImapIdleChannelAdapterSpec mailChannel = Mail.imapIdleAdapter(mailReceiver).sendingTaskExecutor(taskExecutor);
		return IntegrationFlows.from(mailChannel).handle("mailService", "handleMessage").get();
	}

	private static void visualizeMessage(InputStream messageStream) throws IOException {
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
