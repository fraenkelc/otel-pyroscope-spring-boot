/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.fraenkelc.autoconfigure.pyroscope;

import io.otel.pyroscope.PyroscopeOtelSpanProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests for {@link PyroscopeOtelAutoConfiguration}. */
class PyroscopeOtelAutoConfigurationTests {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(PyroscopeOtelAutoConfiguration.class))
          .withPropertyValues("otel.pyroscope.start-profiling=false");

  @Test
  void isRegisteredInAutoConfigurationImports() {
    assertThat(ImportCandidates.load(AutoConfiguration.class, null).getCandidates())
        .contains(PyroscopeOtelAutoConfiguration.class.getName());
  }

  @Test
  void shouldProvideBeans() {
    this.runner.run(
        (context) -> assertThat(context).hasSingleBean(PyroscopeOtelSpanProcessor.class));
  }

  @Test
  void shouldBackOffIfOpenTelemetryIsNotOnClasspath() {
    this.runner
        .withClassLoader(new FilteredClassLoader("io.opentelemetry"))
        .run((context) -> assertThat(context).doesNotHaveBean(PyroscopeOtelSpanProcessor.class));
  }

  @Test
  void shouldBackOffIfOtelPyroscopeIsNotOnClasspath() {
    this.runner
        .withClassLoader(new FilteredClassLoader("io.otel.pyroscope"))
        .run((context) -> assertThat(context).doesNotHaveBean(PyroscopeOtelSpanProcessor.class));
  }
}
