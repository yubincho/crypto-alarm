package com.example.ai_batch1.controller;

import com.example.ai_batch1.domain.crypto.BitcoinEntity;
import com.example.ai_batch1.domain.crypto.BitcoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;


/**
* 데이터 수집 후 저장
*/
@RequiredArgsConstructor
@RestController
public class DataController {

    private final BitcoinRepository bitcoinRepository;


    @PostMapping("/saveData")
    public String saveCryptoData(@RequestBody Map<String, Object> data) {
        BitcoinEntity bitcoinData = BitcoinEntity.builder()
                .tradePrice(convertToDouble(data.get("trade_price")))
                .tradeVolume(convertToDouble(data.get("trade_volume")))
                .accTradePrice24h(convertToDouble(data.get("acc_trade_price_24h")))
                .accTradeVolume24h(convertToDouble(data.get("acc_trade_volume_24h")))
                .change((String) data.get("change"))
                .changePrice(convertToDouble(data.get("change_price")))
                .changeRate(convertToDouble(data.get("change_rate")))
                .highPrice(convertToDouble(data.get("high_price")))
                .lowPrice(convertToDouble(data.get("low_price")))
                .prevClosingPrice(convertToDouble(data.get("prev_closing_price")))
                .timestamp(LocalDateTime.now())
                .build();

        // 데이터베이스에 저장
        bitcoinRepository.save(bitcoinData);

        return "Data saved successfully";
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        }
        return 0.0; // 값이 null이거나 다른 타입일 경우 기본값 0.0 반환
    }

}
