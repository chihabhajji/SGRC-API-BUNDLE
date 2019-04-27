package bte.sgrc.SpringBackend.api.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;


@Document
public class ChangeStatus{ 
	@Getter @Setter @Id private String id;
	@Getter @Setter @DBRef private Ticket ticket;
	@Getter @Setter @DBRef private User userChange;
    @Getter @Setter private Date dateChangeStatus;
	@Getter @Setter private StatusEnum status;

}