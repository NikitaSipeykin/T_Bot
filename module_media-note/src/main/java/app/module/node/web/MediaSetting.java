package app.module.node.web;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaSetting {
  @Id
  private String keyName; // логический ключ: greeting_video, error_video

  private String fileName; // имя файла в MinIO
}

