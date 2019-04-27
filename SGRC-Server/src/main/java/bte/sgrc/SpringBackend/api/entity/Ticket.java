package bte.sgrc.SpringBackend.api.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import bte.sgrc.SpringBackend.api.enums.PriorityEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;



public class Ticket{
     
	@Getter @Setter @Id private String id;
	@Getter @Setter @DBRef(lazy = true) private User user;
	@Getter @Setter @DBRef(lazy = true) private User assignedUser;
	@Getter @Setter @Transient  List<ChangeStatus> changes;
    @Getter @Setter private Date date;
    @Getter @Setter private String title;
    @Getter @Setter private Integer number;
    @Getter @Setter private StatusEnum status;
    @Getter @Setter private PriorityEnum priority;
    @Getter @Setter private String description;
    @Getter @Setter private String image;	
}