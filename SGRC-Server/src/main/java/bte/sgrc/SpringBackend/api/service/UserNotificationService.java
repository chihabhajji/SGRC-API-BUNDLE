package bte.sgrc.SpringBackend.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.repository.UserNotificationRepository;

@Service
public class UserNotificationService {
    @Autowired
    private UserNotificationRepository userNotificationRepository;

    public UserNotification createOrUpdate(UserNotification notification) {
        return userNotificationRepository.save(notification);
    }

    public UserNotification findByUser(String userId) {
        return this.userNotificationRepository.findByUserId(userId);
    }

    public UserNotification notifyUser(String userId, String ticketId, String message) {
        UserNotification notification = userNotificationRepository.findByUserId(userId);
        UserNotification temp = new UserNotification();
        temp.addNotification(new Notification(ticketId,message));
        temp.setUserId(userId);
        if (notification == null)
            notification = userNotificationRepository.save(temp);
        else
            notification.addNotification(new Notification(ticketId,message));
        //this.purgeFromDB(notification);
        return userNotificationRepository.save(notification);
    }
}