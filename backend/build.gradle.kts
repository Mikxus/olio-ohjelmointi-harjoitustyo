// Backend

plugins {
    application
}

dependencies {
    implementation("com.auth0:java-jwt:4.5.1")
}

application {
    mainClass.set("com.project.backend.Main")
}