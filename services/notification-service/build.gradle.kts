dependencies {
    // ADD THIS LINE: Core Spring Boot dependency
    implementation("org.springframework.boot:spring-boot-starter")

    // Spring Kafka for consuming messages
    implementation("org.springframework.kafka:spring-kafka")

    // For running tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

