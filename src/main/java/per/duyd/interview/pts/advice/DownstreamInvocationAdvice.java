package per.duyd.interview.pts.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DownstreamInvocationAdvice {
  @Around("@annotation(trackDownstreamInvocation)")
  public Object trackInvocation(
      ProceedingJoinPoint point,
      TrackDownstreamInvocation trackDownstreamInvocation) throws Throwable {
    String downstreamName = trackDownstreamInvocation.downstreamName();
    String endpoint = trackDownstreamInvocation.endpoint();
    long starTime = System.currentTimeMillis();

    log.info("message=\"Calling Downstream\", api=\"{}\", endpoint=\"{}\"", downstreamName,
        endpoint);
    try {
      return point.proceed();
    } finally {
      log.info(
          "message=\"Completed Downstream Call\", api=\"{}\", elapsed_time_ms=\"{}\", endpoint=\"{}\"",
          downstreamName, System.currentTimeMillis() - starTime, endpoint);
    }
  }
}
