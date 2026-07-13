package com.example.demo.mapper;

import com.example.demo.model.ImageSubmission;
import com.example.demo.repository.model.JImageSubmission;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ImageSubmissionMapper {

  public JImageSubmission toEntity(
      UUID id, String fileName, String email, LocalDateTime createdAt) {
    return JImageSubmission.builder()
        .id(id)
        .fileName(fileName)
        .email(email)
        .createdAt(createdAt)
        .build();
  }

  public ImageSubmission toModel(JImageSubmission entity) {
    return ImageSubmission.builder()
        .id(entity.getId())
        .fileName(entity.getFileName())
        .email(entity.getEmail())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public List<ImageSubmission> toModel(List<JImageSubmission> entities) {
    return entities.stream().map(this::toModel).toList();
  }
}
