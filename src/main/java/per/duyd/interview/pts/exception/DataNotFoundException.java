package per.duyd.interview.pts.exception;

import static per.duyd.interview.pts.enums.ErrorCode.EXCHANGE_RATE_NOT_FOUND;

import per.duyd.interview.pts.enums.ErrorCode;

public class DataNotFoundException extends RuntimeException {
  private final ErrorCode errorCode;

  public DataNotFoundException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public static final DataNotFoundException EXCHANGE_RATE_NOT_FOUND_EXCEPTION =
      new DataNotFoundException("No valid exchange rate found for specified currency",
          EXCHANGE_RATE_NOT_FOUND);

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
