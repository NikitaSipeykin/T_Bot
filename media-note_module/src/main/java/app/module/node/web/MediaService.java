package app.module.node.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {

  private final MinioService minioService;
  private final MediaSettingRepository repo;

  public void uploadAndAssign(MultipartFile file, String key) throws Exception {
    String filename = file.getOriginalFilename();
    minioService.upload(filename, file);

    repo.save(new MediaSetting(key, filename));
  }

  public List<String> listFiles() throws Exception {
    return minioService.list();
  }

  public Map<String, String> listSettings() {
    return repo.findAll().stream()
        .collect(Collectors.toMap(
            MediaSetting::getKeyName,
            it -> it.getFileName() == null ? "" : it.getFileName()
        ));
  }

  public void deleteFile(String filename) throws Exception {
    // 1. Удаляем файл из Minio
    minioService.delete(filename);

    // 2. Обнуляем привязку файла, но ключ не удаляем!
    repo.findAll().stream()
        .filter(s -> filename.equals(s.getFileName()))
        .forEach(s -> {
          s.setFileName(null);
          repo.save(s);
        });
  }

  /**
   * Получить имя файла по ключу
   */
  public String getFilenameByKey(String key) {
    return repo.findById(key)
        .map(MediaSetting::getFileName)
        .orElse(null); // вернём null если нет файла
  }

  /**
   * Получить файл (скачать из Minio во временный файл)
   */
  public File getFileByKey(String key) throws Exception {
    String filename = getFilenameByKey(key);

    if (filename == null || filename.isBlank()) {
      throw new RuntimeException("No file assigned for key: " + key);
    }

    // 1. Создаём уникальную временную директорию
    Path tempDir = Files.createTempDirectory("media_");

    // 2. Формируем путь к файлу с оригинальным именем
    Path filePath = tempDir.resolve(filename);

    // 3. Качаем файл из MinIO и записываем на диск
    try (InputStream is = minioService.download(filename)) {
      Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    // 4. Возвращаем file-объект на оригинальный файл
    return filePath.toFile();
  }
}

