plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("tailwind") {
            id = "me.dvyy.tailwind"
            implementationClass = "me.dvyy.tailwind.TailwindPlugin"
        }
    }
}
