plugins {
    kotlin("jvm") version "2.1.10"
}

group = "dev.spaghett"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.2.0.Final")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}