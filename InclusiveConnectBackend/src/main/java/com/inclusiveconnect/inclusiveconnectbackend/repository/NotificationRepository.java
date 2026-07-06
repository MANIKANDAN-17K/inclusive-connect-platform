package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Notification> findByIdAndUser_Id(Long id, Long userId);
    long countByUser_IdAndIsReadFalse(Long userId);
}