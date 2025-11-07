package ru.practikum.masters.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.masters.authservice.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
