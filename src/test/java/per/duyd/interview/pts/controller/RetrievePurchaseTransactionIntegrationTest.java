package per.duyd.interview.pts.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import per.duyd.interview.pts.dto.ConvertedTransactionResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionErrorResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;

public class RetrievePurchaseTransactionIntegrationTest
    extends BasePurchaseTransactionIntegrationTest {
  @RegisterExtension
  static WireMockExtension treasuryWiremock = WireMockExtension.newInstance()
      .options(wireMockConfig().port(8090))
      .build();

  @Test
  void shouldRetrievePurchaseTransactionSuccessfully() throws Exception {
    //Given
    treasuryWiremock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
            urlEqualTo(getTreasuryExchangeRateUrl()))
        .willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200).withBody(
                readFileToJsonString("/json/treasury/exchange-rate_200-response.json"))));

    PurchaseTransactionResponse purchaseTransactionResponse =
        postPurchaseTransaction("/json/pts/request/request_valid.json");

    //When
    MvcResult mvcResult = mockMvc.perform(
            get(PURCHASE_TRANSACTION_PATH + "/" + purchaseTransactionResponse.getTransactionId()
                + "?currency=Australia-Dollar"))
        .andExpect(status().is(200))
        .andReturn();

    ConvertedTransactionResponse actualResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            ConvertedTransactionResponse.class);

    //Then
    ConvertedTransactionResponse expectedResponse = ConvertedTransactionResponse.builder()
        .transactionId(purchaseTransactionResponse.getTransactionId())
        .purchaseAmount(purchaseTransactionResponse.getPurchaseAmount())
        .exchangeRate(1.326)
        .transactionDate(purchaseTransactionResponse.getTransactionDate())
        .description(purchaseTransactionResponse.getDescription())
        .convertedAmount(BigDecimal.valueOf(16369.51))
        .build();

    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void shouldReturnErrorResponseWhenPurchaseTransactionDoesNotExist() throws Exception {
    //When
    MvcResult mvcResult = mockMvc.perform(
            get(PURCHASE_TRANSACTION_PATH + "/" + UUID.randomUUID()
                + "?currency=Australia-Dollar"))
        .andExpect(status().is(404))
        .andReturn();

    PurchaseTransactionErrorResponse purchaseTransactionErrorResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            PurchaseTransactionErrorResponse.class);

    //Then
    assertThat(purchaseTransactionErrorResponse)
        .isEqualTo(objectMapper.readValue(
            readFileToJsonString(
                "/json/pts/response/response_purchase-transaction-does-not-exist.json"),
            PurchaseTransactionErrorResponse.class));
  }

  @ParameterizedTest
  @MethodSource("shouldReturnExpectedErrorResponsesParams")
  void shouldReturnExpectedErrorResponsesForExchangeRateErrors(
      String treasuryResponseFile,
      int treasuryResponseStatus,
      int errorStatus,
      String responseFile) throws Exception {
    //Given
    treasuryWiremock.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
            urlEqualTo(getTreasuryExchangeRateUrl()))
        .willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(treasuryResponseStatus)
            .withBody(readFileToJsonString(treasuryResponseFile))));

    PurchaseTransactionResponse purchaseTransactionResponse =
        postPurchaseTransaction("/json/pts/request/request_valid.json");

    //When
    MvcResult mvcResult = mockMvc.perform(
            get(PURCHASE_TRANSACTION_PATH + "/" + purchaseTransactionResponse.getTransactionId()
                + "?currency=Australia-Dollar"))
        .andExpect(status().is(errorStatus))
        .andReturn();

    PurchaseTransactionErrorResponse purchaseTransactionErrorResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            PurchaseTransactionErrorResponse.class);

    //Then
    assertThat(purchaseTransactionErrorResponse)
        .isEqualTo(objectMapper.readValue(readFileToJsonString(responseFile),
            PurchaseTransactionErrorResponse.class));
  }

  public static Stream<Arguments> shouldReturnExpectedErrorResponsesParams() {
    return Stream.of(
        Arguments.of("/json/treasury/exchange-rate_400-response.json", 400, 400,
            "/json/pts/response/response_treasury-400.json"),
        Arguments.of("/json/treasury/exchange-rate_429-response.json", 429, 429,
            "/json/pts/response/response_treasury-429.json"),
        Arguments.of("/json/treasury/exchange-rate_404-response.json", 404, 404,
            "/json/pts/response/response_treasury-404.json"),
        Arguments.of("/json/treasury/exchange-rate_500-response.json", 500, 500,
            "/json/pts/response/response_treasury-500.json"),
        Arguments.of("/json/treasury/exchange-rate_empty-response.json", 200, 404,
            "/json/pts/response/response_treasury-exchange-rate-empty.json")
    );
  }

  private String getTreasuryExchangeRateUrl() {
    return String.format("/v1/accounting/od/rates_of_exchange"
            + "?fields=effective_date,exchange_rate,country_currency_desc"
            + "&filter=country_currency_desc:eq:Australia-Dollar,"
            + "effective_date:gte:%s,effective_date:lte:%s&sort=-effective_date",
        TRANSACTION_DATE.minusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE),
        TRANSACTION_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE)
    ) + "&page%5Bnumber%5D=1&page%5Bsize%5D=1";
  }
}
