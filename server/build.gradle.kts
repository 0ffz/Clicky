import me.dvyy.tailwind.GenerateTailwindCssTask

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
//tasks.withType<ProcessResources> {
//    val wasmOutput = file("../web/build/dist/wasmJs/productionExecutable")
//    if (wasmOutput.exists()) {
//        inputs.dir(wasmOutput)
//    }
//
//    from("../web/build/dist/wasmJs/productionExecutable") {
//        into("web")
//        include("**/*")
//    }
//    duplicatesStrategy = DuplicatesStrategy.WARN
//}

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlinx.html)
    implementation(libs.ktor.server.htmx)
    implementation(libs.ktor.htmx.html)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation("me.dvyy:shocky-icons:0.3.0-dev.6")
}

ktor {
    development = true
}

tasks {
    register<GenerateTailwindCssTask>("tailwind") {
        input = file("src/main/resources/tailwind.css")
        output = file("src/main/resources/web/styles/tailwind.css")
        watch = file("src/main/kotlin")
    }
    processResources { dependsOn("tailwind") }
}