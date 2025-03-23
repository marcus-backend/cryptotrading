package com.marcus.service.impl;

import com.marcus.model.auth.Role;
import com.marcus.repository.RoleRepository;
import com.marcus.service.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public record RoleServiceImpl(RoleRepository repository) implements RoleService {
    @Override
    @PostConstruct
    public List<Role> findAll() {
        List<Role> result = repository.getAllByUserId(1L);
        log.info("Found {} roles", result.size());
        return result;
    }
}
