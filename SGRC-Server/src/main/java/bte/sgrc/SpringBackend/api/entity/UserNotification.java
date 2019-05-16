package bte.sgrc.SpringBackend.api.entity;

import java.util.Stack;

import org.springframework.data.annotation.Id;

import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import lombok.Getter;
import lombok.Setter;

public class UserNotification {
    @Getter @Setter @Id private String notificationId;
    @Getter @Setter private String userId;
    @Getter @Setter private Stack<Notification> notification = new Stack<Notification>();

    public void addNotification(Notification notification) {
        this.notification.push(notification);
    }

    public void deleteNotification(Notification notification) {
        this.notification.removeIf((Notification temp) -> temp.getNotificationId() == notification.getNotificationId());
    }
}