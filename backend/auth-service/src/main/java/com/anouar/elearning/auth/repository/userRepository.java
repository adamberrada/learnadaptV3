package com.anouar.elearning.auth.repository;

import com.anouar.elearning.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
}
