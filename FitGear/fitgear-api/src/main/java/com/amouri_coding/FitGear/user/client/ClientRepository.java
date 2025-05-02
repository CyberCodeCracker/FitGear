package com.amouri_coding.FitGear.user.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    @Query("""
            SELECT client
            FROM Client client
            WHERE client.coach.id = :coachId
            """)
    Page<Client> findAllClientsByCoachId(Pageable pageable, Long coachId);

    @Query("""
            SELECT client
            FROM Client client
            WHERE client.coach.id = :coachId
            AND (
                LOWER(client.firstName) LIKE LOWER(CONCAT(:name, '%')) OR
                LOWER(client.lastName) LIKE LOWER(CONCAT(:name, '%')) OR
                LOWER(CONCAT(client.firstName, ' ', client.lastName)) LIKE LOWER(CONCAT(:name, '%')
            )
    )
    """)
    Page<Client> findAllClientsByName(String name, Pageable pageable, Long coachId);

    @Query("""
        SELECT client.coach.id FROM Client client
        WHERE client.id = :clientId
        """)
    Optional<Long> findCoachIdByClientId(Long clientId);
}
