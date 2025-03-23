package com.marcus.repository;

import com.marcus.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query(value = "select r.name from Role r inner join UserHasRole ur on r.id = ur.user.id where ur.id= :userId")
    List<String> findAllRolesByUserId(Long userId);
}
