rootProject.name = "otel-pyroscope-spring-boot-parent"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
include(":otel-pyroscope-spring-boot", ":otel-pyroscope-spring-boot-starter")