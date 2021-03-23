package com.github.hallwong.sessions.beanvalidator.service;

import static java.util.Collections.emptyList;

import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import com.github.hallwong.sessions.beanvalidator.dto.response.AssetResponse;
import com.github.hallwong.sessions.beanvalidator.error.ExpirationDateEarlyThanEffectiveDateException;
import com.github.hallwong.sessions.beanvalidator.error.InvalidAssetKeyException;
import com.github.hallwong.sessions.beanvalidator.error.NullEffectiveDateException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

  private static final Pattern ASSET_KEY_PATTERN = Pattern.compile("(DSC-\\d{4}|OPT-\\d{5})");

  public List<AssetResponse> list(String key) {
    if (key != null && !key.trim().isEmpty()) {
      validateAssetKey(key);
    }
    // implement of reading from the storage
    return emptyList();
  }

  public AssetResponse create(AssetCreateRequest request){
    validateAssetKey(request.getKey());

    if(request.getEffectiveDate() == null){
      throw new NullEffectiveDateException();
    }
    if(request.getExpirationDate() != null && request.getExpirationDate().isBefore(request.getEffectiveDate())){
      throw new ExpirationDateEarlyThanEffectiveDateException();
    }

    // implement of writing to the storage
    return AssetResponse.builder()
        .key(request.getKey())
        .name(request.getName())
        .effectiveDate(request.getEffectiveDate())
        .expirationDate(request.getExpirationDate())
        .createdAt(System.currentTimeMillis())
        .build();
  }

  private void validateAssetKey(String key) {
    Matcher matcher = ASSET_KEY_PATTERN.matcher(key);
    if (!matcher.matches()) {
      throw new InvalidAssetKeyException();
    }
  }

}
