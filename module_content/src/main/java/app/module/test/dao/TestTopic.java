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

  public int getOrderIndex() {
    return this.orderIndex;
  }
}