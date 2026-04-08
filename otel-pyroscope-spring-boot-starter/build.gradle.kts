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
    isCanBeResolved = false
    isCanBeConsumed = false
}

val agentJar by configurations.creating {
    description = "Pyroscope agent jar"
    isCanBeResolved = true
    isCanBeConsumed = false
}
val libWithDepsForExample by configurations.creating {
    description = "This library including dependencies for use in examples"
    isCanBeResolved = true
    isCanBeConsumed = false
}
val testDependencies = configurations.consumable("testDependencies") {
    description = "Test dependencies for use in integration tests"
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

    api(libs.spring.boot.starter)
    api(libs.spring.boot.micrometer.tracing.opentelemetry)
    api(libs.pyroscope.otel) {
        // exclude here to allow consumers the choice of how the agent is applied.
        exclude("io.pyroscope", "agent")
    }

    compileOnly(libs.opentelemetry.api)
    compileOnly(libs.pyroscope.agent)

    implementation(libs.spring.context)

    agentJar(platform(libs.spring.boot.dependencies))
    agentJar(libs.pyroscope.agent)
    libWithDepsForExample(platform(libs.spring.boot.dependencies))
    libWithDepsForExample(dependencyFactory.create(project)) {
        exclude("org.springframework")
        exclude("org.springframework.boot")
    }
}

tasks.jar {
    manifest {
        attributes(
            "Spring-Boot-Jar-Type" to "dependencies-starter"
        )
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name = "otel-pyroscope-spring-boot-starter"
                description = "Spring Boot autoconfiguration for otel-pyroscope"
                url = "https://github.com/fraenkelc/otel-pyroscope-spring-boot"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "fraenkelc"
                        name = "Christian Fränkel"
                    }
                }
                scm {
                    url = "https://github.com/fraenkelc/otel-pyroscope-spring-boot.git"
                }
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

@Suppress("UnstableApiUsage")
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(libs.spring.boot.test)
                implementation(libs.assertj.core)
                implementation(libs.pyroscope.otel)
            }
        }
    }
}

val copyArtifactsForExamples by tasks.registering(Sync::class) {
    group = "build"
    description = "Copies artifacts for use in the examples docker build"
    destinationDir = layout.buildDirectory.dir("examples").get().asFile

    into("agent") {
        from(agentJar)
        rename { "pyroscope-agent.jar" }
    }
    into("lib") {
        from(libWithDepsForExample)
    }
}

artifacts.add("testDependencies", copyArtifactsForExamples.map { it.destinationDir })