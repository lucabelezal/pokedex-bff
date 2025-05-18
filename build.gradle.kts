plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "com.pokedex"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// CSV Processing
	implementation("org.apache.commons:commons-csv:1.10.0")

	// Database
	runtimeOnly("org.postgresql:postgresql")

	// Dev Tools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.mockk:mockk:1.13.9")
}

kotlin {
	jvmToolchain(17)
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

// Configuração para entidades JPA

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
