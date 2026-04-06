# otel-pyroscope-spring Examples

These examples demonstrate different ways to integrate Pyroscope profiling with OpenTelemetry in
a [Spring Boot](https://spring.io/projects/spring-boot) application.

Each example runs a simple Spring Boot app that exposes a `/fibonacci?n=40` endpoint performing intentionally
CPU-intensive recursive work, making profiling data easy to observe.

For the original upstream examples and additional context
see [examples/README.md in the grafana/otel-profiling-java repo](https://github.com/grafana/otel-profiling-java/blob/bb3e3d858ee2ec8c67845af9b336588e770662ef/examples/README.md)

## Examples

The `spring-opentelemetry` project is a Spring Boot example that demonstrates using the `otel-pyroscope-spring`
auto-configuration/starter to wire Pyroscope profiling into an OpenTelemetry-enabled application. It is used in all
example configurations.

### spring-opentelemetry-agent

Uses the **Pyroscope agent** and `otel-pyroscope-spring-boot-starter` as runtime library dependencies:

- The app itself has **zero Pyroscope or OTel code** — it's plain Spring Boot
- Spring Boot auto-instruments HTTP requests and creates spans automatically
- The `pyroscope-agent.jar` starts profiling on startup as a java agent
- The `otel-pyroscope-spring-boot-starter` Auto-Configuration registers itself and wires `PyroscopeOtelSpanProcessor` to link
  profiling data to trace spans
- Configured entirely via environment variables or (optionally) spring properties

JVM startup looks like:

```
java -javaagent:pyroscope-agent.jar \
     -jar app.jar
```

### spring-opentelemetry-classpath

Uses the **Pyroscope agent** and `otel-pyroscope-spring-boot-starter` as runtime library dependencies:

- The app itself has **zero Pyroscope or OTel code** — it's plain Spring Boot
- Spring Boot auto-instruments HTTP requests and creates spans automatically
- The `otel-pyroscope-spring-boot-starter` Auto-Configuration registers itself, auto-starts the Pyroscope profiler and wires
  `PyroscopeOtelSpanProcessor` to link profiling data to trace spans
- Configured entirely via environment variables or (optionally) spring properties

JVM startup looks like:

```
java -jar app.jar
```

### spring-opentelemetry-classpath

Combination of both `spring-opentelemetry-classpath` and `spring-opentelemetry-agent`. The `pyroscope-agent.jar` is
present as both `-javaagent` and via classpath.

This example demonstrates that including the agent in both locations does not cause any issues.

## Prerequisites

- Docker and Docker Compose

## Running

From this repository's `examples/` directory you can run the whole example stack (Pyroscope + Grafana + example
services) using the provided compose file.

1. Start services with Docker Compose:
   ```bash
    cd examples
    docker-compose up --build
    ```

   This starts:
    - **Pyroscope** at `http://localhost:4040`
    - **Grafana** at `http://localhost:3000`
    - **Tempo** at `http://localhost:3200`
    - **Redpanda Console** at `http://localhost:8070`
    - **spring-opentelemetry-agent-example** at `http://localhost:8080`
    - **spring-opentelemetry-agent-classpath-example** at `http://localhost:8081`
    - **spring-opentelemetry-classpath-example** at `http://localhost:8082`
2. Generate load to produce profiling data:
    ```bash
    # from the examples/ directory
    ./loadgen.sh
    ```
3. Open the Grafana UI at `http://localhost:3000` and select "Drilldown -> Traces" to view traces and related
   flamegraps.

## Configuration

| Environment Variable         | Default                 | Description                            |
|------------------------------|-------------------------|----------------------------------------|
| `PYROSCOPE_SERVER_ADDRESS`   | `http://localhost:4040` | Pyroscope server URL                   |
| `PYROSCOPE_APPLICATION_NAME` | example-specific        | Application name shown in Pyroscope UI |

