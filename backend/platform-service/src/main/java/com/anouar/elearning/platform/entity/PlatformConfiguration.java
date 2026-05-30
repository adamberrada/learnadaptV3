package com.anouar.elearning.platform.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "platform_configurations",
        uniqueConstraints = @UniqueConstraint(name = "uk_platform_configuration_singleton", columnNames = "singleton_key")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformConfiguration {

    public static final String GLOBAL_SINGLETON_KEY = "GLOBAL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "singleton_key", nullable = false, unique = true, updatable = false, length = 30)
    @Builder.Default
    private String singletonKey = GLOBAL_SINGLETON_KEY;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String siteName;

    @NotBlank
    @Email
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String supportEmail;

    @Column(nullable = false)
    private boolean maintenanceMode;

    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "platform_allowed_languages",
            joinColumns = @JoinColumn(name = "configuration_id")
    )
    @Column(name = "language_code", nullable = false, length = 12)
    @Builder.Default
    private List<@NotBlank @Size(max = 12) String> allowedLanguages = new ArrayList<>();

    @Min(1)
    @Max(1024)
    @Column(nullable = false)
    private int maxUploadSizeInMb;
}
