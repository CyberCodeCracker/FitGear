package com.amouri_coding.FitGear.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE (
                :search = '' OR
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.email)     LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                :role = '' OR
                (:role = 'COACH'  AND TYPE(u) = Coach)  OR
                (:role = 'CLIENT' AND TYPE(u) = Client) OR
                (:role = 'ADMIN'  AND TYPE(u) = Admin)
            )
            ORDER BY u.createdAt DESC
            """)
    Page<User> searchUsers(String search, String role, Pageable pageable);
}
