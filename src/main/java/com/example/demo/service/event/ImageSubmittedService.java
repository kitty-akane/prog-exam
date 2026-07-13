package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.ImageSubmitted;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.service.ImageSubmissionService;
import jakarta.mail.internet.InternetAddress;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ImageSubmittedService implements Consumer<ImageSubmitted> {

  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(ImageSubmitted event) {
    var extension = ImageSubmissionService.extensionOf(event.getFileName());
    var originalKey = ImageSubmissionService.originalKey(event.getId(), event.getFileName());

    var originalFile = bucketComponent.download(originalKey);
    var grayscaleBytes = toGrayscale(Files.readAllBytes(originalFile.toPath()), extension);

    var bwKey = "images/%s/black-and-white.%s".formatted(event.getId(), extension);
    uploadToBucket(bwKey, grayscaleBytes);

    var downloadUrl = bucketComponent.presign(bwKey, Duration.ofDays(7)).toString();

    sendEmail(event.getEmail(), event.getFileName(), downloadUrl);
  }

  private byte[] toGrayscale(byte[] originalBytes, String extension) throws Exception {
    var original = ImageIO.read(new java.io.ByteArrayInputStream(originalBytes));
    var grayscale =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var graphics = grayscale.createGraphics();
    graphics.drawImage(original, 0, 0, null);
    graphics.dispose();

    var output = new ByteArrayOutputStream();
    ImageIO.write(grayscale, extension.equals("png") ? "png" : "jpg", output);
    return output.toByteArray();
  }

  @SneakyThrows
  private void uploadToBucket(String key, byte[] content) {
    File tempFile = File.createTempFile("bw-", "-" + key.replace("/", "_"));
    Files.write(tempFile.toPath(), content);
    try {
      bucketComponent.upload(tempFile, key);
    } finally {
      Files.deleteIfExists(tempFile.toPath());
    }
  }

  @SneakyThrows
  private void sendEmail(String to, String fileName, String downloadUrl) {
    var subject = "Your black & white version of %s is ready".formatted(fileName);
    var htmlBody =
        """
<html>
    <body>
        <p>Hello,</p>
        <p>Your image <strong>%s</strong> has been converted to black &amp; white.</p>
        <p><a href="%s">Download it here</a> (link valid for 7 days).</p>
    </body>
</html>
"""
            .formatted(fileName, downloadUrl);
    mailer.accept(
        new Email(new InternetAddress(to), List.of(), List.of(), subject, htmlBody, List.of()));
  }
}
