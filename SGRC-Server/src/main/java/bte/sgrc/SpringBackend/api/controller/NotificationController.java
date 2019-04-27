package bte.sgrc.SpringBackend.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;
import bte.sgrc.SpringBackend.api.service.NotificationService;
import io.swagger.annotations.ApiOperation;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private static Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public NotificationController() {
    }
    
    @RequestMapping(value="/notifications/user",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getNotificationsByUser(@RequestParam(required=false,defaultValue="") String limit, @RequestBody User user){

		logger.debug("inside getNotificationsByUser api for fetch user notification ");
		Integer intLimit = Integer.parseInt(limit);
		notificationService.findByUser(user, intLimit);
		return ResponseEntity.ok().body("NotificationFethced" + HttpStatus.OK);
	}
    
	// TODO : dont flame me, but idk what the Req_UpdateNotitfication payload is but i used it ( big thumbs up), oh and look up patch method angular side
    @RequestMapping(value="/notifications/user",method=RequestMethod.PATCH,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateUserNotification(@RequestHeader @Valid @RequestBody Req_UpdateNotitfication reqUpdateNotitfication){

		logger.debug("inside updateUserNotification api ");
		Map<String,Object> response = notificationService.updateUserNotification(reqUpdateNotitfication,reqUpdateNotitfication.getUser());
		
		if((boolean)response.get("error") == true){
			return ResponseEntity.badRequest().body(response.get("message").toString()+(HttpStatus) response.get("status"));
		}
                 
		return ResponseEntity.ok().body(response.get("data") + response.get("message").toString() + HttpStatus.OK);
	}

}