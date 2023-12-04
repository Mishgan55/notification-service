package innowise.khorsun.notificationservice.repository;

import innowise.khorsun.notificationservice.entity.Notification;
import innowise.khorsun.notificationservice.utill.enums.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    List<Notification> findDistinctByTopic(Topic topic);
}
