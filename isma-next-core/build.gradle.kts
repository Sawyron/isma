plugins {
    kotlin("jvm")
    id("de.jjohannes.extra-java-module-info")
}

group = "ru.nstu.isma.next"
version = "1.0.0"

val moduleName by extra("isma.isma.next.core.main")

dependencies {
    implementation(project(":isma-next-core-simulation-gen"))
    implementation(project(":isma-hsm"))
    implementation(project(":isma-lisma"))
    implementation(project(":isma-next-tools"))
    implementation(project(":isma-intg-api"))
    implementation(project(":isma-intg-core"))
    implementation(project(":isma-intg-server:isma-intg-server-client"))
    implementation(project(":isma-intg-lib:isma-intg-lib-common"))
    implementation(project(":isma-intg-lib:isma-intg-lib-euler"))

    testImplementation (project(":isma-intg-lib:isma-intg-lib-common"))

    implementation ("org.apache.commons:commons-lang3:3.12.0")
    implementation ("org.apache.commons:commons-text:1.9")
    implementation ("org.slf4j:slf4j-api:1.7.36")
    implementation ("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${PackageVersion.kotlinxCoroutines}")

    implementation ("io.insert-koin:koin-core:${PackageVersion.koin}")

    testImplementation ("junit:junit:4.13.2")
    testImplementation ("com.tngtech.java:junit-dataprovider:1.13.1")
    testImplementation ("com.github.jbellis:jamm:0.3.3")
}

tasks {
    compileJava {
        inputs.property("moduleName", moduleName)
        options.compilerArgs = listOf(
            "--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}"
        )
    }
    //check { dependsOn(integTestTask) }
}

extraJavaModuleInfo {
    automaticModule("kotlinx-coroutines-core-jvm-${PackageVersion.kotlinxCoroutines}.jar", "kotlinx.coroutines.core.jvm")
    failOnMissingModuleInfo.set(false)
}