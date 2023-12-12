package melowave.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import melowave.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
