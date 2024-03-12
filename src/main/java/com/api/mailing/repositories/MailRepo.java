package com.api.mailing.repositories;

import com.api.mailing.entities.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepo extends JpaRepository<Mail, Long> {
}
