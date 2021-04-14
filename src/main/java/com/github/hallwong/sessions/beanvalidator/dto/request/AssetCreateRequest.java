package com.github.hallwong.sessions.beanvalidator.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class AssetCreateRequest {

  private String key;

  private String name;

  // no more than 450, only allowed 2 digits after decimal point
  private Double weight;

  private LocalDate effectiveDate;

  // allow null or not early than effective date
  private LocalDate expirationDate;

  // not empty, must be ascending by index
  private List<AssetItemCreateRequest> items;

}
