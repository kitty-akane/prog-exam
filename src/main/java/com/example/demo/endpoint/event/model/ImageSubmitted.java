package com.example.demo.endpoint.event.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder
public class ImageSubmitted extends PojaEvent {
  private UUID id;
  private String fileName;
  private String email;
  private LocalDateTime createdAt;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(35);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(60);
  }
}
