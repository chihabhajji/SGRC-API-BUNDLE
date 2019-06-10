package bte.sgrc.SpringBackend.api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bte.sgrc.SpringBackend.api.entity.UserNotification;

@Repository
public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    
    Page<UserNotification> findByUserId(Pageable pages, String userId);
   
    Optional<UserNotification> findByUserId(String userId);


}