import org.gradle.api.publish.PublishingExtension
import java.io.ByteArrayOutputStream

plugins {
    java
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

apply {
    from("${rootDir}/scripts/publish-root.gradle")
}

group = "org.virtuslab"
version = getAppVersion()

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

fun getAppVersion(): String {
    var stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    val commitId = stdout.toString().replace("\n", "").replace("\r", "").trim()
    stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "tag", "--points-at", commitId)
        standardOutput = stdout
    }
    val releaseTagName = stdout.toString()
            .replace("\n", "")
            .replace("\r", "")
            .trim()
    stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--abbrev=0")
        standardOutput = stdout
    }
    val snapshotTagName = stdout.toString()
            .replace("\n", "")
            .replace("\r", "")
            .trim()
    var versionName = "git-$commitId-SNAPSHOT"
    if ("" != releaseTagName) {
        versionName = releaseTagName.drop(1)
    }
    else if ("" != snapshotTagName) {
        versionName = "${snapshotTagName.drop(1)}-$commitId-SNAPSHOT"
    }
    return versionName
}


tasks {
   val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }

    create("printVersion") {
        println(getAppVersion())
    }

}



tasks.getByName<Test>("test") {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

/* Release */

val projectExt = ext

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(projectExt["sonatypeStagingProfileId"] as String)
            username.set(projectExt["ossrhUsername"] as String)
            password.set(projectExt["ossrhPassword"] as String)
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                artifact(tasks.get("jar") as Jar)
                artifact(tasks.get("javadocJar") as Jar)
                artifact(tasks.get("sourcesJar") as Jar)

                pom {
                    name.set(project.name)
                    description.set("Library for extracting meta-information written in using-directives syntax")
                    url.set("https://github.com/VirtuslabRnD/using_directives")
                    developers {
                        developer {
                            id.set("pikinier20")
                            name.set("Filip Zybała")
                            email.set("filip.zybala@gmail.com")
                        }
                        developer {
                            id.set("KacperFKorban")
                            name.set("Kacper Korban")
                            email.set("kkorban@virtuslab.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:github.com/VirtuslabRnD/using_directives.git")
                        developerConnection.set("scm:git:ssh://github.com/VirtuslabRnD/using_directives.git")
                        url.set("https://github.com/VirtuslabRnD/using_directives/tree/main")
                    }

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
            rootProject.ext["signing.keyId"] as String,
            rootProject.ext["signing.key"] as String,
            rootProject.ext["signing.password"] as String
    )
    sign(publishing.publications)
}

