package bte.sgrc.SpringBackend.api.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.security.model.VerificationToken;
import io.github.kaiso.relmongo.annotation.CascadeType;
import io.github.kaiso.relmongo.annotation.FetchType;
import io.github.kaiso.relmongo.annotation.OneToOne;
import lombok.Getter;
import lombok.Setter;
// TODO : add name property here and in angular client
@Document
public class User{
    @Id @Getter @Setter private String id;
    @Indexed(unique = true) @NotBlank(message = "E-mail required") @Email(message = "E-mail invalid") @Getter @Setter private String email;
	@NotBlank(message = "Password required") @Size(min = 6) @Getter @Setter private  String password;
	@Getter @Setter private  ProfileEnum profile;
	@Getter @Setter private  Boolean isActive = false;
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Getter @Setter @JsonIgnore private VerificationToken verificationToken;
} 