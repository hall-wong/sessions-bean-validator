package com.github.hallwong.sessions.beanvalidator.dto.request;

import lombok.Data;

@Data
public class AssetItemCreateRequest {

  // not null
  private Integer index;

  // not blank
  private String name;

}
