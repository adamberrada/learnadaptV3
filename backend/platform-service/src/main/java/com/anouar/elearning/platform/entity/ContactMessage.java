package com.anouar.elearning.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String senderName;

    @NotBlank
    @Email
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String senderEmail;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String subject;

    @NotBlank
    @Size(max = 5000)
    @Column(nullable = false, length = 5000)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactStatus status;

    @Size(max = 5000)
    @Column(length = 5000)
    private String adminNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = ContactStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }
}
