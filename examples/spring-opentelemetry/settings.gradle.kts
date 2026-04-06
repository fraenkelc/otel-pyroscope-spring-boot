rootProject.name = "spring-opentelemetry-example"

val standalone = providers.gradleProperty("standalone").orNull?.toBoolean() ?: false

if (!standalone) {
    includeBuild("../../")
}
