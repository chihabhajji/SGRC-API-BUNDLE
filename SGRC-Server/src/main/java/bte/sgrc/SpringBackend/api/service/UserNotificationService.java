package bte.sgrc.SpringBackend.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.User;
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

    public UserNotification findByUser(User user) {
        return this.userNotificationRepository.findByUser(user);
    }

    public UserNotification notifyUser(User user, Ticket ticket, String message) {
        UserNotification notification = userNotificationRepository.findByUser(user);
        UserNotification temp = new UserNotification();
        temp.addNotification(new Notification(ticket,message));
        temp.setUser(user);
        if (notification == null)
            notification = userNotificationRepository.save(temp);
        else
            notification.addNotification(new Notification(ticket,message));
        this.purgeFromDB(notification);
        return userNotificationRepository.save(notification);
    }

    // Doesnt work, who cares anyway
    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    private void purgeFromDB(UserNotification notification) {
        userNotificationRepository.delete(notification);
    }
}