package org.example.betty.domain.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_token_prices")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyTokenPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    @Column(name = "token_name", nullable = true, length = 20)
    private String tokenName;

    @Column(name = "price", precision = 36, scale = 18, nullable = false)
    private BigDecimal price;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public HourlyTokenPrice(Token token, String tokenName, BigDecimal price, LocalDateTime updatedAt) {
        this.token = token;
        this.tokenName = tokenName;
        this.price = price;
        this.updatedAt = updatedAt;
    }

}
