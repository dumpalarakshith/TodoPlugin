plugins {
    id("org.jetbrains.intellij") version "1.17.0"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("java")
}

group = "com.dumpalarakshith.todo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.2.6")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks.patchPluginXml {
    sinceBuild.set("232")
    untilBuild.set("242.*")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
}

tasks.test {
    useJUnitPlatform()
}