package com.safeview.domain.video.entity;

import com.safeview.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/*
 * 비디오 엔티티
 * 
 * CCTV 영상 정보를 관리하는 엔티티
 * 사용자별 영상 파일 정보와 S3 저장소 URL을 포함
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Getter
public class Video extends BaseEntity {
    /*
     * 비디오 ID (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 영상을 소유한 사용자 ID
     */
    @Column(name = "user_id")
    private Long userId;

    /*
     * 영상 파일명
     */
    @Column(name = "filename")
    private String filename;

    /*
     * S3 저장소의 영상 URL
     */
    @Column(name = "s3_url")
    private String s3Url;
}
