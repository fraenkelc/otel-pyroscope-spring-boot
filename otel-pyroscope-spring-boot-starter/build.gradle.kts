plugins {
    `java-library`
}

group = "io.github.fraenkelc"

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(project(":otel-pyroscope-spring-boot"))
    implementation(libs.pyroscope.otel)
}