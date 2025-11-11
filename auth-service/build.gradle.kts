plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("checkstyle")
}

group = "ru.practikum.masters"
version = "0.0.1-SNAPSHOT"
description = "auth-service"

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
}

// Конфигурация Checkstyle
checkstyle {
	toolVersion = "10.12.5"
	configFile = file("${rootDir}/checkstyle.xml")
    isIgnoreFailures = false
	maxWarnings = 0
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.postgresql:postgresql")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
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
	useJUnitPlatform()
	dependsOn("checkstyleMain") // Проверка кода перед тестами
}
