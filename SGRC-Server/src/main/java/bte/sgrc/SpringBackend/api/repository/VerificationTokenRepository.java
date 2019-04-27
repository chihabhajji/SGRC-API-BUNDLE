package bte.sgrc.SpringBackend.api.repository;

import bte.sgrc.SpringBackend.api.security.model.VerificationToken;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    List<VerificationToken> findByUserEmail(String email);
    List<VerificationToken> findByToken(String token);
}