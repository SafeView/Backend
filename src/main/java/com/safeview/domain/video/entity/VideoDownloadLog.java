package com.safeview.domain.video.entity;

import com.safeview.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 영상 다운로드 로그 엔티티
 * 
 * 영상 다운로드 시 사용자 정보와 다운로드 시간을 기록하는 엔티티
 */
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "video_download_logs")
public class VideoDownloadLog extends BaseTimeEntity {

    /**
     * 다운로드 로그 ID (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    /**
     * 다운로드한 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 다운로드한 사용자 이름
     */
    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    /**
     * 다운로드한 사용자 이메일
     */
    @Column(name = "user_email", length = 50)
    private String userEmail;

    /**
     * 다운로드한 사용자 전화번호
     */
    @Column(name = "user_phone", length = 20)
    private String userPhone;

    /**
     * 다운로드한 사용자 주소
     */
    @Column(name = "user_address", length = 255)
    private String userAddress;

    /**
     * 다운로드한 사용자 성별
     */
    @Column(name = "user_gender", length = 10)
    private String userGender;

    /**
     * 다운로드한 사용자 생년월일
     */
    @Column(name = "user_birthday", length = 20)
    private String userBirthday;

    /**
     * 다운로드한 사용자 역할
     */
    @Column(name = "user_role", length = 20)
    private String userRole;
}
