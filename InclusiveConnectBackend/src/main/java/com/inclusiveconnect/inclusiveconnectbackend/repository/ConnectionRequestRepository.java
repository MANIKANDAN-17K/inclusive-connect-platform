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

    Optional<ConnectionRequest> findByIdAndSender_Id(Long requestId, Long senderId);

    List<ConnectionRequest> findByReceiver_IdAndStatus(Long receiverId, ConnectionRequest.ConnectionStatus status);

    List<ConnectionRequest> findBySender_IdAndStatus(Long senderId, ConnectionRequest.ConnectionStatus status);

    @Query("""
            SELECT cr FROM ConnectionRequest cr
            WHERE cr.status = 'ACCEPTED'
            AND (cr.sender.id = :userId OR cr.receiver.id = :userId)
            """)
    List<ConnectionRequest> findAcceptedConnectionsForUser(@Param("userId") Long userId);

    /**
     * True if any request (in either direction) is PENDING between these two users.
     */
    @Query("""
            SELECT COUNT(cr) > 0 FROM ConnectionRequest cr
            WHERE cr.status = 'PENDING'
            AND (
                (cr.sender.id = :userA AND cr.receiver.id = :userB)
             OR (cr.sender.id = :userB AND cr.receiver.id = :userA)
            )
            """)
    boolean hasPendingBetween(@Param("userA") Long userA, @Param("userB") Long userB);

    /**
     * True if an ACCEPTED connection exists between two users (in either
     * direction).
     */
    @Query("""
            SELECT COUNT(cr) > 0 FROM ConnectionRequest cr
            WHERE cr.status = 'ACCEPTED'
            AND (
                (cr.sender.id = :userA AND cr.receiver.id = :userB)
             OR (cr.sender.id = :userB AND cr.receiver.id = :userA)
            )
            """)
    boolean hasAcceptedBetween(@Param("userA") Long userA, @Param("userB") Long userB);

    /** Find the accepted connection between two users so it can be deleted. */
    @Query("""
            SELECT cr FROM ConnectionRequest cr
            WHERE cr.status = 'ACCEPTED'
            AND (
                (cr.sender.id = :userA AND cr.receiver.id = :userB)
             OR (cr.sender.id = :userB AND cr.receiver.id = :userA)
            )
            """)
    Optional<ConnectionRequest> findAcceptedBetween(@Param("userA") Long userA, @Param("userB") Long userB);
}