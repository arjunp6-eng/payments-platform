dependencies {
    // Spring Cloud Gateway for routing
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    // Eureka Client for service discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // For running tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
