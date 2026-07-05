package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    Optional<ConnectionRequest> findBySender_IdAndReceiver_Id(Long senderId, Long receiverId);

    Optional<ConnectionRequest> findByIdAndReceiver_Id(Long requestId, Long receiverId);

    List<ConnectionRequest> findByReceiver_IdAndStatus(Long receiverId, ConnectionRequest.ConnectionStatus status);

    @Query("""
            SELECT cr FROM ConnectionRequest cr
            WHERE cr.status = 'ACCEPTED'
            AND (cr.sender.id = :userId OR cr.receiver.id = :userId)
            """)
    List<ConnectionRequest> findAcceptedConnectionsForUser(@Param("userId") Long userId);
}