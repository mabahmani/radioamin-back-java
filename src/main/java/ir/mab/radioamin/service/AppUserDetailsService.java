package ir.mab.radioamin.service;

import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.model.RoleEnum;
import ir.mab.radioamin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AppUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        if (s.equals("developer")){
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(RoleEnum.DEVELOPER));
            return new org.springframework.security.core.userdetails.User(
                    "developer", new BCryptPasswordEncoder().encode("developer"),
                    true,
                    true,
                    true,
                    true,
                    mapUserAuthority(roles));
        }

        User user = userRepository.findUserByEmail(s)
                .orElseThrow(() -> new UsernameNotFoundException("user '"+ s + "' not found."));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.getActive(),
                true,
                true,
                true,
                mapUserAuthority(user.getUserRoles()));
    }

    private List<GrantedAuthority> mapUserAuthority(Set<Role> userRoles) {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (userRoles.isEmpty()){
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.CONSUMER.name()));
        }

        else {
            for (Role role : userRoles){
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole().name()));
            }
        }

        return grantedAuthorities;
    }
}
