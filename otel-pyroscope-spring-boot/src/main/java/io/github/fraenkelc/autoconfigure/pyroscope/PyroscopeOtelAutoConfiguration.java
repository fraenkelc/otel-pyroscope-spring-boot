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
package io.github.fraenkelc.autoconfigure.pyroscope;

import io.opentelemetry.sdk.trace.SpanProcessor;
import io.otel.pyroscope.PyroscopeOtelConfiguration;
import io.otel.pyroscope.PyroscopeOtelSpanProcessor;
import io.otel.pyroscope.shadow.javaagent.PyroscopeAgent;
import io.otel.pyroscope.shadow.javaagent.config.Config;
import org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(before = OpenTelemetryAutoConfiguration.class)
public class PyroscopeOtelAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass({SpanProcessor.class, PyroscopeOtelSpanProcessor.class})
  @EnableConfigurationProperties(PyroscopeOtelProperties.class)
  public static class SpanProcessorConfiguration {
    private final PyroscopeOtelProperties pyroscopeOtelProperties;

    public SpanProcessorConfiguration(PyroscopeOtelProperties pyroscopeOtelProperties) {
      this.pyroscopeOtelProperties = pyroscopeOtelProperties;
      if (pyroscopeOtelProperties.isStartProfiling()) {
        Config pyroConfig = Config.build();
        PyroscopeAgent.start(pyroConfig);
      }
    }

    @Bean
    PyroscopeOtelSpanProcessor pyroscopeOtelSpanProcessor() {
      PyroscopeOtelConfiguration pyroOtelConfig =
          new PyroscopeOtelConfiguration.Builder()
              .setRootSpanOnly(pyroscopeOtelProperties.isRootSpanOnly())
              .setAddSpanName(pyroscopeOtelProperties.isAddSpanName())
              .build();
      return new PyroscopeOtelSpanProcessor(pyroOtelConfig);
    }
  }
}
