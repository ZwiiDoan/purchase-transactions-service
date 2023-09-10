package per.duyd.interview.pts.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.entity.ExchangeRateId;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, ExchangeRateId> {
  List<ExchangeRate> findByCurrencyAndEffectiveDateBetween(String currency, LocalDate from,
                                                           LocalDate to);
}
