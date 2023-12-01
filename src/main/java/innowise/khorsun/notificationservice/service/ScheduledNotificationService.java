package innowise.khorsun.notificationservice.service;

import innowise.khorsun.notificationservice.model.ScheduledNotificationRequest;
import org.springframework.stereotype.Service;

@Service
public interface ScheduledNotificationService {

    void createScheduledNotification(ScheduledNotificationRequest request);

}
