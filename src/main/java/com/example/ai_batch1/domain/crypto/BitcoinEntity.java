package com.example.ai_batch1.domain.crypto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Double tradePrice;
    private Double tradeVolume;
    private Double accTradePrice24h;
    private Double accTradeVolume24h;
    private String change;
    private Double changePrice;
    private Double changeRate;
    private Double highPrice;
    private Double lowPrice;
    private Double prevClosingPrice;
    private LocalDateTime timestamp;
}
