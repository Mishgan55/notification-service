package innowise.khorsun.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import innowise.khorsun.notificationservice.repository.NotificationRepository;
import innowise.khorsun.notificationservice.entity.Notification;
import innowise.khorsun.notificationservice.service.NotificationService;
import innowise.khorsun.notificationservice.service.ScheduledNotificationService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class NotificationConsumerImpl implements NotificationService {

    @Value("spring.mail.username")
    private String emailFrom;

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;
    private final ScheduledNotificationService scheduledNotificationService;

    @Autowired
    public NotificationConsumerImpl(JavaMailSender javaMailSender, NotificationRepository notificationRepository, ScheduledNotificationService scheduledNotificationService) {
        this.javaMailSender = javaMailSender;
        this.notificationRepository = notificationRepository;
        this.scheduledNotificationService = scheduledNotificationService;
    }

    /**
     * This method sends a notification by email when the user successfully creates his account.
     * We will use it when we set up authentication
     */
    @Transactional
    public void sendAuthenticationNotification(String email) {
        sendNotification(email,Topic.REGISTRATION,PropertyUtil.REGISTRATION_MESSAGE);
    }

    /**
     * This method automatically tracks, generates and sends notifications using a  scheduled_notification database.
     * Searches for users to whom we want to send a notification from the notification database
     * by the date when the operation was performed and the type of operation.
     */
    @Scheduled(fixedRate = 60000)//60 sek
    @Transactional
    public void sendScheduledNotification() {
        //we receive all scheduled Notification that we have not yet sent
        List<ScheduledNotification> scheduledNotificationForSending = scheduledNotificationService
                .getAllScheduledNotificationsByIsSent(false);
        for (ScheduledNotification scheduledNotification : scheduledNotificationForSending) {
            /*
            Here we check for scheduled Notification that we need to send.
            We compare the time at which we want to send notifications and the time that is now
             */
            if (scheduledNotification.getScheduledTime().isBefore(LocalDateTime.now())) {
                Topic findByTopic = scheduledNotification.getFindByTopic();
                /*
                We find users to whom we will send notifications based on specified
                 criteria from scheduled Notification db
                 */
                List<Notification> notificationsForSending =
                        getNotificationForSendingByTopicAndCreationTime(findByTopic,
                                scheduledNotification.getStartTimeForSearch(),
                                scheduledNotification.getEndTimeForSearch());
                //Here we send emails for users
                for (Notification notification : notificationsForSending) {
                    Topic topic = scheduledNotification.getTopic();
                    String email = notification.getEmail();
                    String sendMessage = scheduledNotification.getSendMessage();
                    sendNotification(email, topic, sendMessage);
                    scheduledNotification.setIsSent(true);
                }
            }
        }
    }

    /**
     * This method searches for unique users by topic and date of operation creation
     */
    @Override
    public List<Notification> getNotificationForSendingByTopicAndCreationTime(Topic topic, LocalDateTime statTime,
                                                                              LocalDateTime endTime) {
        List<Notification> byTopic = notificationRepository.findDistinctByTopic(topic);
        List<Notification> notificationsByCreationTime = new ArrayList<>();
        for (Notification notification : byTopic) {
            LocalDateTime creationDate = notification.getCreationDate();
            if (creationDate.isAfter(statTime) && creationDate.isBefore(endTime)) {
                notificationsByCreationTime.add(notification);
            }
        }
        return notificationsByCreationTime;
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
        String name = properties.get("name");
        sendNotification(email, Topic.LINK_PAYMENT,
                PropertyUtil.PAYMENT_MESSAGE_WITH_LINK.replace("{name}",name) + link);
    }

    /**
     * When a client successfully pays for a car rental, he receives payment notification by email
     */
    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic.notification.payment}", groupId = "email")
    public void sendNotificationAboutSuccessPayment(@Payload String payload) {
        Map<String, String> properties = createMapFromKafkaTopic(payload);
        String email = properties.get("email");
        String name = properties.get("name");
        sendNotification(email, Topic.PAYMENT, PropertyUtil.PAYMENT_MESSAGE.replace("{name}",name));
    }

    /**
     * This method saves the notification in the database
     * and sends the notification by email using the specified parameters
     */
    private void sendNotification(String email, Topic topic, String sendMessage) {
        try {
            javaMailSender.send(createSimpleMailMessage(email, sendMessage));
            saveNotification(email, true, topic, sendMessage);
        } catch (MailException e) {
            saveNotification(email, false, topic, e.getMessage());
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
