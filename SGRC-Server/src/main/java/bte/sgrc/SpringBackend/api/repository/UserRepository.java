package bte.sgrc.SpringBackend.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import bte.sgrc.SpringBackend.api.entity.User;


public interface UserRepository extends MongoRepository<User, String>{
    public User findByEmail(String email);
    
    @Query("{ 'profile' : ?0 }")
    Page<User> findAllByRole(String profile, Pageable pages);
}
