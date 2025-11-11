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
}

// Конфигурация Checkstyle
checkstyle {
    toolVersion = "10.12.5"
    configFile = file("${rootDir}/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
    dependsOn("checkstyleMain") // Проверка кода перед тестами
}
