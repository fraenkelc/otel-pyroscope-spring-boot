plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.fraenkelc"

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

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    internal(platform(libs.spring.boot.dependencies))
    implementation(project(":otel-pyroscope-spring-boot"))
    implementation(libs.pyroscope.otel)
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
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        named<MavenPublication>("mavenJava") {
            pom {
                name = "otel-pyroscope-spring-boot-starter"
            }
            from(components["java"])
        }
    }
}
