package com.anouar.elearning.platform.repository;

import com.anouar.elearning.platform.entity.LegalContent;
import com.anouar.elearning.platform.entity.LegalContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LegalContentRepository extends JpaRepository<LegalContent, Long> {

    @Query("""
            select lc from LegalContent lc
            where lc.contentType = :contentType and lc.isActive = true
            order by lc.version desc
            """)
    List<LegalContent> findActiveByTypeOrderByVersionDesc(@Param("contentType") LegalContentType contentType);

    @Query("select lc from LegalContent lc where lc.contentType = :contentType and lc.isActive = true")
    List<LegalContent> findActiveVersionsByType(@Param("contentType") LegalContentType contentType);

    Optional<LegalContent> findFirstByContentTypeOrderByVersionDesc(LegalContentType contentType);
}
