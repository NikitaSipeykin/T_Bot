package app.module.test.repo;

import app.module.test.dao.TestTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestTopicRepository extends JpaRepository<TestTopic, Long> {
  Optional<TestTopic> findByOrderIndex(Integer index);
}
