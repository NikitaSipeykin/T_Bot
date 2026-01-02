package app.module.analytics.repository;

import app.module.analytics.model.AnalyticsEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsEventRepository
    extends JpaRepository<AnalyticsEventEntity, Long> {
}

