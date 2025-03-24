package com.marcus.service.impl;

import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Asset;
import com.marcus.model.core.Coin;
import com.marcus.repository.core.AssetRepository;
import com.marcus.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;

    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getAskPrice().doubleValue()); // Assuming buy price is the ask price at creation
        return assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("Asset not found!"));
    }

    @Override
    public Asset updateAsset(Long assetId, double quantity) {
        Asset asset = getAssetById(assetId);
        double newQuantity = asset.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new BusinessException("Cannot reduce asset quantity below 0");
        }
        asset.setQuantity(newQuantity);
        return assetRepository.save(asset);
    }

    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, Long coinId) {
        return assetRepository.findByUserIdAndCoinId(userId, coinId);
    }

    @Override
    public void deleteAsset(Long assetId) {
        assetRepository.deleteById(assetId);
    }
}
