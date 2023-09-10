package per.duyd.interview.pts.validation;

import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class TransactionDateValidator implements ConstraintValidator<TransactionDate, LocalDate> {
  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    return !value.isAfter(LocalDate.now(UTC_ZONE_ID));
  }
}
