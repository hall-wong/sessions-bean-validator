package com.github.hallwong.sessions.beanvalidator.dto.request;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssetCreateRequest {

  @NotNull(message = "The asset key must not be null.")
  @Pattern(regexp = "(DSC-\\d{4}|OPT-\\d{5})", message = "The asset key is invalid.")
  private String key;

  private String name;

  @Max(value = 450, message = "The weight of the assert is over 450.")
  @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Only 2 digits are allowed after the decimal point.")
  private Double weight;

  @NotNull(message = "The effective date must not be null.")
  private LocalDate effectiveDate;

  // allow null or not early than effective date
  private LocalDate expirationDate;

  // not empty, must be ascending by index
  private List<AssetItemCreateRequest> items;

}
