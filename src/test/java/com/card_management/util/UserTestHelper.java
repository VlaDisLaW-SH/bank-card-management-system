package com.card_management.util;

import com.card_management.users_api.enumeration.UserAccessType;
import com.card_management.users_api.model.User;
import com.card_management.users_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTestHelper {

    @Autowired
    private UserRepository userRepository;

    public void createUsers() {
        User user1 = new User();
        user1.setName("Иван");
        user1.setSurname("Иванов");
        user1.setEmail("ivanov@example.com");
        user1.setPassword("securePassword123");
        user1.setAccessType(UserAccessType.valueOf("USER"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Петр");
        user2.setSurname("Петров");
        user2.setEmail("petrov@example.com");
        user2.setPassword("securePassword123");
        user2.setAccessType(UserAccessType.valueOf("USER"));
        userRepository.save(user2);
    }
}
