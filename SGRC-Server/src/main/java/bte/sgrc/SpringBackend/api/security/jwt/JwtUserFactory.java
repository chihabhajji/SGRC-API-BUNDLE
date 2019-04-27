package bte.sgrc.SpringBackend.api.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import bte.sgrc.SpringBackend.api.security.jwt.JwtUser;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;

public class JwtUserFactory{

    private JwtUserFactory(){

    }

    public static JwtUser create(User user){
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword(),
            mapToGrantedAuthorities(user.getProfile()));
    }

    public static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum){
        List<GrantedAuthority> autorities = new ArrayList<GrantedAuthority>();
        autorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
        return autorities;
    }
}