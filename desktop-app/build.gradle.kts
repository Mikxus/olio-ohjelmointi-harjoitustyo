import org.gradle.api.tasks.JavaExec

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "26"
    modules = listOf("javafx.controls")
}

dependencies {
    implementation("io.github.mkpaz:atlantafx-base:2.1.0")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0") // icons for app
    implementation("org.kordamp.ikonli:ikonli-material2-pack:12.4.0") // material2 icon pack
}

application {
    mainClass.set("com.project.app.Main")
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}

tasks.named<JavaExec>("run") {
    notCompatibleWithConfigurationCache("JavaFX plugin accesses task extensions at execution time")
}