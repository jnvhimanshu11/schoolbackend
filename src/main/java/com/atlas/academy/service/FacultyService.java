package com.atlas.academy.service;

import com.atlas.academy.dto.faculty.FacultyRequest;
import com.atlas.academy.entity.Faculty;
import com.atlas.academy.exception.ResourceNotFoundException;
import com.atlas.academy.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public List<Faculty> getAll() {
        return facultyRepository.findAllByOrderByCreatedAtAsc();
    }

    @Transactional
    public Faculty create(FacultyRequest request) {
        Faculty faculty = Faculty.builder()
                .name(request.getName())
                .designation(request.getDesignation())
                .subject(request.getSubject())
                .photoUrl(request.getPhotoUrl())
                .bio(request.getBio())
                .build();
        return facultyRepository.save(faculty);
    }

    @Transactional
    public Faculty update(Long id, FacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));

        faculty.setName(request.getName());
        faculty.setDesignation(request.getDesignation());
        faculty.setSubject(request.getSubject());
        faculty.setPhotoUrl(request.getPhotoUrl());
        faculty.setBio(request.getBio());

        return facultyRepository.save(faculty);
    }

    @Transactional
    public void delete(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Faculty not found with id " + id);
        }
        facultyRepository.deleteById(id);
    }
}
