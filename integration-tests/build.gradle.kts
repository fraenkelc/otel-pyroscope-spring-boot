plugins {
    java
    `jvm-test-suite`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val testDependencies by configurations.creating {
    description = "Test dependencies for use in integration tests"
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    testDependencies(project(":otel-pyroscope-spring-boot-starter", "testDependencies"))
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(platform(libs.spring.boot.dependencies))
                implementation(libs.assertj.core)
                implementation(libs.spring.boot.resttestclient)
                implementation(libs.testcontainers)
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.json.path)
                implementation(libs.awaitility)
                runtimeOnly(libs.logback.classic)
            }
            targets.all {
                testTask {
                    // the docker-compose build consumes the test dependencies
                    dependsOn(testDependencies)
                    // it also uses the following files in the copy action
                    inputs.files(fileTree(rootProject.projectDir) {
                        include("gradle.properties")
                        include("settings.gradle.kts")
                        include("build.gradle.kts")
                        include("gradle/libs.versions.toml")
                        include("otel-pyroscope-spring-boot-starter/build.gradle.kts")
                        include("otel-pyroscope-spring-boot-starter/src/**")
                        include("examples/spring-opentelemetry/build.gradle.kts")
                        include("examples/spring-opentelemetry/settings.gradle.kts")
                        include("examples/spring-opentelemetry/gradle/libs.versions.toml")
                        include("examples/spring-opentelemetry/src/**")
                    })
                }
            }
        }
    }
}
