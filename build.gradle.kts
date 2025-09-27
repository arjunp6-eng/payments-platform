import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    // Make the Spring Boot plugin available to sub-projects
    id("org.springframework.boot") version "3.3.0" apply false
    // Make the Spring Dependency Management plugin available to sub-projects
    id("io.spring.dependency-management") version "1.1.5" apply false
    // Make the Kotlin for JVM plugin available to sub-projects
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    // Make the Kotlin JPA plugin available to sub-projects
    id("org.jetbrains.kotlin.plugin.jpa") version "2.0.0" apply false
    // Make the All-Open plugin available
    id("org.jetbrains.kotlin.plugin.allopen") version "2.0.0" apply false
}

// This block configures settings for all our microservice sub-projects
subprojects {
    // Apply the necessary plugins to each microservice
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

    // Set a standard group and version for all services
    group = "com.payments.platform"
    version = "0.0.1-SNAPSHOT"

    // Define where to download dependencies from. This fixes the "Could not resolve" error.
    repositories {
        mavenCentral()
    }

    // Configure Java compatibility for all services
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    // Configure Kotlin options for all services
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // Centrally manage dependency versions using Spring's Bill of Materials (BOM)
    configure<DependencyManagementExtension> {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.2")
        }
    }

    // Configure the all-open plugin for any subproject it's applied to.
    configure<AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("jakarta.persistence.Embeddable")
    }
}

