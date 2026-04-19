// Backend

plugins {
    application
    id("io.quarkus") version "3.35.0.CR1"
}

dependencies {
    implementation(project(":common"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.35.0.CR1"))
    implementation("com.auth0:java-jwt:4.5.1")
    implementation("io.quarkus:quarkus-rest")
}

application {
    mainClass.set("com.project.backend.Main")
}