plugins {
    id("java-library")
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "org.ddmac"
version = "v0.1.1"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
