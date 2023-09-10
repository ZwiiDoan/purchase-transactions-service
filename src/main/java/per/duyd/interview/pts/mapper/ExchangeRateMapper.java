package per.duyd.interview.pts.mapper;

import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.entity.ExchangeRate;

public class ExchangeRateMapper {
  private ExchangeRateMapper() {
    //For Jacoco Coverage
  }

  public static ExchangeRate fromExchangeRateDto(ExchangeRateDto exchangeRateDto,
                                                 long expirationInSeconds) {
    return ExchangeRate.builder()
        .currency(exchangeRateDto.getCurrency())
        .exchangeRate(exchangeRateDto.getExchangeRate())
        .recordDate(exchangeRateDto.getRecordDate())
        .expirationInSeconds(expirationInSeconds)
        .build();
  }
}
