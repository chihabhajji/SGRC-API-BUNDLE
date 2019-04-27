package bte.sgrc.SpringBackend.api.security.model;

import bte.sgrc.SpringBackend.api.entity.User;
import lombok.Getter;
import lombok.Setter;

public class CurrentUser{

    @Getter @Setter private String token;
    @Getter @Setter private User user;
    public CurrentUser(String token, User user){
        this.setToken(token);
        this.setUser(user);
    }
}