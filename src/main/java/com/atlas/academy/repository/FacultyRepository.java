package com.atlas.academy.repository;

import com.atlas.academy.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findAllByOrderByCreatedAtAsc();
}
