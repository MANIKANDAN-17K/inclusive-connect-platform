package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ForgotPasswordRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.LoginRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.RegisterRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ResetPasswordRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(String token);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}