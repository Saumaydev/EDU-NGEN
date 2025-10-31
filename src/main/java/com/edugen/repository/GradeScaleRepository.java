package com.edugen.repository;

import com.edugen.entity.GradeScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeScaleRepository extends JpaRepository<GradeScale, UUID> {
    List<GradeScale> findBySchoolIdOrderByMinPercentageDesc(UUID schoolId);
    Optional<GradeScale> findBySchoolIdAndMinPercentageLessThanEqualAndMaxPercentageGreaterThanEqual(
            UUID schoolId, BigDecimal percentage, BigDecimal percentage2);
}
