package per.duyd.interview.pts.entity;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("ExchangeRate")
@Data
@Builder
public class ExchangeRate {
  @Id
  private String currency;

  @NotNull
  private LocalDate recordDate;

  @NotNull
  private Double exchangeRate;

  @TimeToLive
  private Long expirationInSeconds;
}
