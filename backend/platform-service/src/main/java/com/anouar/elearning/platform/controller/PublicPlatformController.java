package com.anouar.elearning.platform.controller;

import com.anouar.elearning.platform.dto.ApiResponse;
import com.anouar.elearning.platform.dto.ContactMessageDTO;
import com.anouar.elearning.platform.dto.ContactRequestDTO;
import com.anouar.elearning.platform.dto.FaqDTO;
import com.anouar.elearning.platform.dto.LegalContentDTO;
import com.anouar.elearning.platform.entity.LegalContentType;
import com.anouar.elearning.platform.service.ContactMessageService;
import com.anouar.elearning.platform.service.FaqService;
import com.anouar.elearning.platform.service.LegalContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/platform")
public class PublicPlatformController {

    private final FaqService faqService;
    private final LegalContentService legalContentService;
    private final ContactMessageService contactMessageService;

    public PublicPlatformController(FaqService faqService,
                                    LegalContentService legalContentService,
                                    ContactMessageService contactMessageService) {
        this.faqService = faqService;
        this.legalContentService = legalContentService;
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/faq")
    public ResponseEntity<ApiResponse<List<FaqDTO>>> listPublishedFaq() {
        return ResponseEntity.ok(ApiResponse.success(
                "FAQ publique recuperee avec succes.",
                faqService.findPublished()
        ));
    }

    @GetMapping("/legal/{type}")
    public ResponseEntity<ApiResponse<LegalContentDTO>> getActiveLegalContent(@PathVariable LegalContentType type) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contenu informatif recupere avec succes.",
                legalContentService.findActiveByType(type)
        ));
    }

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<ContactMessageDTO>> submitContactMessage(
            @Valid @RequestBody ContactRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Message de support envoye avec succes.",
                contactMessageService.submit(request)
        ));
    }
}
