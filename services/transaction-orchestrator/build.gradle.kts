dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    // For consuming Kafka messages
    implementation("org.springframework.kafka:spring-kafka")
    // For making non-blocking HTTP requests with WebClient
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // For @Retryable annotation and AOP
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation(project(":services:event-dtos"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

