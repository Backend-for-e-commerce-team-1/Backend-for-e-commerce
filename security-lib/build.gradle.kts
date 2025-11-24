plugins {
    id("java")
    id("java-library")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
}

group = "ru.practikum.masters.securitylib"
version = "0.0.1-SNAPSHOT"
description = "security-lib"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

//test {
//    useJUnitPlatform()
//}
//
//bootJar {
//    enabled = false
//}

//jar {
//    enabled = true
//    archiveBaseName("security-lib-starter")
//}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = false
    archiveBaseName = "security-lib-starter"
}


//tasks.getByName<Jar>("jar") {
//    enabled = false
//    archiveBaseName = "security-lib-starter"
//}

//
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
//
tasks.register("copyJarToLibs", Copy::class) {
    from(project.layout.projectDirectory.dir("jar"))
    into("${rootProject.projectDir}/libs")
    dependsOn()
}

tasks["build"].dependsOn("copyJarToLibs")

        //jar.finalizedBy copyJarToLibs
tasks["jar"].finalizedBy("copyJarToLibs")
tasks.register("installToLocalRepo") {
    dependsOn("publishToMavenLocal")
}
//
tasks.withType(GenerateModuleMetadata::class) {
    suppressedValidationErrors.add("dependencies-without-versions")
}
