import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.hse.online"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

repositories {
	mavenCentral()
}

tasks.withType<BootJar> {
	archiveFileName.set("group-service.jar")
	manifest {
		attributes("Start-Class" to "ru.hse.online.GroupService.GroupServiceApplication")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
