package bte.sgrc.SpringBackend.api.service;

import static org.springframework.http.ResponseEntity.ok;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService() {
    }

    public Map<String, Object> updateUserNotification(Notification notification, User user) {

        notification = notificationRepository.save(notification);
        if (notification == null) {

            String string = "NotificationNotUpdated:";
            return (Map<String, Object>) ResponseEntity.badRequest().body(string + HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String body = "Notification Updated";
        return (Map<String, Object>) ok(body);
    }

    public Notification save(Notification notification) {
        try {
            return notificationRepository.save(notification);
        } catch (Exception e) {
            logger.error("Exception occur while save Notification ", e);
            return null;
        }
    }

    public Notification findByUser(User user) {
        try {
            return notificationRepository.findByUser(user);
        } catch (Exception e) {
            logger.error("Exception occur while fetch Notification by User ", e);
            return null;
        }
    }

    public List<Notification> findByUser(User user, Integer limit) {
        try {
            return notificationRepository.findByUserOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, limit));
        } catch (Exception e) {
            logger.error("Exception occur while fetch Notification by User ", e);
            return null;
        }
    }

    public Notification createNotificationObject(String message, User user) {
        return new Notification(message, new Date(), user);
    }

    public Notification findByUserAndNotificationId(User user, Integer notificationId) {
        try {
            return notificationRepository.findByUserAndNotificationId(user, notificationId.toString());
        } catch (Exception e) {
            logger.error("Exception occur while fetch Notification by User and Notification Id ", e);
            return null;
        }
    }

}