package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  List<User> findAllByOrderByCreatedAtDesc();

  long countByRole_Name(RoleName roleName);

  /**
   * Returns active, verified users that the current user has no relationship
   * with.
   * Excludes: self, already-connected users, users with any PENDING or ACCEPTED
   * request
   * in either direction.
   */
  @Query("""
      SELECT u FROM User u
      WHERE u.id <> :currentUserId
        AND u.isActive = true
        AND u.id NOT IN (
            SELECT cr.receiver.id FROM ConnectionRequest cr
            WHERE cr.sender.id = :currentUserId
              AND cr.status IN ('PENDING','ACCEPTED')
        )
        AND u.id NOT IN (
            SELECT cr.sender.id FROM ConnectionRequest cr
            WHERE cr.receiver.id = :currentUserId
              AND cr.status IN ('PENDING','ACCEPTED')
        )
      """)
  Page<User> findDiscoverableUsers(@Param("currentUserId") Long currentUserId, Pageable pageable);

  /**
   * Same exclusion rules as discover, but adds a keyword filter across
   * firstName, lastName, headline and skill names (case-insensitive).
   */
  @Query("""
      SELECT DISTINCT u FROM User u
      LEFT JOIN Skill s ON s.user.id = u.id
      WHERE u.id <> :currentUserId
        AND u.isActive = true
        AND u.id NOT IN (
            SELECT cr.receiver.id FROM ConnectionRequest cr
            WHERE cr.sender.id = :currentUserId
              AND cr.status IN ('PENDING','ACCEPTED')
        )
        AND u.id NOT IN (
            SELECT cr.sender.id FROM ConnectionRequest cr
            WHERE cr.receiver.id = :currentUserId
              AND cr.status IN ('PENDING','ACCEPTED')
        )
        AND (
              LOWER(u.firstName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.lastName)   LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.headline)   LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.skillName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
      """)
  Page<User> searchUsers(
      @Param("currentUserId") Long currentUserId,
      @Param("keyword") String keyword,
      Pageable pageable);
}