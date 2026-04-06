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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Pyroscope OpenTelemetry integration. These properties are bound to
 * the 'otel.pyroscope' prefix in application properties.
 */
@ConfigurationProperties("otel.pyroscope")
public class PyroscopeOtelProperties {
  private boolean startProfiling = true;

  /**
   * Returns whether profiling should be started automatically.
   *
   * @return true if profiling should be started, false otherwise
   */
  public boolean isStartProfiling() {
    return startProfiling;
  }

  /**
   * Sets whether profiling should be started automatically.
   *
   * @param startProfiling true to start profiling, false otherwise
   */
  public void setStartProfiling(boolean startProfiling) {
    this.startProfiling = startProfiling;
  }
}
