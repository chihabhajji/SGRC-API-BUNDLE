package bte.sgrc.SpringBackend.api.controller;

import bte.sgrc.SpringBackend.api.dto.Summary;
import bte.sgrc.SpringBackend.api.entity.ChangeStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

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
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import bte.sgrc.SpringBackend.api.response.Response;
import bte.sgrc.SpringBackend.api.security.jwt.JwtTokenUtil;
import bte.sgrc.SpringBackend.api.service.TicketService;
import bte.sgrc.SpringBackend.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController{

    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwbTokenUtil;

    @Autowired
    private UserService userService;

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
            ticket.setStatus(StatusEnum.getStatus("New"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate((new Date()));
            ticket.setNumber(generateNumber());
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch(Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validadeCreateTicket(Ticket ticket, BindingResult result){
        if (ticket.getTitle()== null||ticket.getDescription()== null){
            result.addError(new ObjectError("Ticket", "Title or Description on informed"));
            return;
        }

    }

    public User userFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String email = jwbTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber(){
        Random random = new Random();
        return random.nextInt(9999);
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
                ticket.setAssignedUser(ticketCurrent.getAssignedUser());
            }
            Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result){
        if (ticket.getTitle() == null){
            result.addError(new ObjectError("Ticket", "Title no informed"));
            return;
        }
        if (ticket.getId() == null){
            result.addError(new ObjectError("Ticket", "Id no informed"));
            return;
        }
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
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
        response.setData(ticket);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
        Response<String> response = new Response<String>();
        Ticket ticket = ticketService.findById(id);
        if (ticket == null){
            response.getErrors().add("Register not found id: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        ticketService.delete(ticket); 
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, 
                @PathVariable("page") Integer page, 
                @PathVariable("count") Integer count){
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);

        if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
            tickets = ticketService.listTicket(page, count);
        } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
        }

        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request, 
                    @PathVariable("page") Integer page,
                    @PathVariable("count") Integer count,
                    @PathVariable("number") Integer number,
                    @PathVariable("title") String title,
                    @PathVariable("status") String status,
                    @PathVariable("priority") String priority,
                    @PathVariable("assigned") Boolean assigned ){

        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;

        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;

        if (number > 0){
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            User userRequest = userFromRequest(request);
            
            if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
                if (assigned) {
                    tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
                } else {
                    tickets = ticketService.findByParameters(page, count, title, status, priority);
                }
                
            } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
                tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
            }
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
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

            if (status.equals("Assigned")){
                ticketCurrent.setAssignedUser(userFromRequest(request));
            }

            Ticket ticketPersisted = ticketService.createOrUpdate(ticketCurrent);
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUserChange(userFromRequest(request));
            changeStatus.setDateChangeStatus(new Date());
            changeStatus.setStatus(StatusEnum.getStatus(status));
            changeStatus.setTicket(ticketPersisted);
            ticketService.createChangeStatus(changeStatus);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateChangeStatus(String id, String status, BindingResult result){
        if (id == null || id.equals("")){
            result.addError(new ObjectError("Ticket", "Id no informed"));
            return;
        } 
        if (status == null || status.equals("")){
            result.addError((new ObjectError("Ticket", "Title no informed")));
            return;
        }
    }

    @GetMapping(value = "/summary")
    // TODO : limit to admin only, implement Archived count 
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
}