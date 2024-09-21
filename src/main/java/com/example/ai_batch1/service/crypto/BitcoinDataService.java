package com.example.ai_batch1.service.crypto;

import com.example.ai_batch1.domain.crypto.BitcoinEntity;
import com.example.ai_batch1.domain.crypto.BitcoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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
}
