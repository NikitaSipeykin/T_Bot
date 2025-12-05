package app.module.test.repo;

import app.module.test.dao.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
  Optional<TestQuestion> findByTopicIdAndNum(Long topicId, Integer num);
}
