plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.github.sor2171"
version = "1.0.2"
description = "backend"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configurations {
    val compileOnly by configurations.getting
    val annotationProcessor by configurations.getting
    compileOnly.extendsFrom(annotationProcessor)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.14")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.32")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
