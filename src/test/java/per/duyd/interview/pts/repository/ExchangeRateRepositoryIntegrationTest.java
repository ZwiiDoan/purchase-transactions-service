package per.duyd.interview.pts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import per.duyd.interview.pts.entity.ExchangeRate;

@DataJpaTest
class ExchangeRateRepositoryIntegrationTest {

  @Autowired
  private ExchangeRateRepository exchangeRateRepository;

  private ExchangeRate expectedExchangeRate;

  private static final String TEST_CURRENCY = "Country-Currency";

  @BeforeEach
  void beforeEachTest() {
    //Given
    expectedExchangeRate = exchangeRateRepository.save(ExchangeRate.builder()
        .exchangeRate(1.2).currency(TEST_CURRENCY).effectiveDate(LocalDate.of(2023, 1, 1))
        .build());
  }

  @ParameterizedTest
  @MethodSource("shouldFindExchangeRateByCurrencyAndEffectiveDateBetweenParams")
  void shouldFindExchangeRateByCurrencyAndEffectiveDateBetween(LocalDate start, LocalDate end) {
    assertThat(exchangeRateRepository.findByCurrencyAndEffectiveDateBetween(TEST_CURRENCY,
        start, end)).hasSize(1).isEqualTo(List.of(expectedExchangeRate));
  }

  @Test
  void shouldReturnEmptyListWhenNoValidExchangeRateFound() {
    assertThat(exchangeRateRepository.findByCurrencyAndEffectiveDateBetween(TEST_CURRENCY,
        LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 31))).isEmpty();
  }

  public static Stream<Arguments> shouldFindExchangeRateByCurrencyAndEffectiveDateBetweenParams() {
    return Stream.of(
        Arguments.of(LocalDate.of(2022, 10, 1), LocalDate.of(2023, 4, 1)),
        Arguments.of(LocalDate.of(2022, 10, 1), LocalDate.of(2023, 1, 1)),
        Arguments.of(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 6, 1))
    );
  }
}