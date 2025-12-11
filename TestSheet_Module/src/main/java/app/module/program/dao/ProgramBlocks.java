package app.module.program.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "program_blocks")
public class ProgramBlocks {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  // id текста из таблицы bot_texts
  @Column(name = "text_id", nullable = false)
  private String textId;

  // Следующий блок в цепочке
  @Column(name = "next_block")
  private String nextBlock;

  @Column(name = "button_text")
  private String buttonText;

  // Getters / Setters
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTextId() {
    return textId;
  }

  public String getNextBlock() {
    return nextBlock;
  }

  public String getButtonText() {
    return buttonText;
  }
}

