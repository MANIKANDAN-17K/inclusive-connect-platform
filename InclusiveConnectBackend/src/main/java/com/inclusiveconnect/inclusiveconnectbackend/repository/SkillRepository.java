package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUser_Id(Long userId);
    Optional<Skill> findByIdAndUser_Id(Long id, Long userId);
    boolean existsByUser_IdAndSkillNameIgnoreCase(Long userId, String skillName);
}