package bte.sgrc.SpringBackend.api.security.model;

import java.util.List;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import lombok.Getter;
import lombok.Setter;

public class CurrentUser{

    @Getter @Setter private String token;
    @Getter @Setter private User user;
    @Getter @Setter private List<Notification> notifications;
    public CurrentUser(String token, User user, List<Notification> notifications){
        this.setToken(token);
        this.setUser(user);
        this.setNotifications(notifications);
    }
}