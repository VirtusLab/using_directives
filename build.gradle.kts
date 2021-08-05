plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.google.code.gson:gson:2.8.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

sourceSets {
    main {
        java.srcDir("src/main/java")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}