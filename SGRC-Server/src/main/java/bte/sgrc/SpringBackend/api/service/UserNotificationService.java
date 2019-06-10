package bte.sgrc.SpringBackend.api.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.repository.UserNotificationRepository;

@Service
public class UserNotificationService {
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    private static Logger logger = LoggerFactory.getLogger(UserNotificationService.class);

    public UserNotification createOrUpdate(UserNotification notification) {
        return userNotificationRepository.save(notification);
    }

    public Optional<UserNotification> findByUser(String userId) {
        return this.userNotificationRepository.findByUserId(userId);
    }

    public UserNotification notifyUser(String userId, String ticketId, String message) {
        UserNotification notification = new UserNotification();

        if (userNotificationRepository.findByUserId(userId).isPresent()){
            notification = userNotificationRepository.findByUserId(userId).get();
            UserNotification temp = new UserNotification();
            temp.addNotification(new Notification(ticketId, message));
            temp.setUserId(userId);

            notification.addNotification(new Notification(ticketId, message));
        } else {
            UserNotification temp = new UserNotification();
            
            temp.addNotification(new Notification(ticketId, message));
            temp.setUserId(userId);
            notification = userNotificationRepository.save(temp);
        }



        //this.purgeFromDB(notification);
        return userNotificationRepository.save(notification);
    }
}