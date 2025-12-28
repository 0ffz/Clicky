import me.dvyy.tailwind.GenerateTailwindCssTask
import me.dvyy.tailwind.InstallTailwindCssTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("me.dvyy.tailwind")
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        optIn.addAll("kotlin.uuid.ExperimentalUuidApi", "io.ktor.utils.io.ExperimentalKtorApi")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dependencies {
    // Dependencies
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.sqids)
    implementation(libs.shocky.icons)
    implementation(libs.qrcode)
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")


    // Ktor
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.sse)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlinx.html)
    implementation(libs.ktor.server.htmx)
    implementation(libs.ktor.htmx.html)
    implementation(libs.ktor.server.netty)
    implementation(libs.kotlinx.collections.immutable)
    implementation("io.ktor:ktor-server-caching-headers:3.3.2")

    // Tests
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

ktor {
    docker {
        jreVersion = JavaVersion.VERSION_25
        localImageName = "ghcr.io/0ffz/clicky"
    }
}

tasks {
    register<InstallTailwindCssTask>("installTailwind")
    register<GenerateTailwindCssTask>("tailwind") {
        input = file("src/main/resources/tailwind.css")
        output = file("src/main/resources/web/styles/style.css")
        watch = file("src/main/kotlin")
        dependsOn("installTailwind")
    }
    processResources { dependsOn("tailwind") }
}