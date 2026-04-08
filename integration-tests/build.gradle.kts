plugins {
    java
    `jvm-test-suite`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
        }
    }
}
