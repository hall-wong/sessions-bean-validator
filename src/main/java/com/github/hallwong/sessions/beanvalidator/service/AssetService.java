package com.github.hallwong.sessions.beanvalidator.service;

import static java.util.Collections.emptyList;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import com.github.hallwong.sessions.beanvalidator.dto.request.AssetItemCreateRequest;
import com.github.hallwong.sessions.beanvalidator.dto.response.AssetResponse;
import com.github.hallwong.sessions.beanvalidator.error.BlankNameException;
import com.github.hallwong.sessions.beanvalidator.error.EmptyItemsException;
import com.github.hallwong.sessions.beanvalidator.error.ExpirationDateEarlyThanEffectiveDateException;
import com.github.hallwong.sessions.beanvalidator.error.NullIndexException;
import com.github.hallwong.sessions.beanvalidator.error.UnsortedItemsException;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

  private static final Pattern ASSET_KEY_PATTERN = Pattern.compile("(DSC-\\d{4}|OPT-\\d{5})");

  public List<AssetResponse> list(String key) {
    // implement of reading from the storage
    return emptyList();
  }

  public AssetResponse create(AssetCreateRequest request) {
    validateDates(request);
    validateItems(request);

    // implement of writing to the storage
    return AssetResponse.builder()
        .key(request.getKey())
        .name(request.getName())
        .effectiveDate(request.getEffectiveDate())
        .expirationDate(request.getExpirationDate())
        .createdAt(System.currentTimeMillis())
        .build();
  }

  private void validateDates(AssetCreateRequest request) {
    if (request.getExpirationDate().isBefore(request.getEffectiveDate())) {
      throw new ExpirationDateEarlyThanEffectiveDateException();
    }
  }

  private void validateItems(AssetCreateRequest request) {
    List<AssetItemCreateRequest> items = request.getItems();
    if (isEmpty(items)) {
      throw new EmptyItemsException();
    }
    Integer lastIndex = null;
    for (AssetItemCreateRequest item : items) {
      if (item == null) {
        throw new EmptyItemsException();
      }
      if (item.getIndex() == null) {
        throw new NullIndexException();
      } else if (lastIndex == null) {
        lastIndex = item.getIndex();
      } else if (lastIndex > item.getIndex()) {
        throw new UnsortedItemsException();
      }
      if (isBlank(item.getName())) {
        throw new BlankNameException();
      }
    }
  }

}
