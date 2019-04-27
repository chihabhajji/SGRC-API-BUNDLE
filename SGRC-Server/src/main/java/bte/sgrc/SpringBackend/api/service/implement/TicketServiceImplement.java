package bte.sgrc.SpringBackend.api.service.implement;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.ChangeStatus;
import bte.sgrc.SpringBackend.api.entity.Ticket;
import bte.sgrc.SpringBackend.api.repository.ChangeStatusRepository;
import bte.sgrc.SpringBackend.api.repository.TicketRepository;
import bte.sgrc.SpringBackend.api.service.TicketService;

@Service
public class TicketServiceImplement implements TicketService{

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusReposiroty;

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
	public Iterable<ChangeStatus> listaChangeStatus(String ticketId) {
		return this.changeStatusReposiroty.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
	}

	@Override
	public Page<Ticket> findByCurrentUser(Integer page, Integer count, String userId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByUserIdOrderByDateDesc(pages, userId);
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
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndUserIdOrderByDateDesc(title, status, priority, userId, pages);
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
	public Page<Ticket> findByParametersAndAssignedUser(Integer page, Integer count, String title, String status,
			String priority, String assignedUserId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndAssignedUserIdOrderByDateDesc(title, status, priority, assignedUserId, pages);
    }
}