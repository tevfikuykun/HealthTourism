package com.healthtourism.userservice.service;

import com.healthtourism.userservice.dto.UserDTO;
import com.healthtourism.userservice.entity.User;
import com.healthtourism.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("1234567890");
        user.setCountry("Turkey");
        user.setRole("USER");
        user.setIsActive(true);
    }

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get user by id")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            userService.getUserById(999L));
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertTrue(result.getIsActive());
        verify(userRepository, times(1)).save(any(User.class));
    }
}

