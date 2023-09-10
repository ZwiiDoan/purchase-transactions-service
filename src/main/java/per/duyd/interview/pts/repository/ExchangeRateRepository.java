package per.duyd.interview.pts.repository;

import org.springframework.data.repository.CrudRepository;
import per.duyd.interview.pts.entity.ExchangeRate;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, String> {
}
