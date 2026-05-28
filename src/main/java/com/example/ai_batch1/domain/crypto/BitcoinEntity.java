package com.example.ai_batch1.domain.crypto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class BitcoinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double tradePrice;   // 현재 거래 가격 (현재가)
    private Double tradeVolume;  // 최근 거래량 (방금 체결된 거래의 수량)
    private Double accTradePrice24h;   // 24시간 누적 거래 금액 (원화 기준)
    private Double accTradeVolume24h;  // 24시간 누적 거래량 (코인 수량 기준)

    @Column(name = "price_change")
    private String change;   // 가격 변동 상태 → RISE(상승) / FALL(하락) / EVEN(보합)

    @Column(name = "price_change_price")
    private Double changePrice;  // 전일 종가 대비 변동 금액

    @Column(name = "price_change_rate")
    private Double changeRate;   // 전일 종가 대비 변동률 (%)
    private Double highPrice;    // 당일 최고가
    private Double lowPrice;     // 당일 최저가
    private Double prevClosingPrice;  // 전일 종가
    private LocalDateTime timestamp;  // 데이터 수신 시각

    @Column(name = "ask_bid")
    private String askBid;     // 매수/매도 구분

}
