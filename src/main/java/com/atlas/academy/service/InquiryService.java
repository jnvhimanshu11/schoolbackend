package com.atlas.academy.service;

import com.atlas.academy.dto.PagedResponse;
import com.atlas.academy.dto.inquiry.InquiryRequest;
import com.atlas.academy.dto.inquiry.InquiryUpdateRequest;
import com.atlas.academy.entity.Inquiry;
import com.atlas.academy.exception.ResourceNotFoundException;
import com.atlas.academy.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private static final int PAGE_SIZE = 10;

    private final InquiryRepository inquiryRepository;

    @Transactional
    public Inquiry submit(InquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .name(request.getName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .subject(request.getSubject() != null ? request.getSubject() : "General Inquiry")
                .message(request.getMessage())
                .resolved(false)
                .build();
        return inquiryRepository.save(inquiry);
    }

    public PagedResponse<Inquiry> getAll(int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);
        return PagedResponse.from(inquiryRepository.findAllByOrderByCreatedAtDesc(pageable));
    }

    @Transactional
    public Inquiry update(Long id, InquiryUpdateRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id " + id));

        if (request.getName() != null) inquiry.setName(request.getName());
        if (request.getMobile() != null) inquiry.setMobile(request.getMobile());
        if (request.getEmail() != null) inquiry.setEmail(request.getEmail());
        if (request.getSubject() != null) inquiry.setSubject(request.getSubject());
        if (request.getMessage() != null) inquiry.setMessage(request.getMessage());
        if (request.getResolved() != null) inquiry.setResolved(request.getResolved());

        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public void delete(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inquiry not found with id " + id);
        }
        inquiryRepository.deleteById(id);
    }
}
