package app.text.node.texts;

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

  private String value;
}
