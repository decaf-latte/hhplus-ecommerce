plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// SpringDoc OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

	// DB
	runtimeOnly("com.mysql:mysql-connector-j")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	 testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testImplementation("com.squareup.okhttp3:mockwebserver:3.4.0")
	testImplementation("org.testcontainers:kafka:1.19.4") // 최신 버전 적용
	testImplementation("org.springframework.kafka:spring-kafka-test")

	// Lombok
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")

	// Feign Client
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-cache")

	// Kafka
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("junit.platform.configuration.parameter.allowMultipleJunitPlatformPropertiesFiles", "false") // 중복 설정 무시
	systemProperty("user.timezone", "UTC")
}

tasks.named<Copy>("processTestResources") {
	exclude("junit-platform.properties")
}

tasks.named<JavaCompile>("compileJava") {
	options.annotationProcessorPath = configurations.annotationProcessor.get()
}

sourceSets {
	named("main") {
		java {
			srcDir("build/generated/querydsl")
		}
	}
}
