package per.duyd.interview.pts.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import per.duyd.interview.pts.enums.CustomHttpHeaders;
import per.duyd.interview.pts.enums.MdcKeys;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
  private String getCorrelationId(@NotNull HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(CustomHttpHeaders.CORRELATION_ID.value()))
        .orElse(UUID.randomUUID().toString());
  }

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
                                  @NotNull HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {
    try {
      MDC.put(MdcKeys.CORRELATION_ID.value(), getCorrelationId(request));
      log.info("request_method=\"{}\", request_path=\"{}\"", request.getMethod(),
          request.getRequestURI());
      filterChain.doFilter(request, response);
      log.info("response_status=\"{}\", request_method=\"{}\", request_path=\"{}\"",
          response.getStatus(), request.getMethod(),
          request.getRequestURI());
    } finally {
      MDC.remove(MdcKeys.CORRELATION_ID.value());
    }
  }
}