package com.sprarta.sproutmarket.domain.user.repository;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.email = :email AND u.status = 'ACTIVE'")
    Optional<User> findByEmailAndStatusIsActive(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.status = 'ACTIVE'")
    Optional<User> findByIdAndStatusIsActive(@Param("userId") Long userId);

    default User findByIdAndStatusIsActiveOrElseThrow(Long userId) {
        return findByIdAndStatusIsActive(userId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_USER)
        );
    }

    default User findUserById(Long id){
        return findById(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }
}
