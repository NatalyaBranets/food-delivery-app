package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestBeansConfiguration.class})
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        // prepare test data
        String email = "admin@gmail.com";

        // act
        Optional<User> optionalUser = this.userRepository.findByEmail(email);

        // assert
        assertTrue(optionalUser.isPresent());
    }

    @Test
    public void findUsers() {
        // prepare test data
        PageRequest pageRequest = PageRequest.of(1, 10);

        // act
        Page<UserDTO> actual = this.userRepository.findUsers(pageRequest);

        // assert
        assertEquals(1, actual.getTotalPages());
    }

}