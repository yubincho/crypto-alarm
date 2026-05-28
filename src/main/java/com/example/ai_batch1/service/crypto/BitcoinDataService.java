package com.example.ai_batch1.service.crypto;

import com.example.ai_batch1.domain.crypto.BitcoinEntity;
import com.example.ai_batch1.domain.crypto.BitcoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinDataService {

    private final BitcoinRepository bitcoinRepository;


    // 데이터베이스에서 가장 최근 거래량을 가져오는 메서드
    public double getCurrentVolume() {
        // 거래량이 가장 최신인 데이터를 가져옴
        BitcoinEntity latestData = bitcoinRepository.findTopByOrderByTimestampDesc();
        return latestData.getTradeVolume();
    }

    public double getCurrentPrice() {
        // 현재 거래 가격
        BitcoinEntity latestData = bitcoinRepository.findTopByOrderByTimestampDesc();
        return latestData.getTradePrice();
    }

    // 8일 이전 데이터 자동 삭제
    public void deleteOldData() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(8);
        bitcoinRepository.deleteByTimestampBefore(sevenDaysAgo);
        log.info("8일 이전 데이터 삭제 완료");
    }
}
