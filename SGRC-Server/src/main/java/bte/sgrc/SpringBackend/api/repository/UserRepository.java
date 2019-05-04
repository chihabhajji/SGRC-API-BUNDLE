package bte.sgrc.SpringBackend.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import bte.sgrc.SpringBackend.api.entity.User;


public interface UserRepository extends MongoRepository<User, String>{
    public User findByEmail(String email);
    
    @Query("{ 'profile' : ?0 }")
    public List<User> findAllByRole(String profile);
}
