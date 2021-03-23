package com.github.hallwong.sessions.beanvalidator.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetResponse {

  private String key;

  private String name;

  private LocalDate effectiveDate;

  private LocalDate expirationDate;

  private Long createdAt;

}
