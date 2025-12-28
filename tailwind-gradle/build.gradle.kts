plugins {
    kotlin("jvm") version "2.3.0"
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
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
