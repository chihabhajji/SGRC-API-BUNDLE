package bte.sgrc.SpringBackend.api.entity.Util;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.entity.User;
import io.github.kaiso.relmongo.annotation.ManyToOne;
import lombok.Getter;
import lombok.Setter;


@Document
public class Notification {
    
    @Getter @Setter @Id private Integer notificationId;
    @Getter @Setter private String message;
    @Getter @Setter private Date createdAt; 
    @Getter @Setter private boolean isRead;
    @Getter @Setter @ManyToOne @DBRef(lazy = true) private User user;

    public Notification() {}
    public Notification(String message, Date createdAt, User user) {
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.isRead = false;
    }
}