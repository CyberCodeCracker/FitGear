package com.amouri_coding.FitGear.training.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseCatalogRepository extends JpaRepository<ExerciseCatalog, Long> {

    @Query("SELECT e FROM ExerciseCatalog e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY e.name")
    List<ExerciseCatalog> searchByName(String query);

    List<ExerciseCatalog> findAllByOrderByNameAsc();

    boolean existsByName(String name);
}
