package bte.sgrc.SpringBackend.api.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.entity.Util.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Notification findByUser(User user);

    List<Notification> findByUserOrderByCreatedAtDesc(String userId, Pageable pages);

    Notification findByUserAndNotificationId(User user, String notificationId);

}