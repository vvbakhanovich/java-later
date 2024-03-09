package ru.practicum.javalater.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.javalater.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
