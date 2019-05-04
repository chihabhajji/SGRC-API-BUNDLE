package bte.sgrc.SpringBackend.api.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.UserNotification;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notifications/user")
public class UserNotificationController {

    @Autowired
    private UserNotificationService notificationService;

    private static Logger logger = LoggerFactory.getLogger(UserNotificationController.class);

    public UserNotificationController() {
    }
    /*
	@RequestMapping(method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	// TODO : Create reponse of type notification and change object to notif
	public ResponseEntity<Response<Page<UserNotification>>> getNotificationsByUser(@RequestParam(required=false,defaultValue="") String limit, @RequestBody User user){

		return ResponseEntity.ok().body(new );
	}
    */
     
}