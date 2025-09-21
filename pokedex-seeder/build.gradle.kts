plugins {
    kotlin("jvm") version "1.9.0"
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}

dependencies {
    implementation(project(":pokedex-bff")) // depende do domínio e infraestrutura
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.slf4j:slf4j-api:2.0.7")
    // outras dependências necessárias para o seeder
}

application {
    mainClass.set("com.pokedex.seeder.SeederApplicationKt")
}

