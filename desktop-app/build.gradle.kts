import org.gradle.api.tasks.JavaExec

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("com.project.app.Main")
}

tasks.named<JavaExec>("run") {
    notCompatibleWithConfigurationCache("JavaFX plugin accesses task extensions at execution time")
}