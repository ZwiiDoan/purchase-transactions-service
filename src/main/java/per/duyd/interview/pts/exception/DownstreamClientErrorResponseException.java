package per.duyd.interview.pts.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamClientErrorResponseException extends DownstreamErrorResponseException {
  public DownstreamClientErrorResponseException(String message, HttpStatusCode httpStatusCode) {
    super(message, httpStatusCode);
  }
}
