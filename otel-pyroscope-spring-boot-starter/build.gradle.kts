plugins {
    `java-library`
}

group = "io.github.fraenkelc"
version = project.properties["otelProfilingVersion"] ?: "1.0.0-SNAPSHOT"

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(project(":otel-pyroscope-spring-boot"))
    implementation(libs.pyroscope.otel)
}