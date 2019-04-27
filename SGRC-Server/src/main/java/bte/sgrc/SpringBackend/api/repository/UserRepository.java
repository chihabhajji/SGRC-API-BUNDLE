package bte.sgrc.SpringBackend.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import bte.sgrc.SpringBackend.api.entity.User;


public interface UserRepository extends MongoRepository<User, String>{
    public User findByEmail(String email);
    
}
