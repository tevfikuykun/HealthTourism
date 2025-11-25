package com.healthtourism.userservice.integration;

import com.healthtourism.userservice.dto.UserDTO;
import com.healthtourism.userservice.entity.User;
import com.healthtourism.userservice.repository.UserRepository;
import com.healthtourism.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User Service Integration Tests")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create and retrieve user")
    @Transactional
    void testCreateAndRetrieveUser() {
        User user = new User();
        user.setEmail("integration@test.com");
        user.setFirstName("Integration");
        user.setLastName("Test");
        user.setPhone("1234567890");
        user.setCountry("Turkey");
        user.setRole("USER");

        UserDTO created = userService.createUser(user);
        
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("integration@test.com", created.getEmail());

        UserDTO retrieved = userService.getUserById(created.getId());
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getEmail(), retrieved.getEmail());
    }

    @Test
    @DisplayName("Should get all users")
    @Transactional
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setFirstName("User1");
        user1.setRole("USER");
        userService.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setFirstName("User2");
        user2.setRole("USER");
        userService.createUser(user2);

        List<UserDTO> users = userService.getAllUsers();
        
        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }
}

