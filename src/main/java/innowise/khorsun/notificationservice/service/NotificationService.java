package innowise.khorsun.notificationservice.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendAuthenticationNotification(String email);
}
