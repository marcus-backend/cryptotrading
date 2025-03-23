package com.marcus.service.impl;

import com.marcus.config.Translator;
import com.marcus.dto.request.AddressDTO;
import com.marcus.dto.request.UserRequestDTO;
import com.marcus.dto.response.UserDetailResponse;
import com.marcus.exception.ResourceNotFoundException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Address;
import com.marcus.model.core.Wallet;
import com.marcus.repository.UserRepository;
import com.marcus.repository.core.WalletRepository;
import com.marcus.service.UserService;
import com.marcus.util.UserStatus;
import com.marcus.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public long save(UserRequestDTO request) throws UnsupportedEncodingException {
        String username = request.getUsername();
        Optional<User> lvUser = userRepository.findByUsername(username);
        if (lvUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        User savedUser = userRepository.save(user);
        log.info("User saved: {}", user.getFirstName());

        // Initialize wallet with 50,000 USDT
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.valueOf(50000))
                .build();
        walletRepository.save(wallet);
        log.info("init wallet for new user with default : {} USDT", 50000);

        return user.getId();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void update(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasLength(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User updated: {}", user.getFirstName());
    }

    public Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("status changed");
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<String> findRolesByUser(long userId) {
        return userRepository.findAllRolesByUserId(userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("email.notfound")));
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("user.notfound")));
    }

    @Override
    public List<UserDetailResponse> getUsers(int pageNo, int pageSize) {
        if (pageNo > 0) pageNo = pageNo - 1;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.stream().map(user ->
                UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build()).toList();
    }
}
