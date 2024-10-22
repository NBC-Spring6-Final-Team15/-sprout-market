package com.sprarta.sproutmarket.domain.user.repository;

import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
