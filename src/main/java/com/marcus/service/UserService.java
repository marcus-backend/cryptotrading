package com.marcus.service;

import com.marcus.dto.request.UserRequestDTO;
import com.marcus.dto.response.UserDetailResponse;
import com.marcus.model.auth.User;
import com.marcus.util.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;
import java.util.List;


public interface UserService {
    UserDetailsService userDetailsService();
    long save(UserRequestDTO request) throws UnsupportedEncodingException;

    void save(User user);

    void update(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void delete(long userId);

    User getByUsername(String userName);

    List<String> findRolesByUser(long userId);

    UserDetailResponse getUser(long userId);

    User getUserByEmail(String email);

    User getUserById(long id);

    List<UserDetailResponse> getUsers(int pageNo, int pageSize);

    boolean existsByUsername(String username);
}
