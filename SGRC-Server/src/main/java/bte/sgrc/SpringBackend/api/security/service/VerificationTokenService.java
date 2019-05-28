package bte.sgrc.SpringBackend.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.VerificationToken;
import bte.sgrc.SpringBackend.api.repository.UserRepository;
import bte.sgrc.SpringBackend.api.repository.VerificationTokenRepository;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.service.SendingMailService;
import bte.sgrc.SpringBackend.api.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SendingMailService sendingMailService;

    @Autowired
    public VerificationTokenService(UserRepository userRepository,
            VerificationTokenRepository verificationTokenRepository, SendingMailService sendingMailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.sendingMailService = sendingMailService;
    }

    public void createVerification(User user) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserId(user.getId());
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

    public void deleteVerifications(User user) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserId(user.getId());
        for (VerificationToken vt : verificationTokens){
               verificationTokenRepository.delete(vt);
        }
    }

    public ResponseEntity<Response<String>> verifyEmail(String token) {
        Response<String> response = new Response<String>();
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
        if (verificationTokens.isEmpty()) {
            response.getErrors().add("Invalid token");
            return ResponseEntity.badRequest().body(response);
        }
        
        VerificationToken verificationToken = verificationTokens.get(0);
        // no resend logic implemented yet
        /**
         * if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
         * response.getErrors().add("Token expired"); return
         * ResponseEntity.unprocessableEntity().body(response); }
         */
        
        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        verificationTokenRepository.save(verificationToken);

        User user = userService.findByEmail(verificationToken.getUser().getEmail());

        if (user.getIsActive()) {
            response.getErrors().add("Email already verified");
            return ResponseEntity.badRequest().body(response);
        }
        user.setIsActive((true));
        userService.createOrUpdate(user);
        
        response.setData("You have successfully verified your email address.");
        return ResponseEntity.ok(response);
    }
}