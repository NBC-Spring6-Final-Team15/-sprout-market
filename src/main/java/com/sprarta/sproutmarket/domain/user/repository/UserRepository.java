package com.sprarta.sproutmarket.domain.user.repository;

import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.email = :email AND u.status = 'ACTIVE'")
    Optional<User> findByEmailAndStatusIsActive(@Param("email") String email);
}
