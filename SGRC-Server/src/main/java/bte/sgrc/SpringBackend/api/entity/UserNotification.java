package bte.sgrc.SpringBackend.api.entity;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.annotation.Id;

import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import lombok.Getter;
import lombok.Setter;


public class UserNotification {
    @Getter @Setter @Id private String notificationId;
    @Getter @Setter private User user;
    //@OrderBy("createdAt")
    @Getter @Setter private Collection<Notification> notification = new ArrayList<Notification>();;
    
    public UserNotification(){}
    public UserNotification(User user, Notification notification){
        this.user= user;
        this.notification.add(notification);
    }

    public void addNotification(Notification notification){
        this.notification.add(notification);
    }
    public void deleteNotification(Notification notification){
        this.notification.removeIf((Notification temp) -> temp.getNotificationId() == notification.getNotificationId());
    }
}