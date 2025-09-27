/*
 * This is the root build file for the entire monorepo.
 * It configures settings, plugins, and dependency versions that will be
 * shared across all microservices.
 */
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Spring Boot plugin to manage Spring Boot dependencies and build executable jars.
    id("org.springframework.boot") version "3.3.0" apply false

    // Apply the Spring Dependency Management plugin to import Spring's BOM (Bill of Materials).
    id("io.spring.dependency-management") version "1.1.5"

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
}

// Configure all subprojects (our microservices) with common settings.
subprojects {
    // Apply necessary plugins for each microservice.
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    // Explicitly apply the java-library plugin to make the 'java' extension available.
    apply(plugin = "java-library")

    // Set group and version for all microservices.
    group = "com.payments.platform"
    version = "1.0.0-SNAPSHOT"

    // Set the Java version. Java 21 is the latest Long-Term Support (LTS) version
    // and is fully compatible with Spring Boot 3.3.
    // This type-safe approach resolves the 'unresolved reference' errors.
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    // Configure Kotlin options for all microservices using the modern compilerOptions DSL.
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // Define the repositories where dependencies will be fetched from.
    repositories {
        mavenCentral()
    }

    // This block is the Gradle equivalent of <dependencyManagement> in Maven.
    // It centrally manages the versions of our dependencies.
    dependencyManagement {
        imports {
            // Import the Spring Boot Bill of Materials (BOM)
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)

            // Import the Spring Cloud Bill of Materials (BOM)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.2")
        }
    }
}

