// Backend

plugins {
    application
    id("io.quarkus") version "3.35.0.CR1"
}

dependencies {
    implementation(project(":common"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.35.0.CR1"))
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
}

application {
    mainClass.set("com.project.backend.Main")
}