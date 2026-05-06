package com.amouri_coding.FitGear.training.catalog;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises/catalog")
@RequiredArgsConstructor
@Tag(name = "Exercise Catalog")
public class ExerciseCatalogController {

    private final ExerciseCatalogRepository repository;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_COACH')")
    public ResponseEntity<List<ExerciseCatalogResponse>> search(
            @RequestParam(defaultValue = "") String q
    ) {
        List<ExerciseCatalogResponse> results;
        if (q.isBlank()) {
            results = repository.findAllByOrderByNameAsc().stream()
                    .map(e -> new ExerciseCatalogResponse(e.getId(), e.getName(), e.getMuscleGroup()))
                    .toList();
        } else {
            results = repository.searchByName(q.trim()).stream()
                    .map(e -> new ExerciseCatalogResponse(e.getId(), e.getName(), e.getMuscleGroup()))
                    .toList();
        }
        return ResponseEntity.ok(results);
    }
}
