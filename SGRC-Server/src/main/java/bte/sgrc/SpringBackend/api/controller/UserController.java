package bte.sgrc.SpringBackend.api.controller;

import java.security.Principal;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.security.service.VerificationTokenService;
import bte.sgrc.SpringBackend.api.dto.UserSummary;
import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import bte.sgrc.SpringBackend.api.service.SendingMailService;
import bte.sgrc.SpringBackend.api.service.TicketService;
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

    @Autowired
    private TicketService ticketService;
    
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

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
            mailSender.sendMail(user.getEmail(), "BTE : SGRC - Account created","Your account has been created by the system administrator, your new password is now :"+ user.getPassword()+" please change it when you first login!");
        
            user.setIsDue(true);
            user.setIsActive(true);

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
        return ResponseEntity.ok(response);
    }
    
    public void validateCreateUser(User user, BindingResult result){
        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email no informed"));
        }
        if (user.getName() == null){
            result.addError(new ObjectError("User", "Name not set"));
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<User>();
        try {
            validateUpdateUser(user, result);
            if (result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            // TODO : do the same when reseting pw
            if (!passwordEnconder.matches(user.getPassword(), userService.findByEmail(user.getEmail()).getPassword())){
                if (userFromRequest(request).getProfile().equals(ProfileEnum.ROLE_ADMIN)&&(!userFromRequest(request).getEmail().equals(user.getEmail()))){
                    mailSender.sendMail(user.getEmail(), "BTE : SGRC - Account updated","Your account has been updated by the system administrator, your new password is now :" + user.getPassword());
                    user.setIsDue(true);
                }else {
                    user.setIsDue(false);
                }
                user.setPassword(passwordEnconder.encode(user.getPassword()));
            }else {
                response.getErrors().add("Please provide a new password!");
                return ResponseEntity.badRequest().body(response);
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
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','TECHNICIAN')")
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
        verificationTokenService.deleteVerifications(user);
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
    
    // TODO: CurrentUser version of TicketController summary for technician and their OWN stats
    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwbTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }
    
    @GetMapping(value = "techlist")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<List<User>>> getAllTechnicians(){
        Response<List<User>> response = new Response<List<User>>();
        List<User> agents = userService.findByRole(ProfileEnum.ROLE_TECHNICIAN.name());

        response.setData(agents);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/summary/{userId}/{year}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<Response<UserSummary>> findSummary(
            @PathVariable("userId") String userId,
            @PathVariable("year") int year) {
        logger.info("im here");
        Response<UserSummary> response = new Response<UserSummary>();
        logger.info("im here");
        UserSummary userSummary = new UserSummary();
        logger.info("im here");
        Iterable<Ticket> tickets;
        logger.info("im here");
        User user = userService.findById(userId);
        logger.info("im here");
        if(user.getProfile().equals(ProfileEnum.ROLE_ADMIN)){
            logger.info("Admin");
            tickets = ticketService.findall();
        } else if (user.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
            logger.info("Tech");
            tickets = ticketService.findByTechnician(userId);
        } else if (user.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
            logger.info("Customer");
            tickets = ticketService.findByUser(userId);
        } else {
            tickets = null;
            response.getErrors().add("Hola amigo , no no no");
            return ResponseEntity.badRequest().body(response);
        }

        if (tickets != null) {
            for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
                Ticket ticket = iterator.next();
                if(ticket.getDate().getYear()==year){
                    switch (ticket.getDate().getMonth()) {
                        case OCTOBER : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getOctober().setAmountApproved(userSummary.getOctober().getAmountApproved()+1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getOctober().setAmountDisaproved(userSummary.getOctober().getAmountDisaproved()+1);
                            }
                            break;
                         } 
                        case JULY: {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                            userSummary.getJuly().setAmountDisaproved(userSummary.getJuly().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getJuly().setAmountDisaproved(userSummary.getJuly().getAmountDisaproved()+1);
                            }
                            break;
                        }
                        case JUNE : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getJune().setAmountDisaproved(userSummary.getJune().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getJune().setAmountDisaproved(userSummary.getJune().getAmountDisaproved() + 1);
                            }
                            break;
                        } 
                        case APRIL: {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getApril().setAmountDisaproved(userSummary.getApril().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getApril().setAmountDisaproved(userSummary.getApril().getAmountDisaproved() + 1);
                            }
                            break;
                        }         
                        case MAY : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getMay().setAmountDisaproved(userSummary.getMay().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getMay().setAmountDisaproved(userSummary.getMay().getAmountDisaproved() + 1);
                            }
                            break;
                        }         
                        case SEPTEMBER : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getSeptember().setAmountDisaproved(userSummary.getSeptember().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getSeptember().setAmountDisaproved(userSummary.getSeptember().getAmountDisaproved() + 1);
                            }
                            break;
                        }         
                        case DECEMBER : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getDecember().setAmountDisaproved(userSummary.getDecember().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getDecember().setAmountDisaproved(userSummary.getDecember().getAmountDisaproved() + 1);
                            }
                            break;
                        }   
                        case AUGUST : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getAugust().setAmountDisaproved(userSummary.getAugust().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getAugust().setAmountDisaproved(userSummary.getAugust().getAmountDisaproved() + 1);
                            }
                            break;
                        }
                        case NOVEMBER : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getNovember().setAmountDisaproved(userSummary.getNovember().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getNovember().setAmountDisaproved(userSummary.getNovember().getAmountDisaproved() + 1);
                            }
                            break;
                        }
                        case FEBRUARY : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getFebruary().setAmountDisaproved(userSummary.getFebruary().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getFebruary().setAmountDisaproved(userSummary.getFebruary().getAmountDisaproved() + 1);
                            }
                            break;
                        }
                        case MARCH : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getMarch().setAmountDisaproved(userSummary.getMarch().getAmountDisaproved() + 1);    
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getMarch().setAmountDisaproved(userSummary.getMarch().getAmountDisaproved() + 1);
                            }
                            break;
                        } 
                        case JANUARY : {
                            if (ticket.getStatus().equals(StatusEnum.Closed)|| ticket.getStatus().equals(StatusEnum.Approved)) {
                                userSummary.getJanuary().setAmountDisaproved(userSummary.getJanuary().getAmountDisaproved() + 1);
                            }
                            if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                                userSummary.getJanuary().setAmountDisaproved(userSummary.getJanuary().getAmountDisaproved() + 1);
                            }
                            break;
                        }
                    }
                }
            }
        }

        response.setData(userSummary);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{name}/{profile}/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findByParams(HttpServletRequest request,
            @PathVariable("page") Integer page, @PathVariable("count") Integer count,
            @PathVariable("name") String name,
            @PathVariable("profile") String profile, @PathVariable("email") String email) {

        name = name.equals("uninformed") ? "" : name;
        profile = profile.equals("uninformed") ? "" : profile;
        email = email.equals("uninformed") ? "" : email;
        logger.info(name + " " + profile +" "+ email );
        Response<Page<User>> response = new Response<Page<User>>();
        Page<User> users = null;
        users = userService.findByParameters(page, count, name, profile, email);
        response.setData(users);
        logger.info(users.toString());
        return ResponseEntity.ok(response);
    }

}
    