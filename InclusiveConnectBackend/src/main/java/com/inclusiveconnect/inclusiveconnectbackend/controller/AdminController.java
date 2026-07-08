package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.*;
import com.inclusiveconnect.inclusiveconnectbackend.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin-only dashboard, user management, employer verification")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", adminService.getDashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", adminService.getAllUsers()));
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<AdminUserResponse>> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User blocked", adminService.setUserBlocked(id, true)));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<AdminUserResponse>> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User unblocked", adminService.setUserBlocked(id, false)));
    }

    @GetMapping("/employers/pending")
    public ResponseEntity<ApiResponse<List<AdminCompanyResponse>>> getPendingEmployers() {
        return ResponseEntity.ok(ApiResponse.success("Pending employers fetched", adminService.getPendingEmployers()));
    }

    @PatchMapping("/employers/{id}/verify")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> verifyEmployer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employer verified", adminService.verifyEmployer(id)));
    }
}