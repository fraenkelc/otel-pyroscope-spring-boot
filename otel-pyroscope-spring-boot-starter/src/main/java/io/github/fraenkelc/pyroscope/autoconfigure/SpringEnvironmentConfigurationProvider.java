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

import io.pyroscope.javaagent.api.ConfigurationProvider;
import io.pyroscope.javaagent.impl.DefaultConfigurationProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;

/**
 * A ConfigurationProvider implementation that retrieves configuration values from the Spring
 * Environment. It prefers the Pyroscope default configuration provider and falls back to the
 * Spring Environment if a property is not found.
 */
public class SpringEnvironmentConfigurationProvider implements ConfigurationProvider {
  private final Environment environment;
  private final DefaultConfigurationProvider defaultConfigurationProvider =
      new DefaultConfigurationProvider();

  /**
   * Constructs a new SpringEnvironmentConfigurationProvider with the given Spring Environment.
   *
   * @param environment the Spring Environment to use for property resolution
   */
  public SpringEnvironmentConfigurationProvider(Environment environment) {
    this.environment = environment;
  }

  /**
   * Retrieves the configuration value for the given key. First checks the default configuration
   * provider, and if not found, checks the Spring Environment.
   *
   * @param key the configuration key
   * @return the configuration value, or null if not found
   */
  @Override
  @Nullable
  public String get(String key) {
    String fromDefault = defaultConfigurationProvider.get(key);
    if (fromDefault == null) {
      return environment.getProperty(key);
    }
    return fromDefault;
  }
}
