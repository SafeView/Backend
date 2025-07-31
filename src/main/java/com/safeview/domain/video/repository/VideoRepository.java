package com.safeview.domain.video.repository;

import com.safeview.domain.video.dto.VideoResponseDto;
import com.safeview.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByUserId(Long userId);

    Video findByFilename(String filename);


}
