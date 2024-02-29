package com.rewe.kafka.repository;

import com.rewe.kafka.domain.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface EmailRepository
        extends JpaRepository<EmailModel, Long> {
    List<EmailModel> findAllByTopic(String topic );
}
