package melowave.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import melowave.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String username);
}