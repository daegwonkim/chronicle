import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.28.0"
    id("signing")
}

group = "io.github.daegwonkim.chronicle"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ch.qos.logback:logback-classic:1.5.26")

    testImplementation("ch.qos.logback:logback-classic:1.5.26")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation("org.awaitility:awaitility:4.3.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

signing {
    sign(publishing.publications)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.daegwonkim",
        artifactId = "chronicle",
        version = "0.1.0"
    )

    pom {
        name = "Chronicle"
        description = "A lightweight Logback appender SDK for asynchronously delivering logs to a centralized server."
        inceptionYear = "2026"
        url = "<https://github.com/daegwonkim/chronicle>"

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("daegwonkim")
                name.set("daegwonkim")
                url.set("https://github.com/daegwonkim")
            }
        }

        scm {
            connection.set("scm:git:github.com/daegwonkim/chronicle.git")
            developerConnection.set("scm:git:ssh://github.com:daegwonkim/chronicle.git")
            url.set("https://github.com/daegwonkim/chronicle")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}

