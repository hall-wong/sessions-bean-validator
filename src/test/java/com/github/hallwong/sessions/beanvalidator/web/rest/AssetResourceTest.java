package com.github.hallwong.sessions.beanvalidator.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
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
import org.springframework.web.filter.CharacterEncodingFilter;

@ActiveProfiles(profiles = "test")
@SpringBootTest
class AssetResourceTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .addFilter(new CharacterEncodingFilter("utf8", true))
        .build();
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  void when_create_asset_should_return_bad_request_given_null_key() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
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
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content("{\"key\": \"T-123\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_500_weight() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content("{\"key\": \"DSC-1323\", \"weight\": 500}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_3_digit_weight() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content("{\"key\": \"DSC-1323\", \"weight\": 123.232}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_exp_date_before_eff_date()
      throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content(
            "{\"key\": \"DSC-1323\", \"effectiveDate\": \"2020-12-21\", \"expirationDate\": \"2011-12-21\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_exp_date_before_eff_date_in_cn()
      throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Accept-Language", "zh-CN")
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content(
            "{\"key\": \"DSC-1323\", \"effectiveDate\": \"2020-12-21\", \"expirationDate\": \"2011-12-21\"}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_ok_given_exp_date_not_before_eff_date()
      throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content(
            "{\"key\": \"DSC-1323\", \"effectiveDate\": \"2020-12-21\", \"expirationDate\": \"2020-12-21\", \"items\": [{\"index\": 1, \"name\": \"i\"}]}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isOk());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_all_null_value_item() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content("{\"items\": [{}]}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_items_not_in_ascending() throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8")
        .content(
            "{\"key\": \"DSC-1323\", \"effectiveDate\": \"2020-12-21\", \"items\": [{\"index\": 12, \"name\": \"i\"}, {\"index\": 1, \"name\": \"m\"}]}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_create_asset_should_return_bad_request_given_not_null_items_and_user_auth()
      throws Exception {
    // given
    RequestBuilder request = post("/assets")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "user")
        .characterEncoding("UTF-8")
        .content(
            "{\"key\": \"DSC-1323\", \"effectiveDate\": \"2020-12-21\", \"items\": [{\"index\": 1, \"name\": \"i\"}, {\"index\": 2, \"name\": \"m\"}]}");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  void when_list_assets_should_return_bad_request_given_not_valid_key() throws Exception {
    // given
    RequestBuilder request = get("/assets?key=T-123")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "admin")
        .characterEncoding("UTF-8");

    // when
    ResultActions result = mockMvc.perform(request).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
  }

}
