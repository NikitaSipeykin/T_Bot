package app.module.node.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

  private final MediaService mediaService;
  private final MinioService minioService;

  @PostMapping("/upload")
  public String upload(
      @RequestParam String key,
      @RequestParam MultipartFile file
  ) throws Exception {
    mediaService.uploadAndAssign(file, key);
    return "ok";
  }

  @GetMapping("/files")
  public List<String> files() throws Exception {
    return mediaService.listFiles();
  }

  @GetMapping("/settings")
  public Map<String, String> settings() {
    return mediaService.listSettings();
  }

  @DeleteMapping("/file/{name}")
  public String delete(@PathVariable String name) throws Exception {
    mediaService.deleteFile(name);
    return "deleted";
  }

  @GetMapping("/url/{name}")
  public String publicUrl(@PathVariable String name) {
    return minioService.getPublicUrl(name);
  }
}

