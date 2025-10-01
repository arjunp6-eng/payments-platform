/*
 * This build file is specific to the Identity Service.
 * It inherits settings from the root build.gradle.kts file.
 */

dependencies {
    // Spring Boot Starter for building web applications, including RESTful APIs.
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Boot Starter for using Spring Data JPA with Hibernate.
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // The PostgreSQL database driver.
    runtimeOnly("org.postgresql:postgresql")

    // Required for using Kotlin with Spring Boot.
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-security")

    // Eureka Client for service discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // For producing messages to Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Spring Boot Starter for testing.
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}