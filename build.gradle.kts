import nl.littlerobots.vcu.plugin.versionSelector

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.kotlin.plugin.serialization) apply false
    alias(libs.plugins.version.catalog.update)
}

// Only suggest stable releases (rejects alpha/beta/rc/dev/etc. but allows suffixes like guava's -jre)
val unstableVersion = Regex("""(?i).*[.-](alpha|beta|rc|cr|m|milestone|preview|eap|dev|snapshot)[.\d-]*""")

versionCatalogUpdate {
    versionSelector { !unstableVersion.matches(it.candidate.version) }
}

subprojects {
    group = "me.dvyy"
    version = "0.0.1"
}
