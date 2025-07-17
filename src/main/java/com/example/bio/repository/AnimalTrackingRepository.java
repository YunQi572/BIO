package com.example.bio.repository;

import com.example.bio.entity.AnimalTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalTrackingRepository extends JpaRepository<AnimalTracking, Long> {
    List<AnimalTracking> findBySpeciesAndSpeciesIdOrderByTimestampAsc(String species, String speciesId);

    List<AnimalTracking> findBySpeciesAndSpeciesIdAndAnimalIdOrderByTimestampAsc(String species, String speciesId,
            String animalId);

    // 新增方法：直接根据animal_id查询轨迹数据
    List<AnimalTracking> findByAnimalIdOrderByTimestampAsc(String animalId);
}