package eu.pinske.playground.playground.mail.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.pinske.playground.playground.mail.model.Mail;

public interface MailRepository extends JpaRepository<Mail, String> {
}
