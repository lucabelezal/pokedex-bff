plugins {
	// Core plugins
	id("jacoco")

	// Spring
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"

	// Kotlin
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "com.pokedex"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
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
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// CSV
	implementation("org.apache.commons:commons-csv:1.10.0")

	// Database
	runtimeOnly("org.postgresql:postgresql")

	// OpenAPI (Swagger)
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	// Dev Tools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation(kotlin("test-junit5"))
	testImplementation("io.mockk:mockk:1.13.10")
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.test {
	useJUnitPlatform()
}

jacoco {
	toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
	}

	classDirectories.setFrom(
		fileTree(layout.buildDirectory.dir("classes/kotlin/main").get().asFile) {
			exclude(
				"**/com/pokedex/bff/PokedexBffApplication*",
				"**/com/pokedex/bff/application/dto/**",
				"**/com/pokedex/bff/domain/entities/**",
				"**/com/pokedex/bff/infrastructure/configuration/**",
			)
		}
	)

	executionData.setFrom(
		fileTree(layout.buildDirectory.get().asFile) {
			include("jacoco/test.exec")
		}
	)

	sourceSets(sourceSets.main.get())
}
