package com.safeview.global.config;

import com.safeview.domain.user.entity.Gender;
import com.safeview.domain.user.entity.User;
import com.safeview.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 관리자가 이미 존재하는지 확인
        if (userRepository.findByEmail("admin@safeview.com").isEmpty()) {
            // 관리자 사용자 생성
            User admin = User.createAdmin(
                    "admin@safeview.com",
                    passwordEncoder.encode("admin123"),
                    "관리자",
                    "서울시 강남구 테헤란로 123",
                    "010-1234-5678",
                    Gender.MALE,
                    "1990-01-01"
            );

            userRepository.save(admin);
            
            log.info("관리자 사용자가 생성되었습니다: admin@safeview.com / admin123");
        } else {
            log.info("관리자 사용자가 이미 존재합니다.");
        }

        // 테스트용 일반 사용자 생성
        if (userRepository.findByEmail("user@safeview.com").isEmpty()) {
            User user = User.createUser(
                    "user@safeview.com",
                    passwordEncoder.encode("user123"),
                    "일반사용자",
                    "서울시 서초구 서초대로 456",
                    "010-9876-5432",
                    Gender.FEMALE,
                    "1995-05-15"
            );

            userRepository.save(user);
            
            log.info("테스트 사용자가 생성되었습니다: user@safeview.com / user123");
        } else {
            log.info("테스트 사용자가 이미 존재합니다.");
        }

        // 중간 관리자 사용자 생성
        if (userRepository.findByEmail("moderator@safeview.com").isEmpty()) {
            User moderator = User.createModerator(
                    "moderator@safeview.com",
                    passwordEncoder.encode("moderator123"),
                    "중간관리자",
                    "서울시 중구 을지로 789",
                    "010-5555-1234",
                    Gender.MALE,
                    "1988-03-20"
            );

            userRepository.save(moderator);
            
            log.info("중간 관리자 사용자가 생성되었습니다: moderator@safeview.com / moderator123");
        } else {
            log.info("중간 관리자 사용자가 이미 존재합니다.");
        }
    }
} 