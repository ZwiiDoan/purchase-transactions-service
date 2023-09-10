package per.duyd.interview.pts.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamServerErrorResponseException extends DownstreamErrorResponseException {
  public DownstreamServerErrorResponseException(String message, HttpStatusCode httpStatusCode) {
    super(message, httpStatusCode);
  }
}
