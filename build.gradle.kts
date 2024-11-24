plugins {
    id("java-library")
}

group = "org.ddmac"
version = "v0.5.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
// https://mvnrepository.com/artifact/io.toolisticon.cute/cute
    implementation("io.toolisticon.cute:cute:1.7.0")


}

tasks.test {
    useJUnitPlatform()
}