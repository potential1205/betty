package org.example.betty.domain.exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @NotNull
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "token_from")
    @NotNull
    private Token tokenFrom;

    @ManyToOne
    @JoinColumn(name = "token_to")
    @NotNull
    private Token tokenTo;

    @Column(name = "amount_in", precision = 18, scale = 8)
    @NotNull
    private BigDecimal amountIn;

    @Column(name = "amount_out", precision = 18, scale = 8)
    @NotNull
    private BigDecimal amountOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", length = 10)
    @NotNull
    private String transactionStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
