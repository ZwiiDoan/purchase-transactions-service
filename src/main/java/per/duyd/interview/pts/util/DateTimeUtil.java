package per.duyd.interview.pts.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {
  private DateTimeUtil() {
    //For Jacoco Coverage
  }

  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static ZonedDateTime toUtcDateTime(LocalDate localDate) {
    return ZonedDateTime.of(localDate.atStartOfDay(), UTC_ZONE_ID);
  }

  public static long getSecondsBetween(ZonedDateTime start, ZonedDateTime end) {
    return end.toEpochSecond() - start.toEpochSecond();
  }
}
