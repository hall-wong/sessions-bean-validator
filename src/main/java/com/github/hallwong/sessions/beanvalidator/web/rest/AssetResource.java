package com.github.hallwong.sessions.beanvalidator.web.rest;

import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import com.github.hallwong.sessions.beanvalidator.dto.response.AssetResponse;
import com.github.hallwong.sessions.beanvalidator.service.AssetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetResource {

  private final AssetService service;

  @GetMapping
  public List<AssetResponse> list(@RequestParam(name = "key", required = false) String key) {
    return service.list(key);
  }

  @PostMapping
  public AssetResponse create(@RequestBody AssetCreateRequest request){
    return service.create(request);
  }

}
