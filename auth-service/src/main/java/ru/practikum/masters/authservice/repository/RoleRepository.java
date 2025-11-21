package ru.practikum.masters.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.masters.authservice.model.Role;
import ru.practikum.masters.authservice.model.RoleType;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleName(RoleType role);
}
