package bte.sgrc.SpringBackend.api.entity;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import lombok.Getter;
import lombok.Setter;

@Document
public class User{
	
	@Id @Getter @Setter private String id;
	@Getter @Setter @NotBlank(message = "Display name required") @Size(min = 10) private String name;
	@Indexed(unique = true) @NotBlank(message = "E-mail required") @Email(message = "E-mail invalid") 
	@Getter @Setter private String email;
	@NotBlank(message = "Password required") @Size(min = 6) @Getter @Setter private  String password;
	@Getter @Setter private  ProfileEnum profile;
	@Getter @Setter private  Boolean isActive = false;
    @Getter @Setter @Transient List<Notification> notifications;
} 