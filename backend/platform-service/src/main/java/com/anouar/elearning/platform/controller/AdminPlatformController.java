package com.anouar.elearning.platform.controller;

import com.anouar.elearning.platform.dto.ApiResponse;
import com.anouar.elearning.platform.dto.ContactMessageDTO;
import com.anouar.elearning.platform.dto.FaqDTO;
import com.anouar.elearning.platform.dto.LegalContentDTO;
import com.anouar.elearning.platform.dto.PlatformConfigDTO;
import com.anouar.elearning.platform.dto.ResolveContactDTO;
import com.anouar.elearning.platform.entity.ContactStatus;
import com.anouar.elearning.platform.service.ContactMessageService;
import com.anouar.elearning.platform.service.FaqService;
import com.anouar.elearning.platform.service.LegalContentService;
import com.anouar.elearning.platform.service.PlatformConfigurationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/platform")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlatformController {

    private final FaqService faqService;
    private final LegalContentService legalContentService;
    private final ContactMessageService contactMessageService;
    private final PlatformConfigurationService configurationService;

    public AdminPlatformController(FaqService faqService,
                                   LegalContentService legalContentService,
                                   ContactMessageService contactMessageService,
                                   PlatformConfigurationService configurationService) {
        this.faqService = faqService;
        this.legalContentService = legalContentService;
        this.contactMessageService = contactMessageService;
        this.configurationService = configurationService;
    }

    @PostMapping("/faq")
    public ResponseEntity<ApiResponse<FaqDTO>> createFaq(@Valid @RequestBody FaqDTO request) {
        return ResponseEntity.ok(ApiResponse.success("FAQ creee avec succes.", faqService.create(request)));
    }

    @PutMapping("/faq/{id}")
    public ResponseEntity<ApiResponse<FaqDTO>> updateFaq(@PathVariable Long id, @Valid @RequestBody FaqDTO request) {
        return ResponseEntity.ok(ApiResponse.success("FAQ mise a jour avec succes.", faqService.update(id, request)));
    }

    @DeleteMapping("/faq/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaq(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("FAQ supprimee avec succes.", null));
    }

    @PutMapping("/legal")
    public ResponseEntity<ApiResponse<LegalContentDTO>> updateLegalContent(
            @Valid @RequestBody LegalContentDTO request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contenu legal publie avec succes.",
                legalContentService.publishNewVersion(request)
        ));
    }

    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<ContactMessageDTO>>> listContacts(
            @RequestParam(required = false) ContactStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Messages de contact recuperes avec succes.",
                contactMessageService.findAll(status)
        ));
    }

    @PutMapping("/contacts/{id}/resolve")
    public ResponseEntity<ApiResponse<ContactMessageDTO>> resolveContact(@PathVariable Long id,
                                                                         @Valid @RequestBody ResolveContactDTO request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Ticket de support cloture avec succes.",
                contactMessageService.resolve(id, request.adminNotes())
        ));
    }

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<PlatformConfigDTO>> getConfiguration() {
        return ResponseEntity.ok(ApiResponse.success(
                "Configuration systeme recuperee avec succes.",
                configurationService.getCurrentConfiguration()
        ));
    }

    @PutMapping("/config")
    public ResponseEntity<ApiResponse<PlatformConfigDTO>> updateConfiguration(
            @Valid @RequestBody PlatformConfigDTO request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Configuration systeme mise a jour avec succes.",
                configurationService.update(request)
        ));
    }
}
