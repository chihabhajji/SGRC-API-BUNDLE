package bte.sgrc.SpringBackend.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.security.jwt.JwtUserFactory;
import bte.sgrc.SpringBackend.api.service.UserService;

@Service
public class JwtUserDetailsServiceImplement implements UserDetailsService{

    @Autowired
    private UserService userService;
    
    @Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException(String.format("No user found with username '%'.", email));
        } else {
            return JwtUserFactory.create(user);
        }
	}

}