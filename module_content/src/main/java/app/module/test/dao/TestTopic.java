package app.module.test.dao;

import jakarta.persistence.*;

@Entity
@Table(
    name = "test_topic"
)
public class TestTopic {
  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY
  )
  private Long id;
  private String name;
  @Column(
      name = "order_index"
  )
  private Integer orderIndex;

  public TestTopic() {
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOrderIndex(Integer orderIndex) {
    this.orderIndex = orderIndex;
  }
}