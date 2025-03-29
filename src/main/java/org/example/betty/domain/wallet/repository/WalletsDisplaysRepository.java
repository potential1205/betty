package org.example.betty.domain.wallet.repository;

import org.example.betty.domain.display.entity.WalletsDisplays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletsDisplaysRepository extends JpaRepository<WalletsDisplays, Long> {

    List<WalletsDisplays> findAllByWalletId(Long id);
}
