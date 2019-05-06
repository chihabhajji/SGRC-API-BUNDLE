package bte.sgrc.SpringBackend.api.controller;

import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;
import bte.sgrc.SpringBackend.api.service.UserService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notifications/user")
public class UserNotificationController {

    @Autowired
    private UserNotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    protected JwtTokenUtil jwbTokenUtil;
    
    private static Logger logger = LoggerFactory.getLogger(UserNotificationController.class);

    @GetMapping(value = "{page}/{count}")
	public ResponseEntity<Response<Page<UserNotification>>> getNotificationsByUser(HttpServletRequest request,
            @PathVariable("page") Integer page, @PathVariable("count") Integer count){
        Response<Page<UserNotification>> response = new Response<Page<UserNotification>>();
        User userRequest = userFromRequest(request);
        Page<UserNotification> notifications = notificationService.findByUser(page, count, userRequest.getId());
        response.setData(notifications);
        // TODO: Call func to update all user notifs to seen
		return ResponseEntity.ok().body(response);
    }
     
    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwbTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }
}