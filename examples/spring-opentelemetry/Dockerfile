FROM gradle:9.4.1-jdk17@sha256:c8b2b08dec24604c29809b8ce785b689231409490dde95a0b5340f9c6a04970a AS lib-builder
WORKDIR /build
COPY gradle.properties ./gradle.properties
COPY settings.gradle.kts ./settings.gradle.kts
COPY build.gradle.kts ./build.gradle.kts
COPY gradle/libs.versions.toml ./gradle/libs.versions.toml
COPY otel-pyroscope-spring-boot-starter/build.gradle.kts ./otel-pyroscope-spring-boot-starter/build.gradle.kts
RUN mkdir -p ./integration-tests
RUN gradle dependencies --no-daemon -q
COPY otel-pyroscope-spring-boot-starter/src/ ./otel-pyroscope-spring-boot-starter/src/
RUN gradle jar copyArtifactsForExamples --no-daemon -q

FROM gradle:9.4.1-jdk17@sha256:c8b2b08dec24604c29809b8ce785b689231409490dde95a0b5340f9c6a04970a AS builder
WORKDIR /build
COPY examples/spring-opentelemetry/build.gradle.kts ./build.gradle.kts
COPY examples/spring-opentelemetry/settings.gradle.kts ./settings.gradle.kts
COPY examples/spring-opentelemetry/gradle/libs.versions.toml ./gradle/libs.versions.toml
COPY gradle.properties ./gradle.properties
ENV ORG_GRADLE_PROJECT_standalone=true
RUN gradle dependencies --no-daemon -q
COPY examples/spring-opentelemetry/src ./src
RUN gradle bootJar --no-daemon -q
RUN java -Djarmode=tools -jar /build/build/libs/app.jar extract --launcher --layers --destination extracted

FROM eclipse-temurin:17-jre@sha256:fe793c7855f1c6317274ad46ccad761ba82dc3086c92a860c6b9843cc2ba590b
WORKDIR /app
ENV LOADER_PATH=libs
COPY --from=lib-builder /build/otel-pyroscope-spring-boot-starter/build/examples/agent/pyroscope-agent.jar /opt/pyroscope-agent.jar
COPY --from=builder /build/extracted/dependencies/ ./
COPY --from=builder /build/extracted/spring-boot-loader/ ./
COPY --from=builder /build/extracted/snapshot-dependencies/ ./
COPY --from=lib-builder /build/otel-pyroscope-spring-boot-starter/build/examples/lib/* ./libs/
COPY --from=builder /build/extracted/application/ ./
EXPOSE 8080
CMD ["java", "-Dpyroscope.otel.debug=true", "org.springframework.boot.loader.launch.PropertiesLauncher"]
