plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.basix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val vertxVersion: String by project

    // vertx
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("ServerKT")
}