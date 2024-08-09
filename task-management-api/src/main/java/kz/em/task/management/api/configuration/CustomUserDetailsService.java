package kz.em.task.management.api.configuration;

import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.api.exception.ResourceNotFoundException;
import kz.em.task.management.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        return user.map(CustomUserDetails::new)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
    }
}
