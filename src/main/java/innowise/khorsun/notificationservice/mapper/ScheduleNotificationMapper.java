package innowise.khorsun.notificationservice.mapper;

import innowise.khorsun.notificationservice.entity.ScheduledNotification;
import innowise.khorsun.notificationservice.model.ScheduledNotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ScheduleNotificationMapper {
    @Mapping(target = "startTimeForSearch", source = "startTimeForSearch")
    @Mapping(target = "endTimeForSearch", source = "endTimeForSearch")
    @Mapping(target = "scheduledTime", source = "scheduledTime")
    ScheduledNotification scheduledNotificationRequestToScheduledNotification(ScheduledNotificationRequest request);
}