package ru.practikum.masters.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.masters.authservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
