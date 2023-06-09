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
    val jupiterVersion: String by project
    val postgresVersion: String by project
    val vertxVersion: String by project

    // vertx
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    // DB
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("io.vertx:vertx-pg-client:$vertxVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("io.vertx:vertx-web-client:$vertxVersion")
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