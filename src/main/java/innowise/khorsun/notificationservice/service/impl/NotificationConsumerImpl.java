package innowise.khorsun.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.khorsun.notificationservice.repository.NotificationRepository;
import innowise.khorsun.notificationservice.entity.Notification;
import innowise.khorsun.notificationservice.service.NotificationService;
import innowise.khorsun.notificationservice.utill.PropertyUtil;
import innowise.khorsun.notificationservice.utill.enums.Topic;
import innowise.khorsun.notificationservice.utill.errors.JsonParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class NotificationConsumerImpl implements NotificationService {

    @Value("spring.mail.username")
    private String emailFrom;

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationConsumerImpl(JavaMailSender javaMailSender, NotificationRepository notificationRepository) {
        this.javaMailSender = javaMailSender;
        this.notificationRepository = notificationRepository;
    }

    /**
     * This method sends a notification by email when the user successfully creates his account.
     * We will use it when we set up authentication
     */
    @Transactional
//    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "email")
    public void sendAuthenticationNotification(String email) {
        try {
            javaMailSender.send(createSimpleMailMessage(email, PropertyUtil.REGISTRATION_MESSAGE));
            saveNotification(email, true, Topic.REGISTRATION, PropertyUtil.REGISTRATION_MESSAGE);
        } catch (MailException e) {
            saveNotification(email, false, Topic.REGISTRATION, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method sends a notification by email with the link for payment when the user return car and.
     */
    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic.notification.link-for-payment}", groupId = "emailAndLink")
    public void sendNotificationWithPaymentLink(@Payload String payload) {
        Map<String, String> properties = createMapFromKafkaTopic(payload);
        String email = properties.get("email");
        String link = properties.get("link");
        try {
            javaMailSender.send(createSimpleMailMessage(email, PropertyUtil.PAYMENT_MESSAGE_WITH_LINK + link));
            saveNotification(email, true, Topic.LINK_PAYMENT, PropertyUtil.PAYMENT_MESSAGE_WITH_LINK + link);
        } catch (MailException e) {
            saveNotification(email, false, Topic.LINK_PAYMENT, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * When a client successfully pays for a car rental, he receives payment notification by email
     */
    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic.notification.payment}", groupId = "email")
    public void sendNotificationAboutSuccessPayment(String email) {
        try {
            javaMailSender.send(createSimpleMailMessage(email, PropertyUtil.PAYMENT_MESSAGE));
            saveNotification(email, true, Topic.PAYMENT, PropertyUtil.PAYMENT_MESSAGE);
        } catch (MailException e) {
            saveNotification(email, false, Topic.PAYMENT, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This private method create SimpleMailMessage using params
     */
    private SimpleMailMessage createSimpleMailMessage(String email, String textForSending) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setText(textForSending);
        return simpleMailMessage;
    }

    /**
     * Parsing Json to map
     */
    private Map<String, String> createMapFromKafkaTopic(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("Error parsing Kafka message payload to Map: " + e.getMessage(), new Date());
        }
    }

    /**
     * This method saves the notification that we send to our database
     */
    @Transactional
    public void saveNotification(String email, Boolean isSent, Topic topic, String message) {
        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setTopic(topic);
        notification.setSendMessage(message);
        notification.setCreationDate(LocalDateTime.now());
        notification.setIsSent(isSent);

        notificationRepository.save(notification);
    }

}
