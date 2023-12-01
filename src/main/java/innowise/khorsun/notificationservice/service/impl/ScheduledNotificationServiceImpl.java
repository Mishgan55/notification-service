package innowise.khorsun.notificationservice.service.impl;

import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import innowise.khorsun.notificationservice.mapper.ScheduleNotificationMapper;
import innowise.khorsun.notificationservice.model.ScheduledNotificationRequest;
import innowise.khorsun.notificationservice.repository.ScheduledNotificationRepository;
import innowise.khorsun.notificationservice.service.ScheduledNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ScheduledNotificationServiceImpl implements ScheduledNotificationService {

    private final ScheduledNotificationRepository scheduledNotificationRepository;
    private final ScheduleNotificationMapper scheduleNotificationMapper;

    public ScheduledNotificationServiceImpl(ScheduledNotificationRepository scheduledNotificationRepository, ScheduleNotificationMapper scheduleNotificationMapper) {
        this.scheduledNotificationRepository = scheduledNotificationRepository;
        this.scheduleNotificationMapper = scheduleNotificationMapper;
    }
    /**
     * This method saves the ScheduledNotificationRequest to the database
     * and adds the current creation time*/
    @Override
    @Transactional
    public void createScheduledNotification(ScheduledNotificationRequest request) {
        ScheduledNotification scheduledNotification = scheduleNotificationMapper
                .scheduledNotificationRequestToScheduledNotification(request);
        scheduledNotification.setCreationDate(LocalDateTime.now());
        scheduledNotificationRepository.save(scheduledNotification);
    }
}
