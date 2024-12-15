plugins {
    `java-library`
    `jvm-test-suite`
}

group = "io.github.fraenkelc"

java {
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    api(platform(libs.spring.boot.dependencies))
    api(libs.spring.boot.actuator.autoconfigure)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.boot)

    compileOnly(platform(libs.spring.boot.dependencies))
    compileOnly(libs.opentelemetry.sdk.trace)
    compileOnly(libs.opentelemetry.api)
    compileOnly(libs.pyroscope.otel)

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.context)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(libs.spring.boot.test)
                implementation(libs.assertj.core)
                implementation(libs.opentelemetry.sdk.trace)
                implementation(libs.opentelemetry.api)
                implementation(libs.pyroscope.otel)
            }
        }
    }
}
