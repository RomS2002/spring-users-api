package ru.roms2002.springusersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.roms2002.springusersapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
