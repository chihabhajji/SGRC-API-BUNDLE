package bte.sgrc.SpringBackend.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import bte.sgrc.SpringBackend.api.entity.Util.VerificationToken;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    List<VerificationToken> findByUserId(String userId);
    List<VerificationToken> findByToken(String token);
    
}