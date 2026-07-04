package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status; // PENDING | SHORTLISTED | REJECTED | HIRED
}