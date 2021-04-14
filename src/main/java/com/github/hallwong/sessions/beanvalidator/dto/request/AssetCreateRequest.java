package com.github.hallwong.sessions.beanvalidator.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class AssetCreateRequest {

  private String key;

  private String name;

  private Double w;

  private LocalDate e;

  private LocalDate x;

  private List<AssetItemCreateRequest> items;

}
