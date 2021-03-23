package com.github.hallwong.sessions.beanvalidator.dto.request;

import java.time.LocalDate;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssetCreateRequest {

  @Pattern(regexp = "(DSC-\\d{4}|OPT-\\d{5})", message = "The asset key is invalid.")
  private String key;

  private String name;

  private LocalDate effectiveDate;

  private LocalDate expirationDate;

}
