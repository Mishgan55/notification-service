package innowise.khorsun.notificationservice.entity;

import innowise.khorsun.notificationservice.utill.enums.Topic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "scheduled_notifications")
public class ScheduledNotification {
    //TODO Validation
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "notification_name")
    private String notificationName;
    @Column(name = "topic")
    @Enumerated(value = EnumType.STRING)
    private Topic topic;
    @Column(name = "send_message")
    private String sendMessage;
    @Column(name = "start_time_for_search")
    private LocalDateTime startTimeForSearch;
    @Column(name = "end_time_for_search")
    private LocalDateTime endTimeForSearch;
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "is_sent")
    private Boolean isSent;
    @Column(name="find_by_topic")
    @Enumerated(value = EnumType.STRING)
    private Topic findByTopic;
}
