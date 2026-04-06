plugins {
    java
    id("org.springframework.boot") version "4.0.5"
}

group = "io.pyroscope.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val standalone = providers.gradleProperty("standalone").orNull?.toBoolean() ?: false

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.opentelemetry)
    implementation(libs.spring.boot.starter.web)
    if (!standalone) {
        runtimeOnly(libs.otel.pyroscope.spring.boot.starter)
    }
}

tasks.bootJar {
    archiveFileName = "app.jar"
    manifest {
        attributes("Main-Class" to "org.springframework.boot.loader.launch.PropertiesLauncher")
    }
    layered.enabled = true
}

