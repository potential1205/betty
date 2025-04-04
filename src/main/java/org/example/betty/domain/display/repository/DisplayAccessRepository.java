package org.example.betty.domain.display.repository;

import org.example.betty.domain.display.entity.DisplayAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayAccessRepository extends JpaRepository<DisplayAccess, Long> {
    boolean existsByWalletAddressAndGameIdAndTeamId(String walletAddress, Long gameId, Long teamId);
}
