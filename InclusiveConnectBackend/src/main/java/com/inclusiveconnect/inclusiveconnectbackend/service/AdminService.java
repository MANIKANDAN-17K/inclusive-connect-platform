package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminCompanyResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminDashboardResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminUserResponse;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboard();
    List<AdminUserResponse> getAllUsers();
    AdminUserResponse setUserBlocked(Long userId, boolean blocked);
    List<AdminCompanyResponse> getPendingEmployers();
    AdminCompanyResponse verifyEmployer(Long companyId);
}