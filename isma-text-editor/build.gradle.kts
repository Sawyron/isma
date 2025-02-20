plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.java.modules)
    alias(libs.plugins.javafx)
}

val moduleName by extra("isma.isma.text.editor.main")

javafx {
    version = "21.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":isma-next-common-services"))
    implementation(project(":isma-lisma"))

    implementation(libs.fxmisc.richtext.core)
    implementation(libs.antlr4.runtime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.javafx)
}