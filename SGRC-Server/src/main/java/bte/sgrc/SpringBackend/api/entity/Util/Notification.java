package bte.sgrc.SpringBackend.api.entity.Util;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.github.kaiso.relmongo.config.EnableRelMongo;
import lombok.Getter;
import lombok.Setter;

@EnableRelMongo
@Document
public class Notification {
    @Getter @Setter private String ticket;
    @Getter @Setter @Id private String notificationId;
    @Getter @Setter private String message;
    @Getter @Setter private Date createdAt; 
    @Getter @Setter private boolean isRead;

    public Notification() {}
    public Notification(String ticket,String message) {
        this.ticket = ticket;
        this.message = message;
        this.createdAt = new Date();
        this.isRead = false;
    }
}