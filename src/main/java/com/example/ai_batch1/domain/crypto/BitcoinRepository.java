package com.example.ai_batch1.domain.crypto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BitcoinRepository extends JpaRepository<BitcoinEntity, Long> {

    // 특정 시간 이후의 데이터를 가져오기
    List<BitcoinEntity> findByTimestampAfter(LocalDateTime onWeekAgo);

    BitcoinEntity findTopByOrderByTimestampDesc();

}
