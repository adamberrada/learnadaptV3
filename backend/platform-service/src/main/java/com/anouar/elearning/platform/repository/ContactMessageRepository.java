package com.anouar.elearning.platform.repository;

import com.anouar.elearning.platform.entity.ContactMessage;
import com.anouar.elearning.platform.entity.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    List<ContactMessage> findByStatusOrderByCreatedAtDesc(ContactStatus status);
}
