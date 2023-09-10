package per.duyd.interview.pts.config;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import per.duyd.interview.pts.enums.CustomHttpHeaders;
import per.duyd.interview.pts.enums.MdcKeys;

@Configuration
@Slf4j
public class RestTemplateConfig {
  @Value("${services.treasury.connectTimeoutMs}")
  private int connectTimeoutMs = 1000;

  @Value("${services.treasury.readTimeoutMs}")
  private int readTimeoutMs = 10000;

  @Bean
  public RestTemplate treasuryRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                           ResponseErrorHandler commonResponseErrorHandler) {
    return restTemplateBuilder.defaultHeader(CustomHttpHeaders.CORRELATION_ID.value(),
            MDC.get(MdcKeys.CORRELATION_ID.value()))
        .errorHandler(commonResponseErrorHandler)
        .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
        .setReadTimeout(Duration.ofMillis(readTimeoutMs))
        .build();
  }
}
