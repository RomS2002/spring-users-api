package ru.roms2002.springusersapi.controller;

import org.springframework.web.bind.annotation.*;
import ru.roms2002.springusersapi.dto.CreateUserRequest;
import ru.roms2002.springusersapi.dto.UserResponse;
import ru.roms2002.springusersapi.entity.User;
import ru.roms2002.springusersapi.mapper.UserMapper;
import ru.roms2002.springusersapi.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return userMapper.toResponse(user);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(userService.getAll());
    }

    @PostMapping
    public UserResponse create(@RequestBody CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User created = userService.create(user);
        return userMapper.toResponse(created);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
