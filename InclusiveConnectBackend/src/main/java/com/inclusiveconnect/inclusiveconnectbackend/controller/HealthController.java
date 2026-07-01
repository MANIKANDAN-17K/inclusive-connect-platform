package com.inclusiveconnect.inclusiveconnectbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Service health check")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("Inclusive Connect API is up", Map.of("status", "UP"));
    }
}