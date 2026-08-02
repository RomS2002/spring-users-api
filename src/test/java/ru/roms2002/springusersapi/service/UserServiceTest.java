package ru.roms2002.springusersapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.roms2002.springusersapi.entity.User;
import ru.roms2002.springusersapi.exception.UserNotFoundException;
import ru.roms2002.springusersapi.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    public void shouldReturnUserWhenUserExists() {
        User userToReturn = new User(5L, "Roman", "romsyt2002@example.com",
                LocalDate.of(2002, 6, 19), null);
        when(userRepository.findById(5L)).thenReturn(Optional.of(userToReturn));

        User result = userService.getById(5L);

        assertSame(userToReturn, result);
        verify(userRepository).findById(5L);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(15L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getById(15L));
        assertEquals("User with id 15 not found", exception.getMessage());
        verify(userRepository).findById(15L);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getById(null));
        assertEquals("User id must not be null", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    public void shouldReturnAllUsers() {
        User user1 = new User(5L, "Roman", "romsyt2002@example.com",
                LocalDate.of(2002, 6, 19), null);
        User user2 = new User(4L, "Igor", "igor_super@example.com",
                LocalDate.of(2000, 9, 12), null);
        User user3 = new User(3L, "Maria", "maria2008@example.com",
                LocalDate.of(2008, 10, 2), null);
        List<User> usersToReturn = List.of(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(usersToReturn);

        List<User> result = userService.getAll();

        assertIterableEquals(usersToReturn, result);
        verify(userRepository).findAll();
    }

    @Test
    public void shouldReturnEmptyUsersList() {
        List<User> usersToReturn = List.of();
        when(userRepository.findAll()).thenReturn(usersToReturn);

        List<User> result = userService.getAll();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    public void shouldCreateUser() {
        User user = new User(5L, "Roman", "romsyt2002@gmail.com", LocalDate.of(2002, 6, 19), null);
        User expectedUser = new User(5L, "Roman", "romsyt2002@gmail.com", LocalDate.of(2002, 6, 19), null);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        User result = userService.create(user);
        verify(userRepository).save(captor.capture());

        User actual = captor.getValue();
        assertNull(actual.getId());
        assertEquals("Roman", actual.getName());
        assertEquals("romsyt2002@gmail.com", actual.getEmail());
        assertEquals(LocalDate.of(2002, 6, 19), actual.getBirthDate());
        assertSame(expectedUser, result);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.create(null));
        assertEquals("User must not be null", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenIdIsNullOnDelete() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteById(null));
        assertEquals("User id must not be null", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    public void shouldDeleteUserById() {
        userService.deleteById(5L);

        verify(userRepository).deleteById(5L);
    }
}
