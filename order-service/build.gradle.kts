plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
}

group = "ru.practikum.masters"
version = "0.0.1-SNAPSHOT"
description = "order-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.named("compileJava") {
    dependsOn(":global-exceptions:publishToMavenLocal")
}

// Конфигурация Checkstyle
checkstyle {
    toolVersion = "10.12.5"
    configFile = file("${rootDir}/checkstyle.xml")
    isIgnoreFailures = true
    maxWarnings = 0
}

dependencies {
    implementation("ru.practicum.masters.exceptions:global-exceptions:0.0.1-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-observation")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")
}

// Настройка отчетов Checkstyle
tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
        html.outputLocation.set(file("${buildDir}/reports/checkstyle/${name}.html"))
    }
}

// Таск для проверки
tasks.register("checkstyleAll") {
    group = "verification"
    description = "Run checkstyle for all source sets in this module"
    dependsOn(tasks.withType<Checkstyle>())
}

tasks.withType<Test> {
    useJUnitPlatform()
}
