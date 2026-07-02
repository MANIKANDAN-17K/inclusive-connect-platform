package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}