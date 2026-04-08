package io.github.fraenkelc.pyroscope.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
// @ExtendWith(SpringExtension.class)
class SpringBootOtelITest {

  public static final File COMPOSE_FILE = new File("../examples/docker-compose-no-ports.yml");

  public static final String APP_1 = "spring-opentelemetry-agent-example";
  public static final String APP_2 = "spring-opentelemetry-agent-classpath-example";
  public static final String APP_3 = "spring-opentelemetry-classpath-example";
  public static final String TEMPO = "tempo";
  public static final String PYROSCOPE = "pyroscope";
  public static final String REDPANDA = "redpanda";
  public static final int APP_PORT = 8080;
  public static final int TEMPO_PORT = 3200;
  public static final int PYROSCOPE_PORT = 4040;
  public static final int REDPANDA_PORT = 9644;

  public static final Logger LOGGER = LoggerFactory.getLogger(SpringBootOtelITest.class);
  public static final String PYROSCOPE_QUERY =
      """
          {
                "start": "%d",
                "end": "%d",
                "labelSelector": "{service_name=\\"%s\\"}",
                "profileTypeID": "process_cpu:cpu:nanoseconds:cpu:nanoseconds",
                "spanSelector": [
                  "%s"
                ]
              }
          """;
  public static final String TEMPO_QUERY =
      "{ resource.service.name=\"%s\" && span.http.url=\"/fibonacci\" } | select (span.pyroscope.profile.id)";

  @Container
  static ComposeContainer composeContainer =
      new ComposeContainer(DockerImageName.parse("docker"), COMPOSE_FILE)
              .withCopyFilesInContainer("../")
          .withExposedService(APP_1, APP_PORT, Wait.forHttp("/health"))
          .withExposedService(APP_2, APP_PORT, Wait.forHttp("/health"))
          .withExposedService(APP_3, APP_PORT, Wait.forHttp("/health"))
          .withExposedService(
              TEMPO, TEMPO_PORT, Wait.forHttp("/ready").withStartupTimeout(Duration.ofMinutes(2)))
          .withExposedService(PYROSCOPE, PYROSCOPE_PORT, Wait.forHttp("/ready"))
          .withExposedService(REDPANDA, REDPANDA_PORT, Wait.forHttp("/v1/status/ready"));

  @ParameterizedTest
  @ValueSource(strings = {APP_1, APP_2, APP_3})
  void verifyTraceToProfileLinking(String appName) {
    // GIVEN
    var start = Instant.now().minus(Duration.ofMinutes(1));

    // WHEN
    serviceClient(appName, APP_PORT)
        .get()
        .uri("/fibonacci?n=40")
        .exchangeSuccessfully()
        .expectBody(String.class)
        .isEqualTo("fibonacci(40) = 102334155");
    var end = Instant.now().plus(Duration.ofMinutes(1));

    // THEN
    String traceQl = TEMPO_QUERY.formatted(appName);
    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofSeconds(1))
        .logging(LOGGER::info)
        .untilAsserted(
            () ->
                serviceClient(TEMPO, TEMPO_PORT)
                    .get()
                    .uri(
                        u ->
                            u.path("/api/search")
                                .queryParam("q", "{q}")
                                .queryParam("limit", "1")
                                .queryParam("start", start.getEpochSecond())
                                .queryParam("end", end.getEpochSecond())
                                .build(traceQl))
                    .exchangeSuccessfully()
                    .expectBody()
                    .jsonPath("$.traces.length()")
                    .isEqualTo(1));
    var traces =
        serviceClient(TEMPO, TEMPO_PORT)
            .get()
            .uri(
                u ->
                    u.path("/api/search")
                        .queryParam("q", "{q}")
                        .queryParam("limit", "1")
                        .queryParam("start", start.getEpochSecond())
                        .queryParam("end", end.getEpochSecond())
                        .build(traceQl))
            .exchangeSuccessfully()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    List<String> profileIds =
        JsonPath.read(
            traces,
            "$.traces[0].spanSet.spans[0].attributes[?(@.key == 'pyroscope.profile.id')].value.stringValue");
    assertThat(profileIds).hasSize(1);

    // a corresponding profile exists and has more that the "total" frame
    serviceClient(PYROSCOPE, PYROSCOPE_PORT)
        .post()
        .uri("querier.v1.QuerierService/SelectMergeSpanProfile")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            PYROSCOPE_QUERY.formatted(
                start.toEpochMilli(), end.toEpochMilli(), appName, profileIds.get(0)))
        .exchangeSuccessfully()
        .expectBody()
        .jsonPath("$.flamegraph.names")
        .value(
            t ->
                assertThat(t)
                    .asInstanceOf(InstanceOfAssertFactories.list(String.class))
                    .contains(
                        "io/pyroscope/example/FibonacciService.compute",
                        "io/pyroscope/example/WorkController.fibonacci"));
  }

  private static @NonNull RestTestClient serviceClient(String serviceName, int servicePort) {
    String host = composeContainer.getServiceHost(serviceName, servicePort);
    Integer port = composeContainer.getServicePort(serviceName, servicePort);
    return RestTestClient.bindToServer().baseUrl("http://" + host + ":" + port).build();
  }
}
