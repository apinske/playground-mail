package eu.pinske.playground.playground.mail.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.pinske.playground.playground.mail.model.Data;

public interface DataRepository extends JpaRepository<Data, Long> {
}
