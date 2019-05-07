package bte.sgrc.SpringBackend.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    public Page<UserNotification> findByUser(Integer page, Integer count, String userId) {
        Pageable pages = PageRequest.of(page, count);
        return this.userNotificationRepository.findByUserId(pages, userId);
    }

    public UserNotification notifyUser(User user, String message) {
        UserNotification notification = userNotificationRepository.findByUser(user);
        UserNotification temp = new UserNotification();
        temp.addNotification(new Notification(message));
        temp.setUser(user);
        if (notification == null)
            notification = userNotificationRepository.save(temp);
        else
            notification.addNotification(new Notification(message));
        this.purgeFromDB(notification);
        return userNotificationRepository.save(notification);
    }

    // Doesnt work, who cares anyway
    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    private void purgeFromDB(UserNotification notification) {
        userNotificationRepository.delete(notification);
    }
}