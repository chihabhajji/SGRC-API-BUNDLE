package bte.sgrc.SpringBackend.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.VerificationToken;
import bte.sgrc.SpringBackend.api.repository.UserRepository;
import bte.sgrc.SpringBackend.api.repository.VerificationTokenRepository;
import bte.sgrc.SpringBackend.api.service.SendingMailService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationTokenService {
    private VerificationTokenRepository verificationTokenRepository;
    private SendingMailService sendingMailService;

    @Autowired
    public VerificationTokenService(UserRepository userRepository,
            VerificationTokenRepository verificationTokenRepository, SendingMailService sendingMailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.sendingMailService = sendingMailService;
    }

    public void createVerification(User user) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserEmail(user.getEmail());
        VerificationToken verificationToken;
        if (verificationTokens.isEmpty()) {
            verificationToken = new VerificationToken();
            verificationToken.setUser(user);
            verificationTokenRepository.save(verificationToken);
        } else {
            verificationToken = verificationTokens.get(0);
        }

        sendingMailService.sendVerificationMail(user.getEmail(), verificationToken.getToken());
    }

    public ResponseEntity<String> verifyEmail(String token) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
        if (verificationTokens.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }

        VerificationToken verificationToken = verificationTokens.get(0);
        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.unprocessableEntity().body("Expired token.");
        }

        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        verificationToken.getUser().setIsActive(true);
        verificationTokenRepository.save(verificationToken);

        return ResponseEntity.ok("You have successfully verified your email address.");
    }
}