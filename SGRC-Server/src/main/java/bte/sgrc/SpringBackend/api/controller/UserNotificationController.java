package bte.sgrc.SpringBackend.api.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;
import bte.sgrc.SpringBackend.api.service.UserService;

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

    @GetMapping(value = "{userId}")
    public ResponseEntity<Response<Collection<Notification>>> findAll(@PathVariable("userId") String userId) {
        Response<Collection<Notification>> response = new Response<Collection<Notification>>();
        
        if(userId.equals(""))
        ResponseEntity.badRequest().body("no no no");
        User user = userService.findById(userId);
        if(user==null)
        ResponseEntity.badRequest().body("User not found");
        UserNotification userNotifications = notificationService.findByUser(user);
        
        Collection<Notification> notifications = userNotifications.getNotification();
 
        response.setData(notifications);
        return ResponseEntity.ok(response);
    }
     
}