package io.pyroscope.example;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkController {

  private final FibonacciService fibonacciService;
  private final CpuBurner cpuBurner;
  private final ObservationRegistry observationRegistry;

  public WorkController(
      FibonacciService fibonacciService,
      CpuBurner cpuBurner,
      ObservationRegistry observationRegistry) {
    this.fibonacciService = fibonacciService;
    this.cpuBurner = cpuBurner;
    this.observationRegistry = observationRegistry;
  }

  /**
   * Computes Fibonacci(n) recursively. Spring Boot automatically creates a span for this HTTP
   * request, which the pyroscope-otel extension uses to annotate the profiling data with the span
   * ID.
   */
  @GetMapping("/fibonacci")
  public String fibonacci(@RequestParam(defaultValue = "40") int n) {
    long result = fibonacciService.compute(n);
    return "fibonacci(" + n + ") = " + result;
  }

  @GetMapping("/child-spans")
  public String childSpans() {
    return Observation.createNotStarted("child-spans", observationRegistry)
        .observe(
            () -> {
              Observation.createNotStarted("child1", observationRegistry).observe(this::burnChild1);
              Observation.createNotStarted("child2", observationRegistry).observe(this::burnChild2);
              return "child-spans";
            });
  }

  private void burnChild1() {
    cpuBurner.burnFor(2_000);
  }

  private void burnChild2() {
    cpuBurner.burnFor(4_000);
  }

  @GetMapping("/health")
  public String health() {
    return "OK";
  }
}
