plugins {
    kotlin("jvm") version "2.2.20"
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
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
//
//sourceSets {
//    main {
//        java.srcDir("build/generated/sources/main/java")
//    }
//}
//
//kotlin {
//    compilerOptions {
//        freeCompilerArgs.add("-Xcontext-parameters")
//    }
//}
//
//publishing {
//    repositories {
//        maven {
//            name = "mineinabyss"
//            url = uri("https://repo.mineinabyss.com/releases")
//            credentials(PasswordCredentials::class)
//        }
//        maven {
//            name = "mineinabyssSnapshots"
//            url = uri("https://repo.mineinabyss.com/snapshots")
//            credentials(PasswordCredentials::class)
//        }
//    }
//}
