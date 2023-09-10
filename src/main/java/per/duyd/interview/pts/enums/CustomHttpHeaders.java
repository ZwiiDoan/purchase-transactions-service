package per.duyd.interview.pts.enums;

public enum CustomHttpHeaders {
  CORRELATION_ID("Correlation-Id");

  private final String value;

  CustomHttpHeaders(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
