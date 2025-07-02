plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
	jacoco
}

group = "com.pokedex"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

	// CSV Processing
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
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation(kotlin("test-junit5"))
	// testImplementation("io.mockk:mockk:1.13.9")
}

kotlin {
	jvmToolchain(21)
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
		fileTree("${buildDir}/classes/kotlin/main") {
			exclude(
				"**/com/pokedex/bff/PokedexBffApplication*",
				"**/com/pokedex/bff/application/dto/**",
				"**/com/pokedex/bff/domain/entities/**",
				"**/com/pokedex/bff/infrastructure/configuration/**",
			)
		}
	)

	executionData.setFrom(fileTree(project.buildDir) {
		include("jacoco/test.exec")
	})

	sourceSets(sourceSets.main.get())
}