package bte.sgrc.SpringBackend.api.service.implement;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.ChangeStatus;
import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Reminder;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.enums.StatusEnum;
import bte.sgrc.SpringBackend.api.repository.ChangeStatusRepository;
import bte.sgrc.SpringBackend.api.repository.ReminderRepository;
import bte.sgrc.SpringBackend.api.repository.TicketRepository;
import bte.sgrc.SpringBackend.api.service.TicketService;
import bte.sgrc.SpringBackend.api.service.UserNotificationService;

@Service
public class TicketServiceImplement implements TicketService{

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
	private ChangeStatusRepository changeStatusReposiroty;
	
	@Autowired
	private UserNotificationService userNotificationService;
	
	@Autowired 
	private ReminderRepository reminderRepository;

	@Autowired
	private UserServiceImplement userService;

	private static Logger logger = LoggerFactory.getLogger(TicketServiceImplement.class);

	@Override
	public Ticket createOrUpdate(Ticket ticket) {
		return this.ticketRepository.save(ticket);
	}

	@Override
	public Ticket findById(String id) {
        Optional<Ticket> optionalTicket = this.ticketRepository.findById(id);
		return optionalTicket.get();
	}

	@Override
	public void delete(Ticket ticket) {
		this.ticketRepository.delete(ticket);
	}

	@Override
	public Page<Ticket> listTicket(Integer page, Integer count) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findAll(pages);
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
		return this.changeStatusReposiroty.save(changeStatus);
	}

	@Override
	public Reminder createReminder(Reminder reminder) {
		return this.reminderRepository.save(reminder);
	}

	@Override
	public Iterable<ChangeStatus> listaChangeStatus(String ticketId) {
		return this.changeStatusReposiroty.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
	}

	@Override
	public Iterable<Reminder> listReminders(String ticketId){
		return this.reminderRepository.findByTicketIdOrderByDateAsc(ticketId);
	}

	@Override
	public Page<Ticket> findByCurrentUser(Integer page, Integer count, String userId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByUserIdAndArchivedFalseAndDeletedFalseOrderByDateDesc(pages, userId);
	}

	@Override
	public Page<Ticket> findByCurrentUserArchived(Integer page, Integer count, String userId) {
		Pageable pages = PageRequest.of(page, count);
		return this.ticketRepository.findByUserIdAndArchivedTrueAndDeletedFalseOrderByDateDesc(pages, userId);
	}

	@Override
	public Page<Ticket> findByParameters(Integer page, Integer count, String title, String status, String priority) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusContainingAndPriorityContainingOrderByDateDesc(title, status, priority, pages);
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(Integer page, Integer count, String title, String status,
			String priority, String userId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndUserIdAndArchivedFalseAndDeletedFalseOrderByDateDesc(title, status, priority, userId, pages);
	}

	@Override
	public Page<Ticket> findByNumber(Integer page, Integer count, Integer number) {
        	Pageable pages = PageRequest.of(page, count);
        	return this.ticketRepository.findByNumber(number, pages);
	}

	@Override
	public Iterable<Ticket> findall() {
	return this.ticketRepository.findAll();
	}

	@Override 
	public Iterable<Ticket> findByUser(String userId){
		return this.ticketRepository.findByUser(userId);
	}
	@Override
	public Iterable<Ticket> findByTechnician(String assignedUserId){
        return this.ticketRepository.findByAssignedUserId(assignedUserId);
	}

	@Override
	public Page<Ticket> findByParametersAndAssignedUser(Integer page, Integer count, String title, String status,
			String priority, String assignedUserId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndAssignedUserIdOrderByDateDesc(title, status, priority, assignedUserId, pages);
	}

	@Override
	public Page<Ticket> findByAssignedUser(Integer page, Integer count,String assignedUserId){
		Pageable pages = PageRequest.of(page, count);
		return this.ticketRepository.findByAssignedUserId(assignedUserId,pages);
	}


	@Scheduled(cron = "0 30 23 * * *")
	private void purgeFromDB() {
		logger.info("Initiating ticket clean up");
		List<Ticket> tickets = ticketRepository.findAll();
		if (tickets != null) {
            for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
				Ticket ticket = iterator.next();
				if (ticket.getStatus().equals(StatusEnum.Resolved)){
					Iterable<ChangeStatus> changesCurrent = listaChangeStatus(ticket.getId());
					for (ChangeStatus change : changesCurrent){
						if (change.getStatus().equals(StatusEnum.Resolved))
							if (LocalDateTime.now().isAfter(change.getDateChangeStatus().plusMonths(2))){
								ChangeStatus updateStatus = new ChangeStatus();
								updateStatus.setDateChangeStatus(LocalDateTime.now());
								updateStatus.setMessage("Automatically approved due to no response from client!");
								updateStatus.setStatus(StatusEnum.Approved);
								updateStatus.setUserChange(ticket.getUser());
								updateStatus.setTicket(ticket);
								createChangeStatus(updateStatus);

								ticket.setStatus(StatusEnum.Approved);
								createOrUpdate(ticket);
								logger.info("Approved ticket number :"+ ticket.getNumber());
								List<User> admins = userService.findByRole(ProfileEnum.ROLE_ADMIN.name());
								for (User admin : admins){
									userNotificationService.notifyUser(admin.getId(), ticket.getId(), "Ticket number :"+ticket.getNumber()+" has been approved");
								}
							}
					}
				}
			}
		}
	}
}