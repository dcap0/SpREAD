plugins {
    id("java-library")
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "org.ddmac"
version = "v0.1.1"

repositories{
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}