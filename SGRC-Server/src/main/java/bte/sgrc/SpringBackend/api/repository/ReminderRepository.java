package bte.sgrc.SpringBackend.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import bte.sgrc.SpringBackend.api.entity.Util.Reminder;

public interface ReminderRepository extends MongoRepository<Reminder, String> {
    Iterable<Reminder> findByTicketIdOrderByDateAsc(String ticketId);
}