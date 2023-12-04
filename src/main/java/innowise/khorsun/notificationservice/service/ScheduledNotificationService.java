package innowise.khorsun.notificationservice.service;

import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import innowise.khorsun.notificationservice.model.ScheduledNotificationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduledNotificationService {

    void createScheduledNotification(ScheduledNotificationRequest request);

    List<ScheduledNotification> getAllScheduledNotificationsByIsSent(Boolean isSent);
}
