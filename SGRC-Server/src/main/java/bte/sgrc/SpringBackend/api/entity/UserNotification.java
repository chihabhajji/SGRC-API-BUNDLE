package bte.sgrc.SpringBackend.api.entity;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import lombok.Getter;
import lombok.Setter;

public class UserNotification {
    @Getter @Setter @Id private String notificationId;
    @Getter @Setter @DBRef(lazy = true) private User user;
    @Getter @Setter private Collection<Notification> notification = new ArrayList<Notification>();

    public void addNotification(Notification notification) {
        this.notification.add(notification);
    }

    public void deleteNotification(Notification notification) {
        this.notification.removeIf((Notification temp) -> temp.getNotificationId() == notification.getNotificationId());
    }
}