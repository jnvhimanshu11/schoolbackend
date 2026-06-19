package com.atlas.academy.repository;

import com.atlas.academy.entity.Admission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    Page<Admission> findByStatusIgnoreCase(String status, Pageable pageable);
}
