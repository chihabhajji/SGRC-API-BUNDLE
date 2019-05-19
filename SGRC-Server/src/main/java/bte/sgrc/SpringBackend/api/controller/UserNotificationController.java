package bte.sgrc.SpringBackend.api.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.service.TicketService;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;
import bte.sgrc.SpringBackend.api.service.UserService;
import bte.sgrc.SpringBackend.api.entity.Util.Reminder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notifications")
public class UserNotificationController {
    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    protected JwtTokenUtil jwbTokenUtil;

    @Autowired
    private TicketService ticketService;

    @GetMapping(value = "{userId}")
    public ResponseEntity<Response<Collection<Notification>>> findAll(@PathVariable("userId") String userId) {
        Response<Collection<Notification>> response = new Response<Collection<Notification>>();
        try {
            UserNotification userNotifications = notificationService.findByUser(userId);
            Stack<Notification> notifications = userNotifications.getNotification();
            Collections.reverse(notifications);
            response.setData(notifications);
            
        } catch ( NullPointerException e){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "{message}")
    public ResponseEntity<Response<Ticket>> reminder(HttpServletRequest request,@PathVariable("message")  String message,@RequestBody Ticket ticket)
    {
        Response<Ticket> response = new Response<Ticket>();
        if(ticket.getAssignedUser()==null){
            for (User admin : userService.findByRole(ProfileEnum.ROLE_ADMIN.name())){
              notificationService.notifyUser(admin.getId(),
                                   ticket.getId(),
                                   "Reminder : Ticket" + ticket.getNumber() + " is pending");   
            }
        }
        else{
        notificationService.notifyUser(ticket.getAssignedUser().getId(),
                                   ticket.getId(),
                                   "Reminder : Ticket" + ticket.getNumber() + " is awaiting resolution");       
        }

        Reminder reminder =new Reminder();
        reminder.setMessage(message);
        reminder.setDate(LocalDateTime.now());
        reminder.setTicket(ticket);
        ticketService.createReminder(reminder);
    
        return ResponseEntity.ok().body(response);
   }
     
}