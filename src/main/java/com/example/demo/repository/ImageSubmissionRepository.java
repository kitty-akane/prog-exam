package com.example.demo.repository;

import com.example.demo.repository.model.JImageSubmission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageSubmissionRepository extends JpaRepository<JImageSubmission, UUID> {}
