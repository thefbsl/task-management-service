package kz.em.task.management.api.service;

import kz.em.task.management.api.entity.UserEntity;
import kz.em.task.management.api.exception.EmailAlreadyExistsException;
import kz.em.task.management.api.repository.UserRepository;
import kz.em.task.management.client.dto.IdDto;
import kz.em.task.management.client.dto.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public IdDto registerUser(RegistrationRequest request) {
        Optional<UserEntity> existingUserWithEmail  = userRepository.findByEmail(request.getEmail());
        if (existingUserWithEmail.isPresent())
            throw new EmailAlreadyExistsException("User with this email already exists");
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new IdDto(user.getId());
    }

    public Boolean isUserVerified(String email){
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

}
