package com.safeview.domain.video.repository;

import com.safeview.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * 비디오 리포지토리
 * 
 * Video 엔티티의 데이터베이스 접근을 담당
 * 사용자별 영상 조회, 파일명으로 영상 조회 기능 제공
 */
@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    /*
     * 사용자 ID로 영상 목록 조회
     */
    List<Video> findAllByUserId(Long userId);

    /*
     * 파일명으로 영상 조회
     */
    Video findByFilename(String filename);

}
