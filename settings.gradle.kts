import org.ajoberstar.reckon.core.CommitMessageScopeParser

rootProject.name = "otel-pyroscope-spring-boot-parent"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.ajoberstar.reckon.settings") version "0.19.1"
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("beta", "rc", "final")
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
}

include(":otel-pyroscope-spring-boot", ":otel-pyroscope-spring-boot-starter")