package bte.sgrc.SpringBackend.api.service.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.repository.UserRepository;
import bte.sgrc.SpringBackend.api.service.UserService;

@Service
public class UserServiceImplement implements UserService{

    @Autowired
    private UserRepository userRepository;

	@Override
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}

	@Override
	public User createOrUpdate(User user) {
		return this.userRepository.save(user);
	}

	@Override
	public User findById(String id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
		return optionalUser.get();
	}

	@Override
	public void delete(String id) {
		this.userRepository.deleteById(id);
	}

	@Override
	public Page<User> findAll(Integer page, Integer count) {
		Pageable pages = PageRequest.of(page, count);
		return this.userRepository.findAll(pages);
	}

	@Override
	public List<User> findTudo() {
		return this.userRepository.findAll();
	}

}