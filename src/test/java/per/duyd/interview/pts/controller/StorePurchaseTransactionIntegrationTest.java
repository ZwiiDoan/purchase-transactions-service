package per.duyd.interview.pts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import per.duyd.interview.pts.dto.PurchaseTransactionErrorResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;

class StorePurchaseTransactionIntegrationTest extends BasePurchaseTransactionIntegrationTest {

  @Test
  void shouldStorePurchaseTransactionSuccessfully() throws Exception {
    //When
    PurchaseTransactionResponse purchaseTransactionResponse =
        postPurchaseTransaction("/json/pts/request/request_valid.json");

    //Then
    assertThat(purchaseTransactionResponse.getPurchaseAmount()).isEqualTo(
        BigDecimal.valueOf(12345.03));
    assertThat(purchaseTransactionResponse.getTransactionId()).isNotNull();
  }

  @ParameterizedTest
  @MethodSource("shouldReturnExpectedErrorResponsesParams")
  void shouldReturnExpectedErrorResponses(String requestFile, int errorStatus,
                                          String responseFile) throws Exception {
    assertThat(postPurchaseTransactionForErrorResponse(requestFile, errorStatus))
        .isEqualTo(objectMapper.readValue(readFileToJsonString(responseFile),
            PurchaseTransactionErrorResponse.class));
  }

  public static Stream<Arguments> shouldReturnExpectedErrorResponsesParams() {
    return Stream.of(
        Arguments.of("/json/pts/request/request_invalid-transaction-date.json", 400,
            "/json/pts/response/response_invalid-transaction-date.json"),
        Arguments.of("/json/pts/request/request_future-transaction-date.json", 400,
            "/json/pts/response/response_future-transaction-date.json"),
        Arguments.of("/json/pts/request/request_invalid-transaction-description.json", 400,
            "/json/pts/response/response_invalid-transaction-description.json"),
        Arguments.of("/json/pts/request/request_invalid-purchase-amount.json", 400,
            "/json/pts/response/response_invalid-purchase-amount.json"),
        Arguments.of("/json/pts/request/request_missing-required-fields.json", 400,
            "/json/pts/response/response_missing-required-fields.json")
    );
  }

  public PurchaseTransactionErrorResponse postPurchaseTransactionForErrorResponse(
      String requestFile,
      int expectedErrorStatus) throws Exception {
    MvcResult mvcResult = mockMvc.perform(post(PURCHASE_TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(readFileToJsonString(requestFile)))
        .andExpect(status().is(expectedErrorStatus))
        .andReturn();

    return objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        PurchaseTransactionErrorResponse.class);
  }

}