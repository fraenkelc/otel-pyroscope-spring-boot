## Spring OpenTelemetry tracing integration

Pyroscope can integrate with distributed tracing systems supporting [**OpenTelemetry**](https://opentelemetry.io/docs/instrumentation/java/getting-started/) standard which allows you to
link traces with the profiling data, and find specific lines of code related to a performance issue.

This project is uses the [Java OpenTelemetry tracing integration](https://github.com/grafana/otel-profiling-java) 
project and adds similar integration for a spring micrometer tracing + otel bridge setup.

```kotlin
dependencies {
    // ...
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    
    implementation("io.github.fraenkelc:otel-pyroscope-spring-boot-starter:0.1.0")
}
```

The project adds the following spring configuration properties:
```properties
# Whether to start PyroscopeAgent automatically.
otel.pyroscope.start-profiling=true
# Whether to link the root span only.
otel.pyroscope.root-span-only=true
# Whether to add the span name as a label.
otel.pyroscope.add-span-name=false
```
