package com.anouar.elearning.platform.service;

import com.anouar.elearning.platform.dto.ContactMessageDTO;
import com.anouar.elearning.platform.dto.ContactRequestDTO;
import com.anouar.elearning.platform.entity.ContactMessage;
import com.anouar.elearning.platform.entity.ContactStatus;
import com.anouar.elearning.platform.exception.ResourceNotFoundException;
import com.anouar.elearning.platform.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public ContactMessageDTO submit(ContactRequestDTO request) {
        ContactMessage message = ContactMessage.builder()
                .senderName(request.senderName())
                .senderEmail(request.senderEmail())
                .subject(request.subject())
                .message(request.message())
                .status(ContactStatus.PENDING)
                .build();
        return toDto(contactMessageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<ContactMessageDTO> findAll(ContactStatus status) {
        List<ContactMessage> messages = status == null
                ? contactMessageRepository.findAllByOrderByCreatedAtDesc()
                : contactMessageRepository.findByStatusOrderByCreatedAtDesc(status);
        return messages.stream().map(this::toDto).toList();
    }

    public ContactMessageDTO resolve(Long id, String adminNotes) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + id));
        message.setStatus(ContactStatus.RESOLVED);
        message.setAdminNotes(adminNotes);
        message.setResolvedAt(LocalDateTime.now());
        return toDto(message);
    }

    private ContactMessageDTO toDto(ContactMessage message) {
        return new ContactMessageDTO(
                message.getId(),
                message.getSenderName(),
                message.getSenderEmail(),
                message.getSubject(),
                message.getMessage(),
                message.getStatus(),
                message.getAdminNotes(),
                message.getCreatedAt(),
                message.getResolvedAt()
        );
    }
}
