package innowise.khorsun.notificationservice.controller;

import innowise.khorsun.notificationservice.model.ScheduledNotificationRequest;
import innowise.khorsun.notificationservice.service.ScheduledNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduledNotificationController {

    private final ScheduledNotificationService scheduledNotificationService;

    @Autowired
    public ScheduledNotificationController(ScheduledNotificationService scheduledNotificationService) {
        this.scheduledNotificationService = scheduledNotificationService;
    }
    /**
     * Creates a scheduled notification for automatic mailing using a post request, specifying the time for mailing
     * and the fields by which we will later create criteria for sending a letter by mail
     * Link <a href="http://localhost:8084/create-scheduled-notification">...</a>
     * Example json
     * {
     *     "notificationName": "sales-for-customers-2023",
     *     "topic": "SALES",
     *     "sendMessage": "Happy new year! We are giving you a discount of $30", example message for sending
     *     "startTimeForSearch": "2023-01-01T01:00:00.00000",
     *     "endTimeForSearch": "2023-12-31T12:00:00.00000",
     *     "scheduledTime": "2023-12-31T12:00:00.00000"   here you can specify the time at which we need to send notifications
     *     "findByTopic" : "PAYMENT";  here you can specify topic by which we will search our clients
     * }*/
    @PostMapping("/create-scheduled-notification")
    public void createScheduledNotification(@RequestBody ScheduledNotificationRequest request) {
        scheduledNotificationService.createScheduledNotification(request);
    }
}
