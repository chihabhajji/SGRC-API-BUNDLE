package bte.sgrc.SpringBackend.api.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import bte.sgrc.SpringBackend.api.entity.ChangeStatus;
import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.Util.Reminder;

@Component
public interface TicketService {
    public Ticket createOrUpdate(Ticket ticket);
    public Ticket findById(String id);
    public void delete(Ticket ticket);
    public ChangeStatus createChangeStatus(ChangeStatus changeStatus);
    public Reminder createReminder(Reminder reminder);

    public Iterable<ChangeStatus> listaChangeStatus(String ticketId);
    public Iterable<Reminder> listReminders(String ticketId);
    public Iterable<Ticket> findall();
    public Iterable<Ticket> findByUser(String userId);
    public Iterable<Ticket> findByTechnician(String assignedUserId);

    public Page<Ticket> findByCurrentUser(Integer page, Integer count, String userId);
    public Page<Ticket> findByCurrentUserArchived(Integer page, Integer count, String id);
    public Page<Ticket> findByParameters(Integer page, Integer count, String title, String status, String priority);
    public Page<Ticket> findByParametersAndCurrentUser(Integer page, Integer count, String title, String status, String priority, String userId);
    public Page<Ticket> findByNumber(Integer page, Integer count, Integer number);
    public Page<Ticket> findByParametersAndAssignedUser(Integer page, Integer count, String title, String status, String priority, String assignedUserId);
    public Page<Ticket> findByAssignedUser(Integer page, Integer count,String assignedUserId);
    public Page<Ticket> listTicket(Integer page, Integer count);
	

}