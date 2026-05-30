package com.anouar.elearning.platform.service;

import com.anouar.elearning.platform.dto.LegalContentDTO;
import com.anouar.elearning.platform.entity.LegalContent;
import com.anouar.elearning.platform.entity.LegalContentType;
import com.anouar.elearning.platform.exception.ResourceNotFoundException;
import com.anouar.elearning.platform.repository.LegalContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LegalContentService {

    private final LegalContentRepository legalContentRepository;

    public LegalContentService(LegalContentRepository legalContentRepository) {
        this.legalContentRepository = legalContentRepository;
    }

    @Transactional(readOnly = true)
    public LegalContentDTO findActiveByType(LegalContentType type) {
        return legalContentRepository.findActiveByTypeOrderByVersionDesc(type)
                .stream()
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("No active legal content found for type: " + type));
    }

    public LegalContentDTO publishNewVersion(LegalContentDTO request) {
        legalContentRepository.findActiveVersionsByType(request.contentType())
                .forEach(activeContent -> activeContent.setActive(false));

        int nextVersion = legalContentRepository.findFirstByContentTypeOrderByVersionDesc(request.contentType())
                .map(content -> content.getVersion() + 1)
                .orElse(1);

        LegalContent content = LegalContent.builder()
                .contentType(request.contentType())
                .htmlContent(request.htmlContent())
                .version(nextVersion)
                .isActive(true)
                .build();

        return toDto(legalContentRepository.save(content));
    }

    private LegalContentDTO toDto(LegalContent content) {
        return new LegalContentDTO(
                content.getId(),
                content.getContentType(),
                content.getHtmlContent(),
                content.getVersion(),
                content.isActive(),
                content.getUpdatedAt()
        );
    }
}
