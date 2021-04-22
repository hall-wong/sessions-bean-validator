package com.github.hallwong.sessions.beanvalidator.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles(profiles = "test")
@SpringBootTest
class AssetResourceTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  void when_create_asset_should_return_bad_request_given_null_key() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content("{}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_not_valid_key() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content("{\"key\": \"T-123\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

}
