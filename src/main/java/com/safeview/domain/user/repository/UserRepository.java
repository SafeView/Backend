package com.safeview.domain.user.repository;

import com.safeview.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // 전화번호 중복 체크
    boolean existsByPhone(String phone);
}
