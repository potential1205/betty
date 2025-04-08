package org.example.betty.domain.exchange.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "token_prices")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPrice {

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
}
