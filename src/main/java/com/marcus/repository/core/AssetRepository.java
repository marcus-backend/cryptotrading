package com.marcus.repository.core;

import com.marcus.model.core.Asset;
import com.marcus.model.core.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByUserIdAndId(Long userId, Long assetId);
    List<Asset> findByUserId(Long userId);
    Asset findByUserIdAndCoinId(Long userId, Long coinId);
}
