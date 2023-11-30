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
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "email")
    private String email;
    @Column(name="topic")
    @Enumerated(value = EnumType.STRING)
    private Topic topic;
    @Column(name = "send_message")
    private String sendMessage;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "is_sent")
    private Boolean isSent;
}
