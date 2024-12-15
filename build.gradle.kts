plugins {
    `maven-publish`
}
subprojects {
    apply<MavenPublishPlugin>()
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                pom {
                    name = "otel-pyroscope-spring-boot"
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
            }
        }
    }
}
