package innowise.khorsun.notificationservice.model;

import innowise.khorsun.notificationservice.utill.enums.Topic;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ScheduledNotificationRequest {

    private String notificationName;
    private Topic topic;
    private String sendMessage;
    private LocalDateTime startTimeForSearch;
    private LocalDateTime endTimeForSearch;
    private LocalDateTime scheduledTime;
    private Topic findByTopic;
}
