package com.edugen.repository;

import com.edugen.entity.LeaderboardCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaderboardCacheRepository extends JpaRepository<LeaderboardCache, UUID> {
    List<LeaderboardCache> findBySchoolIdAndClassSectionAndRankingPeriodOrderByRankPosition(
            UUID schoolId, String classSection, LeaderboardCache.RankingPeriod period);
}
