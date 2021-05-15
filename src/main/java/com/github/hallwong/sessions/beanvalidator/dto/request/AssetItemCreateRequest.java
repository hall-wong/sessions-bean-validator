package com.github.hallwong.sessions.beanvalidator.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetItemCreateRequest implements Comparable<AssetItemCreateRequest> {

  @NotNull(message = "The index of the item must not null.")
  private Integer index;

  @NotBlank(message = "The name of the item must not blank.")
  private String name;

  @Override
  public int compareTo(AssetItemCreateRequest o) {
    if (this.index == null) {
      return 1;
    }
    if (o == null) {
      return -1;
    }
    Integer i = o.index;
    if (i == null) {
      return -1;
    }
    return this.index.compareTo(i);
  }

}
