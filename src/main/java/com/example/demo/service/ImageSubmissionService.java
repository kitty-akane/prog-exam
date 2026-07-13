package com.example.demo.service;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.ImageSubmitted;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mapper.ImageSubmissionMapper;
import com.example.demo.model.ImageSubmission;
import com.example.demo.repository.ImageSubmissionRepository;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ImageSubmissionService {

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

  private final ImageSubmissionMapper mapper;
  private final ImageSubmissionRepository repository;
  private final BucketComponent bucketComponent;
  private final EventProducer<ImageSubmitted> eventProducer;

  @SneakyThrows
  public ImageSubmission create(MultipartFile file, String email) {
    var contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Only image/jpeg and image/png are accepted");
    }

    var id = UUID.randomUUID();
    var originalKey = originalKey(id, file.getOriginalFilename());

    uploadToBucket(originalKey, file.getBytes());

    var entity = mapper.toEntity(id, file.getOriginalFilename(), email, LocalDateTime.now());
    var saved = mapper.toModel(repository.save(entity));

    eventProducer.accept(
        List.of(
            ImageSubmitted.builder()
                .id(id)
                .fileName(file.getOriginalFilename())
                .email(email)
                .createdAt(saved.createdAt())
                .build()));

    return saved;
  }

  public List<ImageSubmission> findAll() {
    return mapper.toModel(repository.findAll());
  }

  /** Derives the S3 key from id + the file's own extension. Used identically by the worker. */
  public static String originalKey(UUID id, String fileName) {
    return "images/%s/original.%s".formatted(id, extensionOf(fileName));
  }

  public static String extensionOf(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "jpg";
    }
    return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
  }

  @SneakyThrows
  private void uploadToBucket(String key, byte[] content) {
    File tempFile = File.createTempFile("upload-", "-" + key.replace("/", "_"));
    Files.write(tempFile.toPath(), content);
    try {
      bucketComponent.upload(tempFile, key);
    } finally {
      Files.deleteIfExists(tempFile.toPath());
    }
  }
}
