package com.marcus.controller;

import com.marcus.config.Translator;
import com.marcus.dto.response.ResponseData;
import com.marcus.model.auth.User;
import com.marcus.model.core.Asset;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;
import com.marcus.model.core.WalletTransaction;
import com.marcus.service.AssetService;
import com.marcus.service.OrderService;
import com.marcus.service.UserService;
import com.marcus.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/asset")
@Validated
@Slf4j
@Tag(name = "Asset Controller")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;
    private final UserService userService;


    @Operation(summary = "", description = "")
    @GetMapping("/{assetId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserWallet(@PathVariable long assetId) {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok().body(asset);
    }

    @Operation(summary = "", description = "")
    @GetMapping("/coin/{coinId}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAssetByUserIdAndCoinId(
            @PathVariable long coinId,
            @PathVariable long userId
    ) {
        User user = userService.getUserById(userId);
        Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
        return ResponseEntity.ok().body(asset);
    }

    @Operation(summary = "", description = "")
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAssetForUser(
            @PathVariable long userId
    ) {
        User user = userService.getUserById(userId);
        List<Asset> assets = assetService.getUserAssets(user.getId());
        return ResponseEntity.ok().body(assets);
    }
}
