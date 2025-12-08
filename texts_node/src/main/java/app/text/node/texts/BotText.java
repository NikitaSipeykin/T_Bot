package app.text.node.texts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bot_texts")
@Getter
@Setter
public class BotText {
  @Id
  private String id;

  @Column(columnDefinition = "TEXT")
  private String value;
}
