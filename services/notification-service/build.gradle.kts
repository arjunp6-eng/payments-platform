dependencies {
    // ADD THIS LINE: Core Spring Boot dependency
    implementation("org.springframework.boot:spring-boot-starter")

    // Spring Kafka for consuming messages
    implementation("org.springframework.kafka:spring-kafka")

    // ADD THIS LINE: Provides the necessary Jackson JSON libraries
    implementation("org.springframework.boot:spring-boot-starter-json")

    // Dependency on our shared events library
    implementation(project(":services:event-dtos"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}