plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jooq.jooq-codegen-gradle") version "3.20.1"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("jvm") version "2.1.21"
}

group = "com.prewave.nodemanager"
version = "1.0-SNAPSHOT"

val jooqVersion by extra("3.20.1")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.liquibase:liquibase-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    api("org.jooq:jooq:${jooqVersion}")

    jooqCodegen("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    jooqCodegen("org.jooq:jooq-meta:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-meta-extensions:${jooqVersion}")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
}

sourceSets {
    val main by getting {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
}

jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/migration/V1__initial_schema.sql"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "as-is"
                    }
                }
            }
            target {
                directory = "build/generated-sources/jooq"
                packageName = "com.prewave.nodemanager.model"
            }
            kotlin {
            }
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}