package innowise.khorsun.notificationservice.service;

import innowise.khorsun.notificationservice.entity.Notification;
import innowise.khorsun.notificationservice.utill.enums.Topic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface NotificationService {
    void sendAuthenticationNotification(String email);

    List<Notification> getNotificationForSendingByTopicAndCreationTime(Topic topic, LocalDateTime statTime, LocalDateTime endTime);
}
