package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
            SELECT c FROM Conversation c
            JOIN c.participants p1
            JOIN c.participants p2
            WHERE p1.id = :userAId AND p2.id = :userBId
            """)
    Optional<Conversation> findConversationBetween(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

    @Query("""
            SELECT c FROM Conversation c
            JOIN c.participants p
            WHERE p.id = :userId
            """)
    List<Conversation> findAllForUser(@Param("userId") Long userId);
}