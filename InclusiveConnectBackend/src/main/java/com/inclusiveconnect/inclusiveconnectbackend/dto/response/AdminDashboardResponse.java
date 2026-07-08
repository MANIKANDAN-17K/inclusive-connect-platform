package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalCandidates;
    private long totalEmployers;
    private long totalJobs;
    private long pendingEmployerVerifications;
}