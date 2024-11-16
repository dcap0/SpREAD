plugins {
    id("java-library")
}

group = "org.ddmac"
version = "v0.0.13"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
