package com.marcus.config;

import com.marcus.dto.request.AddressDTO;
import com.marcus.dto.request.UserRequestDTO;
import com.marcus.service.UserService;
import com.marcus.util.Gender;
import com.marcus.util.UserStatus;
import com.marcus.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "Init data for test")
public class DataInitializer {

    private final UserService userService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (!userService.existsByUsername("test-user-1")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date dateOfBirth = dateFormat.parse("07/14/1990");

                AddressDTO addressDTO = AddressDTO.builder()
                        .apartmentNumber("4")
                        .floor("8")
                        .building("G")
                        .streetNumber("39")
                        .street("Can Tho")
                        .city("Can Tho")
                        .country("Vietnam")
                        .addressType(0)
                        .build();

                UserRequestDTO userRequestDTO = new UserRequestDTO();
                userRequestDTO.setFirstName("test-user-1");
                userRequestDTO.setLastName("test-user-1");
                userRequestDTO.setEmail("hoangann.dev@gmail.com");
                userRequestDTO.setPhone("0376599506");
                userRequestDTO.setDateOfBirth(dateOfBirth);
                userRequestDTO.setGender(Gender.MALE);
                userRequestDTO.setUsername("test-user-1");
                userRequestDTO.setPassword("123123");
                userRequestDTO.setType(UserType.ADMIN.name());
                userRequestDTO.setAddresses(Collections.singleton(addressDTO));
                userRequestDTO.setStatus(UserStatus.ACTIVE);

                try {
                    long userId = userService.save(userRequestDTO);
                  log.info("Initialized test user with ID: {} " , userId);
                } catch (Exception e) {
                    log.info("Failed to initialize user: {}" , e.getMessage());
                }
            } else {
              log.info("User test-user-1 already exists, skipping initialization.");
            }
        };
    }
}