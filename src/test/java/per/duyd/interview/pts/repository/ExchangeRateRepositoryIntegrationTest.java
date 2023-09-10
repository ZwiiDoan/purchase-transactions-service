package per.duyd.interview.pts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import per.duyd.interview.pts.entity.ExchangeRate;

@DataJpaTest
class ExchangeRateRepositoryIntegrationTest {

  @Autowired
  private ExchangeRateRepository exchangeRateRepository;

  @Test
  void shouldFindExchangeRateByCurrencyAndEffectiveDateBetween() {
    //Given
    ExchangeRate expectedExchangeRate = exchangeRateRepository.save(ExchangeRate.builder()
        .exchangeRate(1.2).currency("Country-Currency").effectiveDate(LocalDate.of(2023, 1, 1))
        .build());

    //When & Then
    assertThat(exchangeRateRepository.findByCurrencyAndEffectiveDateBetween("Country-Currency",
        LocalDate.of(2022, 10, 1), LocalDate.of(2023, 4, 1)))
        .hasSize(1)
        .isEqualTo(List.of(expectedExchangeRate));
  }
}