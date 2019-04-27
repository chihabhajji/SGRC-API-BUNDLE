package bte.sgrc.SpringBackend.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import bte.sgrc.SpringBackend.api.entity.ChangeStatus;

public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String>{
    Iterable<ChangeStatus> findByTicketIdOrderByDateChangeStatusDesc(String ticketId);
}