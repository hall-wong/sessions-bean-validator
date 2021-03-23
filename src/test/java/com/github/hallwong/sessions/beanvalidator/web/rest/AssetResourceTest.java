package com.github.hallwong.sessions.beanvalidator.web.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
  void should_return_bad_request_when_list_assets_given_invalid_key() throws Exception {
    // given
    RequestBuilder request = get("/assets?key=123");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail", equalTo("The asset key is invalid.")));
  }

  @Test
  void should_return_bad_request_when_create_asset_given_invalid_key() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content("{\"key\": \"456\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail", equalTo("The asset key is invalid.")));
  }

  @Test
  void should_return_bad_request_when_create_asset_given_null_eff_date() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content("{\"key\": \"DSC-4391\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail", equalTo("The effective date must not be null.")));
  }

  @Test
  void should_return_bad_request_when_create_asset_given_exp_date_lt_eff_date() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content("{\"key\": \"DSC-4391\", \"effectiveDate\": \"2020-01-01\", \"expirationDate\": \"1999-01-01\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail", equalTo("The expiration date must be after effective date.")));
  }

}
