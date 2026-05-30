package com.anouar.elearning.notification.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "notification_settings", uniqueConstraints = @UniqueConstraint(columnNames = "userId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private boolean enableEmail;

    @Column(nullable = false)
    private boolean enableInApp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;

    @Column(nullable = false)
    private Instant updatedAt;
}
