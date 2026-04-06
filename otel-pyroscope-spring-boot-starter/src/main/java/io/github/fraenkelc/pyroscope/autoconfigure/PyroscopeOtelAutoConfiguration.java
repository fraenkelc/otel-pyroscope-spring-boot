/*
 * Copyright 2024 Christian Fränkel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.fraenkelc.pyroscope.autoconfigure;

import io.opentelemetry.sdk.trace.SpanProcessor;
import io.otel.pyroscope.PyroscopeOtelSpanProcessor;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.api.ConfigurationProvider;
import io.pyroscope.javaagent.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.tracing.opentelemetry.autoconfigure.OpenTelemetryTracingAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for integrating Pyroscope profiling with
 * OpenTelemetry in Spring Boot applications. This configuration sets up the necessary beans for
 * linking spans to Pyroscope profiles.
 */
@AutoConfiguration(before = OpenTelemetryTracingAutoConfiguration.class)
@ConditionalOnClass({SpanProcessor.class, PyroscopeOtelSpanProcessor.class, PyroscopeAgent.class})
@EnableConfigurationProperties(PyroscopeOtelProperties.class)
public class PyroscopeOtelAutoConfiguration {

  @Bean
  PyroscopeOtelSpanProcessor pyroscopeOtelSpanProcessor() {
    return new PyroscopeOtelSpanProcessor();
  }

  @Bean
  @ConditionalOnMissingBean
  ConfigurationProvider configurationProvider(Environment environment) {
    return new SpringEnvironmentConfigurationProvider(environment);
  }

  @Configuration(proxyBeanMethods = false)
  public static class PyroscopeStarter {
    public PyroscopeStarter(
        PyroscopeOtelProperties pyroscopeOtelProperties,
        ConfigurationProvider configurationProvider) {
      if (pyroscopeOtelProperties.isStartProfiling() && !PyroscopeAgent.isStarted()) {
        PyroscopeAgent.start(Config.build(configurationProvider));
      }
    }
  }
}
