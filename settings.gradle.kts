rootProject.name = "otel-pyroscope-spring-boot-parent"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.ajoberstar.reckon.settings") version "2.0.0"
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("beta", "rc", "final")
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
}

include(":otel-pyroscope-spring-boot-starter", "integration-tests")