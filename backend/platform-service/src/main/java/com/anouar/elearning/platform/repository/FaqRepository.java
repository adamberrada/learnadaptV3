package com.anouar.elearning.platform.repository;

import com.anouar.elearning.platform.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    @Query("select f from Faq f where f.isPublished = true order by f.displayOrder asc, f.id asc")
    List<Faq> findPublishedOrdered();
}
