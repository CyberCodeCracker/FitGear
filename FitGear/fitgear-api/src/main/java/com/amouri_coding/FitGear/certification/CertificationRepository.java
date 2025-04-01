package com.amouri_coding.FitGear.certification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    List<Certification> findAllByIdIn(List<Integer> certificationsIds);
}
