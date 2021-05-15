package com.github.hallwong.sessions.beanvalidator.service;

import static java.util.Collections.emptyList;

import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import com.github.hallwong.sessions.beanvalidator.dto.response.AssetResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

  public List<AssetResponse> list(String key) {
    // implement of reading from the storage
    return emptyList();
  }

  public AssetResponse create(AssetCreateRequest request) {
    // implement of writing to the storage
    return AssetResponse.builder()
        .key(request.getKey())
        .name(request.getName())
        .effectiveDate(request.getEffectiveDate())
        .expirationDate(request.getExpirationDate())
        .createdAt(System.currentTimeMillis())
        .build();
  }

}
