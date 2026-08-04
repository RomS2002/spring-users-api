package ru.roms2002.springusersapi.service;

import org.springframework.stereotype.Service;
import ru.roms2002.springusersapi.entity.User;
import ru.roms2002.springusersapi.exception.UserNotFoundException;
import ru.roms2002.springusersapi.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User create(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        user.setId(null);
        return repository.save(user);
    }

    public void deleteById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        repository.deleteById(id);
    }
}
