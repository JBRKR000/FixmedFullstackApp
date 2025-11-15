package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> getUserById(Long id);

    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Users> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
