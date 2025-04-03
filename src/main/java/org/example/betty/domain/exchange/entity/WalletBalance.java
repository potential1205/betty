package org.example.betty.domain.exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.betty.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @NotNull
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "token_id")
    @NotNull
    private Token token;

    @Column(precision = 18, scale = 8)
    private BigDecimal balance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
