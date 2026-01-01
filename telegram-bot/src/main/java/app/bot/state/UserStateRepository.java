package app.bot.state;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStateRepository extends JpaRepository<UserStateEntity, Long> {
}
