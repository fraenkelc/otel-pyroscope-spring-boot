## Spring OpenTelemetry tracing integration

Pyroscope can integrate with distributed tracing systems supporting [**OpenTelemetry**](https://opentelemetry.io/docs/instrumentation/java/getting-started/) standard which allows you to
link traces with the profiling data, and find specific lines of code related to a performance issue.

This project is uses the [Java OpenTelemetry tracing integration](https://github.com/grafana/otel-profiling-java) 
project and adds similar integration for a `spring-boot-starter-opentelemetry` setup.

```kotlin
dependencies {
    // ...
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    runtimeOnly("io.github.fraenkelc:otel-pyroscope-spring-boot-starter:0.1.0")
    // also add the pyroscope agent if it is not started as `-javaagent`
    runtimeOnly("io.pyroscope:agent:2.5.1")
}
```

The project adds the following spring configuration properties:
```properties
# Whether to start PyroscopeAgent automatically.
otel.pyroscope.start-profiling=true
```
