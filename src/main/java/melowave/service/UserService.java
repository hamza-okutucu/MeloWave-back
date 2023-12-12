package melowave.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import melowave.model.Role;
import melowave.model.User;
import melowave.repository.RoleRepo;
import melowave.repository.UserRepo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findUserByUsername(username);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            logger.info("User found in the database: {}", username);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
            
            return new org.springframework
                          .security
                          .core
                          .userdetails
                          .User(user.getUsername(), user.getPassword(), authorities);
        }
        
        logger.error("User not found in the database");
        throw new UsernameNotFoundException("User not found in the database");
    }

    public List<User> getUsers() {   
        logger.info("Fetching all users");
        return userRepo.findAll();
    }
    
    public User getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<User> user = userRepo.findUserById(id);
        return user.orElse(null);
    }
    
    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        Optional<User> user = userRepo.findUserByUsername(username);
        return user.orElse(null);
    }

    public User createUser(User user) {
        logger.info("Creating a new user: {}", user.getUsername());

        if (userRepo.existsByUsername(user.getUsername())) {
            logger.error("Username already taken: {}", user.getUsername());
            return null;
        }

        Role role = roleRepo.findByName("ROLE_USER");
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);

        User savedUser = userRepo.save(user);

        logger.info("New user created with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(User existingUser, User user) {
        logger.info("Updating user with ID: {}", existingUser.getId());

        if (user.getUsername() != null)
            existingUser.setUsername(user.getUsername());

        if (user.getPassword() != null)
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepo.save(existingUser);

        logger.info("User updated with ID: {}", savedUser.getId());
        return savedUser;
    }
}