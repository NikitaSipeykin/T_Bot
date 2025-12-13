package app.module.node.web;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioService {

  private final MinioClient client;

  @Value("${minio.bucket}")
  private String bucket;

  @Value("${minio.url}")
  private String url;

  public void upload(String filename, MultipartFile file) throws Exception {
    client.putObject(
        PutObjectArgs.builder()
            .bucket(bucket)
            .object(filename)
            .stream(file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build()
    );
  }

  public List<String> list() throws Exception {
    List<String> files = new ArrayList<>();
    Iterable<Result<Item>> results = client.listObjects(
        ListObjectsArgs.builder()
            .bucket(bucket)
            .recursive(true)
            .build());

    for (Result<Item> r : results) {
      files.add(r.get().objectName());
    }
    return files;
  }

  public void delete(String filename) throws Exception {
    client.removeObject(RemoveObjectArgs.builder()
        .bucket(bucket)
        .object(filename)
        .build());
  }

  public InputStream download(String filename) throws Exception {
    return client.getObject(
        GetObjectArgs.builder()
            .bucket(bucket)
            .object(filename)
            .build()
    );
  }

  public File downloadToTempFile(String filename) throws Exception {
    try (InputStream is = download(filename)) {
      String safeName = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
      File temp = File.createTempFile("minio_", "_" + safeName);
      temp.deleteOnExit();

      try (FileOutputStream fos = new FileOutputStream(temp)) {
        is.transferTo(fos);
      }
      return temp;
    }
  }

  public String getPublicUrl(String filename) {
    return "%s/%s/%s".formatted(url, bucket, filename);
  }
}

