package com.github.hallwong.sessions.beanvalidator.dto.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AssetCreateRequest {

  private String key;

  private String name;

  private LocalDate effectiveDate;

  private LocalDate expirationDate;

}
