package com.amouri_coding.FitGear.user.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    Page<Client> findAllClientsByCoachId(Pageable pageable, Long coachId);
}
