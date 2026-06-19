package com.atlas.academy.service;

import com.atlas.academy.dto.notice.NoticeRequest;
import com.atlas.academy.entity.Notice;
import com.atlas.academy.exception.ResourceNotFoundException;
import com.atlas.academy.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> getAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Notice create(NoticeRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice update(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id " + id));
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        return noticeRepository.save(notice);
    }

    @Transactional
    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notice not found with id " + id);
        }
        noticeRepository.deleteById(id);
    }
}
