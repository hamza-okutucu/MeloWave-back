package melowave.controller;

import lombok.RequiredArgsConstructor;
import melowave.model.User;
import melowave.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "https://hamza-okutucu.github.io")
@RequiredArgsConstructor
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    
    @GetMapping(path = "/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        logger.info("Attempting to get all users");
        List<User> users = userService.getUsers();
        logger.info("Retrieved {} users", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Attempting to get user by ID: {}", id);
        User user = userService.getUserById(id);

        if (user == null) {
            logger.error("User not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Retrieved user with ID: {}", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("Attempting to get user by username: {}", username);
        User user = userService.getUserByUsername(username);

        if (user == null) {
            logger.error("User not found with username: {}", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Retrieved user with username: {}", username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Attempting to create a new user with username: {}", user.getUsername());
        User createdUser = userService.createUser(user);

        if (createdUser == null) {
            logger.error("Username already taken: {}", user.getUsername());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        logger.info("Created a new user with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody User user, Authentication authentication) {
        String currentUsername = authentication.getName();
        logger.info("Attempting to update user with username: {}", currentUsername);

        User existingUser = userService.getUserByUsername(currentUsername);
        if (existingUser == null) {
            logger.error("User not found with username: {}", currentUsername);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User updatedUser = userService.updateUser(existingUser, user);

        logger.info("Updated user with username: {}", authentication.getName());
        
        String newAccessToken = generateAccessToken(updatedUser);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("access_token", newAccessToken);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    private String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
                .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        logger.info("Attempting to get current user");
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        logger.info("Retrieved current user with username: {}", username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
