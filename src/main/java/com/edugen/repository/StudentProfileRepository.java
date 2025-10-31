package com.edugen.repository;

import com.edugen.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
    Optional<StudentProfile> findByUserId(UUID userId);
    List<StudentProfile> findBySchoolId(UUID schoolId);
    List<StudentProfile> findByClassSection(String classSection);
}
