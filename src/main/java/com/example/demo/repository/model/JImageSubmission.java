package com.example.demo.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JImageSubmission {
  @Id private UUID id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "email")
  private String email;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
