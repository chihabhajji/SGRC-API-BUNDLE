package bte.sgrc.SpringBackend.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.security.service.VerificationTokenService;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.service.SendingMailService;
import bte.sgrc.SpringBackend.api.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController{
    
    @Autowired
    protected JwtTokenUtil jwbTokenUtil;

    @Autowired 
    private SendingMailService mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    private PasswordEncoder passwordEnconder;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @GetMapping("/verify-email")
    @ResponseBody
    public String verifyEmail(String code) {
        log.info("Got Request");
        return verificationTokenService.verifyEmail(code).getBody();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user,
            BindingResult result) {
        Response<User> response = new Response<User>();
        try {
            validateCreateUser(user, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEnconder.encode(user.getPassword()));
            
            User userPersisted = userService.createOrUpdate(user);
            log.info(userPersisted.toString());      
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
    
    public void validateCreateUser(User user, BindingResult result){
        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email no informed"));
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<User>();
        try {
            validateUpdateUser(user, result);
            if (result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            String passPre = user.getPassword();
            String passO = userService.findByEmail(user.getEmail()).getPassword();
            user.setPassword(passwordEnconder.encode(user.getPassword()));
            if (userFromRequest(request).getProfile() == ProfileEnum.ROLE_ADMIN||!user.getPassword().matches(passO)){
                mailSender.sendMail(user.getEmail(), "BTE : SGRC - Account updated","Your account has been updated by the system administrator, your new password is now :" + passPre);
                // notify him to check email
            }
            User userPesistente = userService.createOrUpdate(user);
            response.setData(userPesistente);


        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    public void validateUpdateUser(User user, BindingResult result){
        if (user.getId() == null){
            result.addError(new ObjectError("User", "Id no informated"));
        }
        if (user.getEmail() == null){
            result.addError(new ObjectError("User", "E-mail no informated"));
        }
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id){
        Response<User> response = new Response<User>();
        User user = userService.findById(id);

        if (user == null){
            response.getErrors().add("Register not fount id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
        Response<String> response = new Response<String>();
        User user = userService.findById(id);

        if (user == null){
            response.getErrors().add("Register not fount id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        mailSender.sendMail(user.getEmail(), "BTE : SGRC - Account deleted", "Your account has been deleted");
        userService.delete(id);
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable("page") Integer page, @PathVariable("count") Integer count){
        Response<Page<User>> response = new Response<Page<User>>();
        Page<User> users = userService.findAll(page, count);
        if (users == null){
            return ResponseEntity.badRequest().body(response);
        } else {
            response.setData(users);
            return ResponseEntity.ok(response);
        }
    }
    
    // TODO: implement Archived count for users, make a CurrentUser version of TicketController summary for technician and their OWN stats
    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwbTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }
    
    @GetMapping(value = "techlist")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<User> getAllTechnicians(){
        return userService.findByRole("ROLE_TECHNICIAN");
    }
}