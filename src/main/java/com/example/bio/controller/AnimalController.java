package com.example.bio.controller;

import com.example.bio.entity.Animal;
import com.example.bio.entity.AnimalTracking;
import com.example.bio.repository.AnimalRepository;
import com.example.bio.repository.AnimalTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalTrackingRepository animalTrackingRepository;

    // 获取所有动物，按物种分组
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllAnimals() {
        List<Animal> animals = animalRepository.findAllGroupedBySpecies();
        Map<String, List<Animal>> groupedAnimals = animals.stream()
                .collect(Collectors.groupingBy(Animal::getSpeciesName));

        Map<String, Object> response = new HashMap<>();
        response.put("animals", groupedAnimals);
        response.put("total", animals.size());
        response.put("speciesCount", groupedAnimals.size());

        return ResponseEntity.ok(response);
    }

    // 获取物种统计信息
    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getStatistics() {
        List<Object[]> stats = animalRepository.getSpeciesStatistics();
        List<Map<String, Object>> result = stats.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("speciesName", row[0]);
                    stat.put("count", row[1]);
                    return stat;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 根据物种获取动物列表
    @GetMapping("/species/{speciesName}")
    public ResponseEntity<List<Animal>> getAnimalsBySpecies(@PathVariable String speciesName) {
        List<Animal> animals = animalRepository.findBySpeciesNameOrderByAnimalIdAsc(speciesName);
        return ResponseEntity.ok(animals);
    }

    // 根据动物ID获取详细信息
    @GetMapping("/{animalId}")
    public ResponseEntity<Map<String, Object>> getAnimalDetails(@PathVariable String animalId) {
        Optional<Animal> animalOpt = animalRepository.findByAnimalId(animalId);
        if (animalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Animal animal = animalOpt.get();

        // 直接使用animal_id查询轨迹数据，因为现在两个表使用统一的animal_id格式
        List<AnimalTracking> trackings = animalTrackingRepository
                .findByAnimalIdOrderByTimestampAsc(animalId);

        Map<String, Object> response = new HashMap<>();
        response.put("animal", animal);
        response.put("trackings", trackings);

        return ResponseEntity.ok(response);
    }

    // 获取患病动物
    @GetMapping("/sick")
    public ResponseEntity<List<Animal>> getSickAnimals() {
        List<Animal> sickAnimals = animalRepository.findByIsSickTrueOrderBySpeciesNameAsc();
        return ResponseEntity.ok(sickAnimals);
    }

    // 获取预警动物
    @GetMapping("/warning")
    public ResponseEntity<List<Animal>> getWarningAnimals() {
        List<Animal> warningAnimals = animalRepository.findByIsWarningTrueOrderBySpeciesNameAsc();
        return ResponseEntity.ok(warningAnimals);
    }

    // 创建或更新动物信息
    @PostMapping("")
    public ResponseEntity<Animal> createOrUpdateAnimal(@RequestBody Animal animal) {
        // 检查是否已存在该动物ID
        Optional<Animal> existing = animalRepository.findByAnimalId(animal.getAnimalId());
        if (existing.isPresent()) {
            // 更新现有动物信息
            Animal existingAnimal = existing.get();
            existingAnimal.setAnimalName(animal.getAnimalName());
            existingAnimal.setAge(animal.getAge());
            existingAnimal.setGender(animal.getGender());
            existingAnimal.setHealthStatus(animal.getHealthStatus());
            existingAnimal.setSick(animal.getSick());
            existingAnimal.setWarning(animal.getWarning());
            existingAnimal.setWeight(animal.getWeight());
            existingAnimal.setHabitat(animal.getHabitat());
            existingAnimal.setDescription(animal.getDescription());

            Animal updated = animalRepository.save(existingAnimal);
            return ResponseEntity.ok(updated);
        } else {
            // 创建新动物
            Animal created = animalRepository.save(animal);
            return ResponseEntity.ok(created);
        }
    }

    // 更新动物状态
    @PutMapping("/{animalId}/status")
    public ResponseEntity<Animal> updateAnimalStatus(
            @PathVariable String animalId,
            @RequestBody Map<String, Object> statusUpdate) {

        Optional<Animal> animalOpt = animalRepository.findByAnimalId(animalId);
        if (animalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Animal animal = animalOpt.get();

        if (statusUpdate.containsKey("isSick")) {
            animal.setSick((Boolean) statusUpdate.get("isSick"));
        }
        if (statusUpdate.containsKey("isWarning")) {
            animal.setWarning((Boolean) statusUpdate.get("isWarning"));
        }
        if (statusUpdate.containsKey("healthStatus")) {
            animal.setHealthStatus((String) statusUpdate.get("healthStatus"));
        }

        Animal updated = animalRepository.save(animal);
        return ResponseEntity.ok(updated);
    }
}