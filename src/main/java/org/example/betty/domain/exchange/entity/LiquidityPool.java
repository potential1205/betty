package org.example.betty.domain.exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidity_pools")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "token_id")
    @NotNull
    private Token token;

    @Column(name = "betty_amount", precision = 18, scale = 8)
    private BigDecimal bettyAmount;

    @Column(name = "fan_token_amount", precision = 18, scale = 8)
    private BigDecimal fanTokenAmount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
