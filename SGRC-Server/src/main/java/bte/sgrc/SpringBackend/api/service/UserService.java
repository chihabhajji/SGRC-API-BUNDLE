package bte.sgrc.SpringBackend.api.service;

import java.util.List;

import org.springframework.data.domain.Page;

import bte.sgrc.SpringBackend.api.entity.User;

public interface UserService{
    public User findByEmail(String email);
    public User createOrUpdate(User user);
    public User findById(String id);
    public void delete(String id);
    public Page<User> findAll(Integer page, Integer count);
    public List<User> findTudo();
    public Page<User> findByRole(String role, Integer page, Integer count);
}