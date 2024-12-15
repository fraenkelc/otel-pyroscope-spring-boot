plugins {
    `java-library`
    `jvm-test-suite`
    `maven-publish`
}

group = "io.github.fraenkelc"

java {
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withJavadocJar()
    withSourcesJar()
}

// https://github.com/gradle/gradle/issues/10861
val internal by configurations.creating {
    description = "Internal configuration used to hide platform dependencies from generated poms"
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = false
}

listOf(
    "compileClasspath",
    "runtimeClasspath",
    "testCompileClasspath",
    "testRuntimeClasspath"
).forEach { configurations.named(it) { extendsFrom(internal) } }

dependencies {
    internal(platform(libs.spring.boot.dependencies))

    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    api(libs.spring.boot.actuator.autoconfigure)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.boot)

    compileOnly(libs.opentelemetry.sdk.trace)
    compileOnly(libs.opentelemetry.api)
    compileOnly(libs.pyroscope.otel)

    implementation(libs.spring.context)
}

publishing {
    publications {
        named<MavenPublication>("mavenJava") {
            pom {
                name = "otel-pyroscope-spring-boot"
            }
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
            addBooleanOption("Xdoclint:none", true)
        }
    }

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
