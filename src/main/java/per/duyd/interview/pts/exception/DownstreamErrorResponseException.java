package per.duyd.interview.pts.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamErrorResponseException extends RuntimeException {
  private final HttpStatusCode httpStatusCode;

  public DownstreamErrorResponseException(String message, HttpStatusCode httpStatusCode) {
    super(message);
    this.httpStatusCode = httpStatusCode;
  }

  public HttpStatusCode getHttpStatusCode() {
    return httpStatusCode;
  }
}
