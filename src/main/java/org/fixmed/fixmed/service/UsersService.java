package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UsersService {
    Optional<Users> getUserById(Long id);
    Users saveUser(Users user);
    Page<Users> getAllUsers(Pageable pageable);
    Optional<Users> updateUser(Long id, Users userDetails);

}
