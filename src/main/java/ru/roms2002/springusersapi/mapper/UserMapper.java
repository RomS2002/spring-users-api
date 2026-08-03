package ru.roms2002.springusersapi.mapper;

import org.springframework.stereotype.Component;
import ru.roms2002.springusersapi.dto.CreateUserRequest;
import ru.roms2002.springusersapi.dto.UserResponse;
import ru.roms2002.springusersapi.entity.User;

import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setBirthDate(user.getBirthDate());
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }

    public User toEntity(CreateUserRequest userRequest) {
        User user = new User();
        user.setId(null);
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setBirthDate(userRequest.getBirthDate());
        user.setCreatedAt(null);
        return user;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
