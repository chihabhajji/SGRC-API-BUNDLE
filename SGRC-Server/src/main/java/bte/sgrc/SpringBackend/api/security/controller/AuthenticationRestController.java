package bte.sgrc.SpringBackend.api.security.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.mongodb.DuplicateKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtAuthenticationRequest;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.security.jwt.WebSecurityConfig;
import bte.sgrc.SpringBackend.api.security.model.CurrentUser;
import bte.sgrc.SpringBackend.api.security.service.VerificationTokenService;
import bte.sgrc.SpringBackend.api.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationRestController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    private PasswordEncoder passwordEnconder;

    private static Logger logger = LoggerFactory.getLogger(AuthenticationRestController.class);

    @PostMapping(value = "/api/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest)
            throws Exception {

        final Authentication authentication = webSecurityConfig.authenticationManagerBean()
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final User user = userService.findByEmail(authenticationRequest.getEmail());
        user.setPassword(null);
        return ResponseEntity.ok(new CurrentUser(token, user));
    }

    @PostMapping(value = "/api/refresh")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = jwtTokenUtil.getUsernameFromToken(token);
        final User user = userService.findByEmail(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token)) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new CurrentUser(refreshedToken, user));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PostMapping( value="/api/auth/register")
    public ResponseEntity<Response<User>> register(HttpServletRequest request, @RequestBody User user,
            BindingResult result) {
        Response<User> response = new Response<User>();
        try {
            user.setId(UUID.randomUUID().toString());
            validateCreateUser(user, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            if (userService.findByEmail(user.getEmail())!=null){
                response.getErrors().add("E-mail already registered");
                return ResponseEntity.badRequest().body(response);
            }

            user.setProfile(ProfileEnum.ROLE_CUSTOMER);
            user.setPassword(passwordEnconder.encode(user.getPassword()));
            User userPersisted = userService.createOrUpdate(user);
            response.setData(userPersisted);
        } catch (DuplicateKeyException dE) {
            response.getErrors().add("E-mail already registered");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        verificationTokenService.createVerification(user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/auth/verify-email/{code}")
    public ResponseEntity<Response<String>> verifyEmail(@PathVariable("code") String code) {
        return verificationTokenService.verifyEmail(code);
    }

    public void validateCreateUser(User user, BindingResult result) {
        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email no informed"));
        }
        if (user.getName() == null) {
            result.addError(new ObjectError("User", "Name not set"));
        }
    }

}