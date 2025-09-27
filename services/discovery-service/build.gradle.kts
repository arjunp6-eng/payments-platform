dependencies {
    // Eureka Server for service discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")

    // For running tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}