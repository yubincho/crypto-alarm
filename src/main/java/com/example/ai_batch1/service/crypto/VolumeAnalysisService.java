package com.example.ai_batch1.service.crypto;

import com.example.ai_batch1.bot.TelegramBot;
import com.example.ai_batch1.domain.crypto.BitcoinEntity;
import com.example.ai_batch1.domain.crypto.BitcoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 거래량 급증 감지
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VolumeAnalysisService {

    private final BitcoinDataService bitcoinDataService;
    private final BitcoinRepository bitcoinRepository;
    private final TelegramBot telegramBot;


    public void volumeAnalyze() {
        // 실시간으로 currentVolume 값을 가져옴
        double currentVolume = bitcoinDataService.getCurrentVolume();

        // currentVolume을 기반으로 급증 감지 로직 수행
        boolean isSpike = isVolumeSpike(currentVolume);

        if (isSpike) {
            sendVolumeSpikeAlert(currentVolume);
        }
    }

    // 평균 거래량을 기반으로 급증 감지
    public boolean isVolumeSpike(double currentVolume) {
        LocalDateTime onWeekAgo = LocalDateTime.now().minusDays(7); //

        // 지난 7일간의 평균 거래량 계산
        List<BitcoinEntity> lastWeekData = bitcoinRepository.findByTimestampAfter(onWeekAgo);
        double averageVolume = lastWeekData.stream()
                .mapToDouble(BitcoinEntity::getTradeVolume)
                .average()
                .orElse(0.0);

        // 현재 거래량이 평균 거래량의 1.5배 이상인지 확인
        return currentVolume > averageVolume * 1.5;   //

//        return true;  // 테스트용으로 항상 알림 발생
    }


    // 알림 보내기
    public void sendVolumeSpikeAlert(double currentVolume) {
        if (isVolumeSpike(currentVolume)) {
            // 거래량 급증 감지 -> 사용자에게 알림 전송
            // 거래량 급증 감지 -> 사용자에게 알림 전송
            String message = String.format("거래량 급증 감지! 현재 거래량: %.2f", currentVolume);
            log.info(message);

            // 텔레그램으로 알림 전송
            telegramBot.sendNotificationToEligibleUsers(message);

        }
    }


    // 고래 감지 로직 추가
    public void whaleDetection() {
        BitcoinEntity latestData = bitcoinRepository.findTopByOrderByTimestampDesc();
        if (latestData == null) return;

        double tradePrice = latestData.getTradePrice();
        double tradeVolume = latestData.getTradeVolume();
        String askBid = latestData.getAskBid();

        // 단일 체결 금액 계산
        double tradeAmount = tradePrice * tradeVolume;

        String direction = "BID".equals(askBid) ? "🟢 매수" : "🔴 매도";

        // 10억 이상 → 초대형 고래
        if (tradeAmount >= 1_000_000_000) {
            String message = "🚨 초대형 고래 감지!\n"
                    + direction + " 체결\n"
                    + "체결 금액: " + String.format("%,.0f", tradeAmount) + "원\n"
                    + "체결 가격: " + String.format("%,.0f", tradePrice) + "원";
            telegramBot.sendNotificationToEligibleUsers(message);

            // 1억 이상 → 고래
        } else if (tradeAmount >= 100_000_000) {
            String message = "🐋 고래 감지!\n"
                    + direction + " 체결\n"
                    + "체결 금액: " + String.format("%,.0f", tradeAmount) + "원\n"
                    + "체결 가격: " + String.format("%,.0f", tradePrice) + "원";
            telegramBot.sendNotificationToEligibleUsers(message);
        }
    }


}
