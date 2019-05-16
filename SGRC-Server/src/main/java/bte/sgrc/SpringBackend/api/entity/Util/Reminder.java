package bte.sgrc.SpringBackend.api.entity.Util;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.entity.Ticket;
import lombok.Getter;
import lombok.Setter;

@Document
public class Reminder {
    @Getter @Setter @Id private String id; 
    @Getter @Setter @DBRef private Ticket ticket;
    @Getter @Setter private LocalDateTime date;
    @Getter @Setter @Max(255) private String message;

}