package bte.sgrc.SpringBackend.api.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.entity.Util.Reminder;
import bte.sgrc.SpringBackend.api.enums.PriorityEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;


@Document
public class Ticket{
     
	@Getter @Setter @Id private String id;
	@Getter @Setter @DBRef(lazy = true) private User user;
	@Getter @Setter @DBRef(lazy = true) private User assignedUser;


    @Getter @Setter private LocalDateTime date;
    @Getter @Setter @Size(min = 6, max = 60)private String title;
    @Getter @Setter @Size(min = 10, max = 255) private String description;
    @Getter @Setter private String image;
    @Getter @Setter private Integer number;
    // Status and Priority enums
    @Getter @Setter private StatusEnum status;
    @Getter @Setter private PriorityEnum priority;

    //Feedback
    @Getter @Setter private String message;
    @Getter @Setter private Integer rating=5;
    // View related bools
    @Getter @Setter private Boolean archived = false;
    @Getter @Setter private Boolean deleted = false;
    @Getter @Setter private Boolean flagged = false;
    // List of changes and reminders
    @Getter @Setter @Transient List<ChangeStatus> changes;
    @Getter @Setter @Transient List<Reminder> reminders;
    // View related bools that are checked on retrival
    @Getter @Setter @Transient private Boolean changesEmpty = true;
    @Getter @Setter @Transient private Boolean remindersEmpty = true;
    @Getter @Setter @Transient private Boolean reminded = false;
    @Getter @Setter @Transient private Boolean overdue = false;


}