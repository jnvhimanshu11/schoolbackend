package com.atlas.academy.service;

import com.atlas.academy.dto.PagedResponse;
import com.atlas.academy.dto.admission.AdmissionRequest;
import com.atlas.academy.entity.Admission;
import com.atlas.academy.exception.ResourceNotFoundException;
import com.atlas.academy.repository.AdmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdmissionService {

    private static final int PAGE_SIZE = 10;

    private final AdmissionRepository admissionRepository;

    @Transactional
    public Admission submit(AdmissionRequest request) {
        Admission admission = Admission.builder()
                .studentName(request.getStudentName())
                .parentName(request.getParentName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .classProgram(request.getClassProgram())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .prevSchool(request.getPrevSchool())
                .message(request.getMessage())
                .status("PENDING")
                .build();

        return admissionRepository.save(admission);
    }

    public PagedResponse<Admission> getAll(int page, String status) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("createdAt").descending());

        if (status != null && !status.isBlank()) {
            return PagedResponse.from(admissionRepository.findByStatusIgnoreCase(status, pageable));
        }
        return PagedResponse.from(admissionRepository.findAll(pageable));
    }

    @Transactional
    public Admission updateStatus(Long id, String status) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id " + id));
        admission.setStatus(status.toUpperCase());
        return admissionRepository.save(admission);
    }

    @Transactional
    public void delete(Long id) {
        if (!admissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admission not found with id " + id);
        }
        admissionRepository.deleteById(id);
    }
}
