package com.example.bio.repository;

import com.example.bio.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    // 根据物种名称查询所有个体
    List<Animal> findBySpeciesNameOrderByAnimalIdAsc(String speciesName);

    // 根据物种ID查询所有个体
    List<Animal> findBySpeciesIdOrderByAnimalIdAsc(String speciesId);

    // 根据动物ID查询个体
    Optional<Animal> findByAnimalId(String animalId);

    // 查询患病的动物
    List<Animal> findByIsSickTrueOrderBySpeciesNameAsc();

    // 查询有预警的动物
    List<Animal> findByIsWarningTrueOrderBySpeciesNameAsc();

    // 查询所有动物，按物种分组
    @Query("SELECT a FROM Animal a ORDER BY a.speciesName ASC, a.animalId ASC")
    List<Animal> findAllGroupedBySpecies();

    // 根据健康状态查询
    List<Animal> findByHealthStatusOrderBySpeciesNameAsc(String healthStatus);

    // 查询不同物种的数量统计
    @Query("SELECT a.speciesName, COUNT(a) FROM Animal a GROUP BY a.speciesName ORDER BY COUNT(a) DESC")
    List<Object[]> getSpeciesStatistics();
}