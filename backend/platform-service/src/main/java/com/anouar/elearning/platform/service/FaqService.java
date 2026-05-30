package com.anouar.elearning.platform.service;

import com.anouar.elearning.platform.dto.FaqDTO;
import com.anouar.elearning.platform.entity.Faq;
import com.anouar.elearning.platform.exception.ResourceNotFoundException;
import com.anouar.elearning.platform.repository.FaqRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Transactional(readOnly = true)
    public List<FaqDTO> findPublished() {
        return faqRepository.findPublishedOrdered()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public FaqDTO create(FaqDTO request) {
        Faq faq = Faq.builder()
                .question(request.question())
                .answer(request.answer())
                .category(request.category())
                .displayOrder(request.displayOrder() == null ? 0 : request.displayOrder())
                .isPublished(Boolean.TRUE.equals(request.isPublished()))
                .build();
        return toDto(faqRepository.save(faq));
    }

    public FaqDTO update(Long id, FaqDTO request) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category());
        faq.setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder());
        faq.setPublished(Boolean.TRUE.equals(request.isPublished()));
        return toDto(faq);
    }

    public void delete(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new ResourceNotFoundException("FAQ not found with id: " + id);
        }
        faqRepository.deleteById(id);
    }

    private FaqDTO toDto(Faq faq) {
        return new FaqDTO(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory(),
                faq.getDisplayOrder(),
                faq.isPublished(),
                faq.getCreatedAt(),
                faq.getUpdatedAt()
        );
    }
}
