package per.duyd.interview.pts.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import per.duyd.interview.pts.dto.ExchangeRateErrorResponse;
import per.duyd.interview.pts.exception.DataNotFoundException;
import per.duyd.interview.pts.exception.DownstreamClientErrorResponseException;
import per.duyd.interview.pts.exception.DownstreamServerErrorResponseException;

@ExtendWith(MockitoExtension.class)
class TreasuryResponseErrorHandlerTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private TreasuryResponseErrorHandler treasuryResponseErrorHandler;

  private ClientHttpResponse response;

  @BeforeEach
  void beforeEachTest() throws IOException {
    response = mock(ClientHttpResponse.class);
  }

  @ParameterizedTest
  @CsvSource({"200,false", "400,true", "500,true", "304,false"})
  void shouldReturnErrorBasedOnResponseStatus(int httpStatusCode, boolean expectedError)
      throws IOException {
    when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(httpStatusCode));

    assertThat(treasuryResponseErrorHandler.hasError(response)).isEqualTo(expectedError);
  }

  @ParameterizedTest
  @MethodSource("shouldThrowExpectedExceptionsForUpstreamResponseErrorsParams")
  void shouldThrowExpectedExceptionsForUpstreamResponseErrors(
      int httpStatusCode,
      Class<Exception> expectedExcepionClass)
      throws IOException {
    when(response.getBody()).thenReturn(mock(InputStream.class));
    when(objectMapper.readValue(any(InputStream.class),
        eq(ExchangeRateErrorResponse.class))).thenReturn(mock(ExchangeRateErrorResponse.class));
    when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(httpStatusCode));
    
    assertThrows(expectedExcepionClass, () -> treasuryResponseErrorHandler.handleError(response));
  }

  public static Stream<Arguments> shouldThrowExpectedExceptionsForUpstreamResponseErrorsParams() {
    return Stream.of(
        Arguments.of(429, DownstreamClientErrorResponseException.class),
        Arguments.of(404, DataNotFoundException.class),
        Arguments.of(500, DownstreamServerErrorResponseException.class)
    );
  }
}