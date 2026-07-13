package com.example.demo.endpoint.rest.controller.health;

import com.example.demo.model.ImageSubmission;
import com.example.demo.service.ImageSubmissionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ImageSubmissionController {
  private final ImageSubmissionService imageSubmissionService;

  @PostMapping(value = "/images", consumes = "multipart/form-data")
  public ImageSubmission submit(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    return imageSubmissionService.create(file, email);
  }

  @GetMapping("/images")
  public List<ImageSubmission> findAll() {
    return imageSubmissionService.findAll();
  }
}
