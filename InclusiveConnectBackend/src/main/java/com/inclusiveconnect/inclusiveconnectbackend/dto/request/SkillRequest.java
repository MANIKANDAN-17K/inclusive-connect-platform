package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private String proficiencyLevel; // BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
}