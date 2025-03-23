package com.marcus.service;

import com.marcus.model.auth.User;
import com.marcus.model.core.Asset;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;

import java.util.List;

public interface AssetService {
    Asset createAsset(User user, Coin coin, double quantity);
    Asset getAssetById(Long assetId);
    Asset getAssetByUserIdAndId(Long userId, Long assetId);
    List<Asset> getUserAssets(Long userId);
    Asset updateAsset(Long assetId, double quantity);
    Asset findAssetByUserIdAndCoinId(Long userId, Long coinId);
    void deleteAsset(Long assetId);
}
