package per.duyd.interview.pts.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.dto.ExchangeRateResponse;
import per.duyd.interview.pts.exception.DataNotFoundException;

@ExtendWith(MockitoExtension.class)
class TreasuryClientTest {

  @Mock
  private RestTemplate treasuryRestTemplate;

  @InjectMocks
  private TreasuryClient treasuryClient;

  private final String sixMonthsAgoDate = LocalDate.now(UTC_ZONE_ID).minusMonths(6)
      .format(DateTimeFormatter.ISO_LOCAL_DATE);

  private ResponseEntity<ExchangeRateResponse> responseEntity;
  private ArgumentCaptor<String> upstreamUrlCaptor;
  private static final String TEST_CURRENCY = "Country-Currency";

  @BeforeEach
  void beforeEachTest() {
    responseEntity = mock(ResponseEntity.class);
    upstreamUrlCaptor = ArgumentCaptor.forClass(String.class);
    when(treasuryRestTemplate.getForEntity(upstreamUrlCaptor.capture(),
        eq(ExchangeRateResponse.class))).thenReturn(responseEntity);
  }

  @Test
  void shouldGetLatestExchangeNotOlderThan6Months() {
    //Given
    ExchangeRateDto expectedExchangeRateDto = ExchangeRateDto.builder().build();
    ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
        .exchangeRateDtos(List.of(expectedExchangeRateDto, ExchangeRateDto.builder().build(),
            ExchangeRateDto.builder().build()))
        .build();
    when(responseEntity.getBody()).thenReturn(exchangeRateResponse);

    //When
    ExchangeRateDto actualExchangeRateDto = treasuryClient.getLatestExchangeRate(TEST_CURRENCY);

    //Then
    assertThat(actualExchangeRateDto).isEqualTo(expectedExchangeRateDto);
    assertThat(upstreamUrlCaptor.getValue()).isEqualTo(String.format(
        "http://localhost:8090/v1/accounting/od/rates_of_exchange?fields=record_date,"
            + "exchange_rate,country_currency_desc&filter=country_currency_desc:eq:Country"
            + "-Currency,record_date:gte:%s&sort=-record_date&page[number]=1&page[size]=1",
        sixMonthsAgoDate));
  }

  @Test
  void shouldThrowDataNotFoundExceptionWhenNoExchangeRateFoundInTreasuryApi() {
    //Given
    ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
        .exchangeRateDtos(List.of()).build();
    when(responseEntity.getBody()).thenReturn(exchangeRateResponse);

    //When & Then
    assertThrows(DataNotFoundException.class,
        () -> treasuryClient.getLatestExchangeRate(TEST_CURRENCY));
  }
}