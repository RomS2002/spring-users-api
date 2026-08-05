package ru.roms2002.springusersapi.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.roms2002.springusersapi.dto.CreateUserRequest;
import ru.roms2002.springusersapi.entity.User;
import ru.roms2002.springusersapi.exception.UserNotFoundException;
import ru.roms2002.springusersapi.mapper.UserMapper;
import ru.roms2002.springusersapi.service.UserService;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserMapper.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    public void shouldReturnUserById() throws Exception {

        User user = new User(
                5L,
                "Roman",
                "romsyt2002@gmail.com",
                LocalDate.of(2002, 6, 19),
                LocalDateTime.of(2026, 8, 4, 12, 0)
        );

        when(userService.getById(5L)).thenReturn(user);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.email").value("romsyt2002@gmail.com"))
                .andExpect(jsonPath("$.birthDate").value("2002-06-19"));

        verify(userService).getById(5L);
    }

    @Test
    public void shouldReturnNotFoundErrorOnUserNotFound() throws Exception {

        when(userService.getById(15L)).thenThrow(new UserNotFoundException(15L));

        mockMvc.perform(get("/users/15"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User with id 15 not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).getById(15L);
    }

    @Test
    public void shouldReturnBadRequestErrorOnIncorrectId() throws Exception {

        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {

        User user1 = new User(1L, "Roman", "romsyt2002@gmail.com",
                LocalDate.of(2002, 6, 19), null);
        User user2 = new User(2L, "Igor", "igor_super@example.com",
                LocalDate.of(2008, 9, 21), null);
        User user3 = new User(3L, "Maria", "maria999@outlook.com",
                LocalDate.of(2010, 10, 3), null);
        List<User> users = List.of(user1, user2, user3);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Roman"))
                .andExpect(jsonPath("$[1].name").value("Igor"))
                .andExpect(jsonPath("$[2].email").value("maria999@outlook.com"));;

        verify(userService).getAll();
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {

        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAll();
    }

    @Test
    public void shouldCreateUser() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "Roman",
                "romsyt2002@gmail.com",
                LocalDate.of(2002, 6, 19)
        );
        User created = new User(
                1L,
                "Roman",
                "romsyt2002@gmail.com",
                LocalDate.of(2002, 6, 19),
                LocalDateTime.of(2026, 8, 4, 12, 0)
        );
        when(userService.create(any(User.class))).thenReturn(created);


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.email").value("romsyt2002@gmail.com"))
                .andExpect(jsonPath("$.birthDate").value("2002-06-19"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userService).create(captor.capture());

        User actual = captor.getValue();
        assertNull(actual.getId());
        assertEquals("Roman", actual.getName());
        assertEquals("romsyt2002@gmail.com", actual.getEmail());
        assertEquals(LocalDate.of(2002, 6, 19), actual.getBirthDate());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "Roman",
                "romsyt2002#gmail.com",
                LocalDate.of(2002, 6, 19)
        );


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email is invalid"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldReturnBadRequestWhenMultipleFieldsAreInvalid() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                " ",
                "romsyt2002#gmail.com",
                LocalDate.now().plusDays(1)
        );


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.errors.email").value("Email is invalid"))
                .andExpect(jsonPath("$.errors.birthDate").value("Birth date must be in the past"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldDeleteUserById() throws Exception {

        mockMvc.perform(delete("/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService).deleteById(5L);
    }

    @Test
    public void shouldReturnNotFoundErrorOnDeleteUserThatNotExists() throws Exception {

        doThrow(new UserNotFoundException(15L)).when(userService).deleteById(15L);

        mockMvc.perform(delete("/users/15"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User with id 15 not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).deleteById(15L);
    }
}
