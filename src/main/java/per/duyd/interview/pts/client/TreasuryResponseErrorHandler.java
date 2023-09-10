package per.duyd.interview.pts.client;

import static per.duyd.interview.pts.exception.DataNotFoundException.EXCHANGE_RATE_NOT_FOUND_EXCEPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import per.duyd.interview.pts.dto.ExchangeRateErrorResponse;
import per.duyd.interview.pts.exception.DownstreamClientErrorResponseException;
import per.duyd.interview.pts.exception.DownstreamServerErrorResponseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TreasuryResponseErrorHandler implements ResponseErrorHandler {

  private final ObjectMapper objectMapper;

  @Override
  public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
    return response.getStatusCode().isError();
  }

  @Override
  public void handleError(@NotNull ClientHttpResponse response) throws IOException {
    log.error("message=\"Error Response from downstream\", status_code=\"{}\"",
        response.getStatusCode());

    ExchangeRateErrorResponse errorResponseBody = objectMapper.readValue(response.getBody(),
        ExchangeRateErrorResponse.class);

    log.debug("message=\"Error Response from downstream\", body=\"{}\"", errorResponseBody);

    HttpStatusCode statusCode = response.getStatusCode();

    if (statusCode.equals(HttpStatus.NOT_FOUND)) {
      throw EXCHANGE_RATE_NOT_FOUND_EXCEPTION;
    } else if (statusCode.is4xxClientError()) {
      throw new DownstreamClientErrorResponseException(errorResponseBody.getMessage(), statusCode);
    } else {
      throw new DownstreamServerErrorResponseException(errorResponseBody.getMessage(), statusCode);
    }
  }
}
