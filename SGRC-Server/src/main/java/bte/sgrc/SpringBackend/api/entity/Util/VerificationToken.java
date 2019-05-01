package bte.sgrc.SpringBackend.api.entity.Util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import bte.sgrc.SpringBackend.api.entity.User;
import io.github.kaiso.relmongo.annotation.CascadeType;
import io.github.kaiso.relmongo.annotation.JoinProperty;
import io.github.kaiso.relmongo.annotation.OneToOne;
import io.github.kaiso.relmongo.config.EnableRelMongo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@EnableRelMongo
@Document
public class VerificationToken {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";
    
    @Getter @Setter @Id private BigInteger id; 
    @Getter @Setter private String token;
    @Getter @Setter private String status;
    @Getter @Setter private LocalDateTime expiredDateTime;
    @Getter @Setter private LocalDateTime issuedDateTime;
    @Getter @Setter private LocalDateTime confirmedDateTime;
    
    
    @OneToOne(cascade = CascadeType.ALL) @JoinProperty(name = "user_id")
    @Getter @Setter @JsonIgnore private User user;
    
    
    public VerificationToken() {
        this.token = UUID.randomUUID().toString();
        this.issuedDateTime = LocalDateTime.now();
        this.expiredDateTime = this.issuedDateTime.plusDays(1);
        this.status = STATUS_PENDING;
    }
}