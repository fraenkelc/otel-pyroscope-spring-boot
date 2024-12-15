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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("otel.pyroscope")
public class PyroscopeOtelProperties {
  /** Whether to start PyroscopeAgent automatically. */
  private boolean startProfiling = true;

  /** Whether to link the root span only. */
  private boolean rootSpanOnly = true;

  /** Whether to add the span name as a label. */
  private boolean addSpanName = false;

  public boolean isStartProfiling() {
    return startProfiling;
  }

  public void setStartProfiling(boolean startProfiling) {
    this.startProfiling = startProfiling;
  }

  public boolean isRootSpanOnly() {
    return rootSpanOnly;
  }

  public void setRootSpanOnly(boolean rootSpanOnly) {
    this.rootSpanOnly = rootSpanOnly;
  }

  public boolean isAddSpanName() {
    return addSpanName;
  }

  public void setAddSpanName(boolean addSpanName) {
    this.addSpanName = addSpanName;
  }
}
