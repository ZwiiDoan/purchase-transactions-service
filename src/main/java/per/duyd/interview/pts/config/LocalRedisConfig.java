package per.duyd.interview.pts.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile("(default | test)")
@Slf4j
public class LocalRedisConfig {
  @Value("${spring.data.redis.port}")
  private int redisPort;

  private RedisServer embeddedRedisServer;

  @PostConstruct
  public void postConstruct() {
    embeddedRedisServer = new RedisServer(redisPort);
    try {
      embeddedRedisServer.start();
    } catch (Exception ex) {
      log.warn("Failed to start embeddedRedisServer due to error: {}", ex.toString());
    }
  }

  @PreDestroy
  public void preDestroy() {
    embeddedRedisServer.stop();
  }
}
