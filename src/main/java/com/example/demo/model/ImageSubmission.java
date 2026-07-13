package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ImageSubmission(UUID id, String fileName, String email, LocalDateTime createdAt) {}
