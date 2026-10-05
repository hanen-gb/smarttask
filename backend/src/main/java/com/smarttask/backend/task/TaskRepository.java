package com.smarttask.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

// JpaSpecificationExecutor : ajoute findAll(Specification<Task>, Pageable) --
// indispensable pour l'etape 8 (recherche/filtres combinables).
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findByProjectIdOrderByPositionAsc(UUID projectId);
}
