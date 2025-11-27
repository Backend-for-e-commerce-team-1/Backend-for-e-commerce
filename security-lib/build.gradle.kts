plugins {
    java
    id("java-library")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
}

group = "ru.practicum.masters"
version = "0.0.1-SNAPSHOT"
description = "security-lib"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation ("org.springframework:spring-context")
    implementation ("org.slf4j:slf4j-api")
    implementation ("com.fasterxml.jackson.core:jackson-annotations")
    implementation ("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation ("ch.qos.logback:logback-classic")
    testImplementation ("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation ("org.assertj:assertj-core:3.24.2")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation ("org.springframework:spring-jdbc")
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation ("org.mockito:mockito-core")
    testImplementation ("org.mockito:mockito-junit-jupiter")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = false
    archiveBaseName = "security-lib-starter"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "ru.practicum.masters.securitylib"
            artifactId = "security-lib"
            version = "0.0.1-SNAPSHOT"
        }
    }
}

tasks.register("copyJarToLibs", Copy::class) {
    from("${rootProject.projectDir}/jar")
    into("${rootProject.projectDir}/libs")
    dependsOn()
}

tasks["build"].dependsOn("copyJarToLibs")

tasks["jar"].finalizedBy("copyJarToLibs")


tasks.register("installToLocalRepo") {
    dependsOn("publishToMavenLocal")
}

tasks.withType(GenerateModuleMetadata::class) {
    suppressedValidationErrors.add("dependencies-without-versions")
}
