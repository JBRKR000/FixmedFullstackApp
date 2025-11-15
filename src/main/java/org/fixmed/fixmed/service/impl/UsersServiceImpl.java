package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.repository.UsersRepository;
import org.fixmed.fixmed.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public Optional<Users> getUserById(Long id) {
        return usersRepository.getUserById(id);
    }

    @Override
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }

    @Override
    public Page<Users> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    @Override
    public Optional<Users> updateUser(Long id, Users userDetails) {
        return Optional.ofNullable(usersRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDetails.getUsername());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setFirst_name(userDetails.getFirst_name());
                    existingUser.setLast_name(userDetails.getLast_name());
                    existingUser.setRole(userDetails.getRole());
                    existingUser.setEnabled(userDetails.getEnabled());
                    return usersRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }
}
