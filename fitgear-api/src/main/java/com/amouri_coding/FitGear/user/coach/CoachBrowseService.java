package com.amouri_coding.FitGear.user.coach;

import com.amouri_coding.FitGear.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachBrowseService {

    private final CoachRepository coachRepository;
    private final CoachPublicMapper mapper;

    public PageResponse<CoachCardResponse> listCoaches(int page, int size, String q) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Coach> coaches = coachRepository.searchAvailableVerified(pageable, q);
        List<CoachCardResponse> content = coaches.stream().map(mapper::toCoachCard).toList();

        return PageResponse.<CoachCardResponse>builder()
                .content(content)
                .number(coaches.getNumber())
                .size(coaches.getSize())
                .totalElements(coaches.getTotalElements())
                .totalPages(coaches.getTotalPages())
                .first(coaches.isFirst())
                .last(coaches.isLast())
                .build();
    }

    public CoachDetailResponse getCoach(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        return mapper.toCoachDetail(coach);
    }
}

