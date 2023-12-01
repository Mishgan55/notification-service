package innowise.khorsun.notificationservice.repository;

import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification,Integer> {
}
