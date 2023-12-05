package innowise.khorsun.notificationservice.repository;

import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification,Integer> {
    List<ScheduledNotification> findAllByIsSent(Boolean isSent);
}
