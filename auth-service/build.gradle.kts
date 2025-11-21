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
	mavenLocal()
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator") // ← перенесено сюда
	implementation("org.postgresql:postgresql")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("io.opentelemetry:opentelemetry-api")
	implementation("io.opentelemetry:opentelemetry-sdk")
	implementation("io.opentelemetry:opentelemetry-exporter-logging")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.micrometer:micrometer-observation")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

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
}
