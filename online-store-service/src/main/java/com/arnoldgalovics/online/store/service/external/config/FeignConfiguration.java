package com.arnoldgalovics.online.store.service.external.config;

import feign.Capability;
import feign.Logger;
import feign.micrometer.MicrometerCapability;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;

@Configuration
@EnableFeignClients(basePackages = "com.arnoldgalovics.online.store.service.external")
public class FeignConfiguration implements FeignFormatterRegistrar {
    @Bean
    public Capability jmxCapability() {
        return new MicrometerCapability(new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM));
    }

    @Bean
    public Logger.Level loggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addFormatter(new OffsetDateTimeToMillisFormatter());
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer() {
        CircuitBreakerConfig userSessionClientConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(COUNT_BASED)
                .slidingWindowSize(5)
                .failureRateThreshold(20.0f)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slowCallDurationThreshold(Duration.ofSeconds(5))
                .slowCallRateThreshold(80)
                .build();
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).cancelRunningFuture(true).build();
        return resilience4JCircuitBreakerFactory -> {
            resilience4JCircuitBreakerFactory.configure((builder) ->
                    builder.circuitBreakerConfig(userSessionClientConfig).timeLimiterConfig(timeLimiterConfig), "UserSessionClient#validateSession(Map,Map)");
        };
    }

//    @Bean
//    public StatusAggregator statusAggregator() {
//        return new SimpleStatusAggregator();
//    }
}
