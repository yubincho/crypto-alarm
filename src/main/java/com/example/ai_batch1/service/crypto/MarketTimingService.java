package com.example.ai_batch1.service.crypto;

import com.example.ai_batch1.bot.TelegramBot;
import com.example.ai_batch1.domain.crypto.BitcoinEntity;
import com.example.ai_batch1.domain.crypto.BitcoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
* 시장 진입 타이밍 분석
 * Buy 신호: 가격이 평균보다 낮고 거래량이 급증
 * Sell 신호: 가격이 평균보다 높고 거래량이 급증
*/
@RequiredArgsConstructor
@Service
public class MarketTimingService {

    private final BitcoinDataService bitcoinDataService;
    private final BitcoinRepository bitcoinRepository;
    private final TelegramBot telegramBot;


    public void marketTimingAnalyze() {
        // 실시간으로 currentVolume 값을 가져옴
        double currentVolume = bitcoinDataService.getCurrentVolume();
        double currentPrice = bitcoinDataService.getCurrentPrice();

        // currentVolume을 기반으로 급증 감지 로직 수행
        suggestMarketEntry(currentVolume, currentPrice);
    }

    public void suggestMarketEntry(double currentVolume, double currentPrice) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // 지난 7일간의 평균 거래량과 가격 계산
        List<BitcoinEntity> lastWeekData = bitcoinRepository.findByTimestampAfter(oneWeekAgo);
        double averageVolume = lastWeekData.stream()
                .mapToDouble(BitcoinEntity::getTradeVolume).average().orElse(0.0);
        double averagePrice = lastWeekData.stream()
                .mapToDouble(BitcoinEntity::getTradePrice).average().orElse(0.0);

        boolean volumeSpike = currentVolume > averageVolume * 1.5;

        String message;
        if (volumeSpike) {
            if (currentPrice < averagePrice) {
                message = "Buy signal: Volume spike detected and price is below average.";  // 매수 신호
            } else if (currentPrice > averagePrice) {
                message = "Sell signal: Volume spike detected and price is above average.";  // 매도 신호
            } else {
                message = "Volume spike detected, but no significant price movement.";
            }
        } else {
            message = "No significant market signal detected.";
        }

        // 텔레그램으로 알림 전송
        telegramBot.sendNotificationToEligibleUsers(message);
//        return message;
    }


}
