package bte.sgrc.SpringBackend.api.controller;

import bte.sgrc.SpringBackend.api.dto.Summary;
import bte.sgrc.SpringBackend.api.entity.ChangeStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Reminder;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import bte.sgrc.SpringBackend.api.repository.UserRepository;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;
import bte.sgrc.SpringBackend.api.service.SendingMailService;
import bte.sgrc.SpringBackend.api.service.TicketService;
import bte.sgrc.SpringBackend.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController{
    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwbTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    SendingMailService mailSender;


    private static Logger logger = LoggerFactory.getLogger(TicketController.class);

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, 
                        @RequestBody 
                        Ticket ticket, 
                        BindingResult result){
        Response<Ticket> response = new Response<Ticket>();
        try {
            validadeCreateTicket(ticket, result);
            if (result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            if (!ticket.getImage().isEmpty())
            if(!ticket.getImage().substring(0,10).equalsIgnoreCase("data:image")){
                            response.getErrors().add("File is not an image");
                            return ResponseEntity.badRequest().body(response);
            }
            ticket.setStatus(StatusEnum.getStatus("New"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate((LocalDateTime.now()));
            ticket.setNumber(generateNumber());
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
            
            for (User admin : userService.findByRole(ProfileEnum.ROLE_ADMIN.name())) {
                notificationService.notifyUser(admin.getId(), ticket.getId(),
                        "Ticket :" + ticket.getNumber() + " has been created");
            }
        } catch(Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, 
                    @RequestBody Ticket ticket, 
                    BindingResult result){
        Response<Ticket> response = new Response<Ticket>();
        try{
            validateUpdateTicket(ticket, result);
            if (result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Ticket ticketCurrent = ticketService.findById(ticket.getId());
                ticket.setStatus(ticketCurrent.getStatus());
                ticket.setUser(ticketCurrent.getUser());
                ticket.setDate(ticketCurrent.getDate());
                ticket.setNumber(ticketCurrent.getNumber());
                    if (ticketCurrent.getAssignedUser() != null){
                        ticket.setAssignedUser(userFromRequest(request));
                    }
                Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
                response.setData(ticketPersisted);
                notificationService.notifyUser(
                    userRepository.findByEmail(ticketPersisted.getUser().getEmail()).getId(), 
                    ticketPersisted.getId(),
                    "Ticket number :"+ticket.getNumber()+" has been updated");
        } catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN','ADMIN')")
    public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id){
        Response<Ticket> response = new Response<Ticket>();
        Ticket ticket = ticketService.findById(id);

        if (ticket == null){
            response.getErrors().add("Register not found id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        
        List<ChangeStatus> changes = new ArrayList<ChangeStatus>();
        Iterable<ChangeStatus> changesCurrent = ticketService.listaChangeStatus(ticket.getId());
        for (Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext();){
            ChangeStatus changeStatus = iterator.next();
            changeStatus.setTicket(null);
            changes.add(changeStatus);
        }
        ticket.setChanges(changes);
        if (!changes.isEmpty()){
            ticket.setChangesEmpty(false);
        }

        List<Reminder> reminders = new ArrayList<Reminder>();
        Iterable<Reminder> remindersCurrent = ticketService.listReminders(ticket.getId());
        for (Iterator<Reminder> iterator = remindersCurrent.iterator(); iterator.hasNext();){
            Reminder reminder = iterator.next();
            reminder.setTicket(null);
            reminders.add(reminder);
        }
        ticket.setReminders(reminders);
        if (!ticket.getReminders().isEmpty()){
            ticket.setRemindersEmpty(false);
        }


        // Checking if user reminded in last 2 days and if the ticket is overdue for a reminder
        if (ticket.getStatus().equals(StatusEnum.New))
            if (LocalDateTime.now().isAfter(ticket.getDate().plusDays(2)))
                ticket.setOverdue(true);
            else
                ticket.setOverdue(false);

        if (ticket.getStatus().equals(StatusEnum.Assigned))
            if (LocalDateTime.now().isAfter(ticket.getChanges().get(ticket.getChanges().size()-1).getDateChangeStatus().plusDays(2)))
                ticket.setOverdue(true);
            else
                ticket.setOverdue(false);

        if (!ticket.getReminders().isEmpty())
            if (LocalDateTime.now().isAfter(ticket.getReminders().get(ticket.getReminders().size()-1).getDate().plusDays(2)))
                ticket.setReminded(false);
            else
                ticket.setReminded(true);

        response.setData(ticket);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
        Response<String> response = new Response<String>();
        Ticket ticket = ticketService.findById(id);
        User user = userRepository.findByEmail(ticket.getUser().getEmail());
       
        if (ticket.equals(null)){
            response.getErrors().add("Register not found id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        ticket.setDeleted(true);
        // Delete if new, soft delete if closed
        if(ticket.getStatus().equals(StatusEnum.New))
        ticketService.delete(ticket);
        else if(ticket.getStatus().equals(StatusEnum.Closed)||ticket.getStatus().equals(StatusEnum.Rejected)||ticket.getStatus().equals(StatusEnum.Approved))
        ticketService.createOrUpdate(ticket);
        else{
            response.getErrors().add("Record not eligable to be deleted");
            return ResponseEntity.badRequest().body(response);
        }
        notificationService.notifyUser( user.getId(),
                                        ticket.getId(),
                                        "You have deleted ticket number :" + ticket.getNumber());
        response.setData("Succesfully deleted");
        return ResponseEntity.ok(response);
    }
    

    @RequestMapping(value = "archive/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<String>> archive(@PathVariable("id") String id) {
        Response<String> response = new Response<String>();
        Ticket ticket = ticketService.findById(id);
        User user = userRepository.findByEmail(ticket.getUser().getEmail());

        if (ticket.getId() == null) {
            response.getErrors().add("Register not found id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        ticket.setArchived(true);
        ticketService.createOrUpdate(ticket);
        notificationService.notifyUser(user.getId(),ticket.getId(), "Your ticket number :" + ticket.getNumber() + " has been archived!");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request,
            @PathVariable("page") Integer page, @PathVariable("count") Integer count) {
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);
        
        if (userRequest.getProfile().equals(ProfileEnum.ROLE_ADMIN)) {
            tickets = ticketService.listTicket(page, count);
        } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId()); // Returns list of non archived and non soft deleted
        } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
            tickets = ticketService.findByAssignedUser(page, count, userRequest.getId());
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "archived/{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<Response<Page<Ticket>>> findAllArchived(HttpServletRequest request,
            @PathVariable("page") Integer page, @PathVariable("count") Integer count) {
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);
        
        tickets = ticketService.findByCurrentUserArchived(page, count, userRequest.getId()); // Returns list of archived
        
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN','ADMIN')")
    public ResponseEntity<Response<Ticket>> changeStatus(HttpServletRequest request, 
                    @PathVariable("id") String id, 
                    @PathVariable("status") String status,
                    @RequestBody Ticket ticket,
                    BindingResult result){
        Response<Ticket> response = new Response<Ticket>();
        try {
            validateChangeStatus(id, status, result);
            if (result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Ticket ticketCurrent = ticketService.findById(id);
            ticketCurrent.setStatus(StatusEnum.getStatus(status));
            if (ticketCurrent.getStatus().equals(StatusEnum.Flagged))
            ticketCurrent.setFlagged(true);
            if (status.equals("Assigned")){
                if(ticket.getAssignedUser()==null){
                  response.getErrors().add("Please select an agent to assign");
                return ResponseEntity.badRequest().body(response);
                }
                ticketCurrent.setAssignedUser(ticket.getAssignedUser());
                ticketCurrent.setReminded(false);

            }
            User userFR = userFromRequest(request);
            if (ticketCurrent.getStatus().equals(StatusEnum.Rejected)|| ticketCurrent.getStatus().equals(StatusEnum.Resolved) && (!userFR.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)))
            ticketCurrent.setMessage(ticket.getMessage());
            Ticket ticketPersisted = ticketService.createOrUpdate(ticketCurrent);
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUserChange(userFromRequest(request));
            changeStatus.setDateChangeStatus(LocalDateTime.now());
            changeStatus.setStatus(StatusEnum.getStatus(status));
            changeStatus.setTicket(ticketPersisted);
            changeStatus.setMessage(ticket.getMessage());
            ticketService.createChangeStatus(changeStatus);
            
            String message;
            if (ticket.getStatus()!=changeStatus.getStatus())
            switch (changeStatus.getStatus()) {
                case Assigned: {
                    notificationService.notifyUser(userRepository.findByEmail(ticketCurrent.getAssignedUser().getEmail()).getId(),
                            ticketCurrent.getId(),
                            "You have been assigned with ticket number " + ticketCurrent.getNumber());
                    break;
                }
                case New: {
                    break;
                }
                case Flagged:{
                    for (User admin : userService.findByRole(ProfileEnum.ROLE_ADMIN.name())) {
                        notificationService.notifyUser(admin.getId(), ticket.getId(),
                        "Ticket :" + ticket.getNumber() + " has been flagged for rejection");
                    }
                    break;
                }
                case Approved: {
                    notificationService.notifyUser(userRepository.findByEmail(ticketCurrent.getAssignedUser().getEmail()).getId(), ticketCurrent.getId(),
                    " Ticket number "+ticket.getNumber()+" has been approved");
                    break;
                }
                case Disapproved: {
                    notificationService.notifyUser(userRepository.findByEmail(ticketCurrent.getAssignedUser().getEmail()).getId(), ticketCurrent.getId(),
                    " Ticket number : "+ticket.getNumber()+" has been dissaproved ,please review it again");
                    
                    break;
                }
                case Closed: {
                    notificationService.notifyUser(userRepository.findByEmail(ticket.getUser().getEmail()).getId(),
                            ticketCurrent.getId(),
                            "Ticket number : " + ticket.getNumber() + " is now closed");
                    break;
                }
                case Resolved:{
                    message = "Ticket number : " + ticket.getNumber() + " is now resolved, please refer to it to confirm or dissaprove it! ";
                    notificationService.notifyUser(userRepository.findByEmail(ticket.getUser().getEmail()).getId(),
                            ticketCurrent.getId(),message);
                    mailSender.sendMail(ticket.getUser().getEmail(), "Ticket number "+ticket.getNumber(),message);
                    break;
                }
                case Rejected:{
                    message = "Ticket number : " + ticket.getNumber()+ " has been rejected! ";
                    notificationService.notifyUser(userRepository.findByEmail(ticket.getUser().getEmail()).getId(),ticketCurrent.getId(), message);
                    mailSender.sendMail(ticket.getUser().getEmail(), "Ticket number " + ticket.getNumber(), message);
                    break;
                }
            }
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/summary")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<Response<Summary>> findSummary(){
        Response<Summary> response = new Response<Summary>();
        Summary summary = new Summary();
        Integer amountNew = 0;
        Integer amountResolved = 0;
        Integer amountApproved = 0;
        Integer amountDisapproved = 0;
        Integer amountAssigned = 0;
        Integer amountClosed = 0;

        Iterable<Ticket> tickets = ticketService.findall();
        if (tickets != null) {
            for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();){
                Ticket ticket = iterator.next();
                if (ticket.getStatus().equals(StatusEnum.New)){
                    amountNew++;
                }
                if (ticket.getStatus().equals(StatusEnum.Resolved)){
                    amountResolved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Approved)){
                    amountApproved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Disapproved)){
                    amountDisapproved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Assigned)){
                    amountAssigned++;
                }
                if (ticket.getStatus().equals(StatusEnum.Closed)){
                    amountClosed++;
                }
            }
        }
        summary.setAmountNew(amountNew);
        summary.setAmountResolved(amountResolved);
        summary.setAmountApproved(amountApproved);
        summary.setAmountDisapproved(amountDisapproved);
        summary.setAmountAssigned(amountAssigned);
        summary.setAmountClosed(amountClosed);
        response.setData(summary);
        return ResponseEntity.ok(response);
    }


    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwbTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    private void validateChangeStatus(String id, String status, BindingResult result) {
        if (id == null || id.equals("")) {
            result.addError(new ObjectError("Ticket", "Id missing ? huh how ?"));
            return;
        }
        if (status == null || status.equals("")) {
            result.addError((new ObjectError("Ticket", "Title missing")));
            return;
        }
    }
    
    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,
            @PathVariable("page") Integer page, @PathVariable("count") Integer count,
            @PathVariable("number") Integer number, @PathVariable("title") String title,
            @PathVariable("status") String status, @PathVariable("priority") String priority,
            @PathVariable("assigned") Boolean assigned) {

        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;
        // THANK GOD FOR GITHUB AND STACKOVERFLOW <3 <3 <3 <3 <3 <3
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;

        if (number > 0) {
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            User userRequest = userFromRequest(request);

            if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
                tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority,userRequest.getId());
            } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
                tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority,
                        userRequest.getId());
            } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_ADMIN)) {
                tickets = ticketService.findByParameters(page, count, title, status, priority);
            }
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Ticket title is missing !!"));
            return;
        }
        if (ticket.getId() == null) {
            result.addError(new ObjectError("Ticket",
                    "Ticket id is missing, unless you're using curl or ARC to try this, i doubt youll see this message so..... Lord ned stark dies !! nyahahahahahahahaahaha"));
            return;
        }
    }
    
    private void validadeCreateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getTitle() == null || ticket.getDescription() == null) {
            result.addError(new ObjectError("Ticket", "Title or Description missing"));
            return;
        }

    }
}