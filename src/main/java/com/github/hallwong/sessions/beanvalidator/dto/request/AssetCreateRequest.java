package com.github.hallwong.sessions.beanvalidator.dto.request;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.AscendingCollectionConstraint;
import com.github.hallwong.sessions.beanvalidator.dto.constraints.AssetKeyConstraint;
import com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint;
import com.github.hallwong.sessions.beanvalidator.dto.constraints.groups.AssetCreateAdmin;
import com.github.hallwong.sessions.beanvalidator.dto.constraints.groups.AssetCreateUser;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Data;

@Data
@ExpirationDateConstraint(allowEqualToEffectiveDate = true)
public class AssetCreateRequest {

  @NotNull
  @AssetKeyConstraint
  private String key;

  private String name;

  @Max(450)
  @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Only 2 digits are allowed after the decimal point.")
  private Double weight;

  @NotNull
  private LocalDate effectiveDate;

  private LocalDate expirationDate;

  @Valid
  @Null(groups = AssetCreateUser.class)
  @NotEmpty(groups = AssetCreateAdmin.class)
  @AscendingCollectionConstraint
  private List<AssetItemCreateRequest> items;

}
