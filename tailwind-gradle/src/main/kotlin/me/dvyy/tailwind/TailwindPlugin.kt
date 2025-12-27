package me.dvyy.tailwind

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.*

class TailwindPlugin : Plugin<Project> {
    override fun apply(target: Project) {
    }
}

/**
 * Gets OS preferred path for storing applications, this is used to cache tailwind verisons.
 */
val shockyInstallPath: Path = run {
    val osName = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")

    when {
        osName.contains("win") -> Path(System.getenv("LOCALAPPDATA") ?: "$userHome\\AppData\\Local") / "Shocky"
        osName.contains("mac") -> Path(userHome) / "Library" / "Application Support" / "Shocky"
        else -> Path(userHome) / ".local" / "share" / "shocky"
    }
}
val tailwindVersion: String = "v4.1.7"
val dest = shockyInstallPath / "tailwind" / "tailwind-cli-${tailwindVersion}"

class InstalltailwindCssTask : DefaultTask() {

    @TaskAction
    fun run() {
        if (dest.exists()) return
        dest.createParentDirectories()
        project.logger.info("Installing TailwindCSS $tailwindVersion to $dest...")

        val tailwindBaseUrl = "https://github.com/tailwindlabs/tailwindcss/releases/download/$tailwindVersion"
        val osName = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val tailwindFileName = when {
            osName.contains("win") && arch.contains("64") -> "tailwindcss-windows.exe"
            osName.contains("mac") && arch.contains("aarch64") -> "tailwindcss-macos-arm64"
            osName.contains("mac") -> "tailwindcss-macos-x64"
            osName.contains("nix") || osName.contains("nux") -> when {
                arch.contains("64") -> "tailwindcss-linux-x64"
                arch.contains("arm") -> when {
                    arch.contains("v7") -> "tailwindcss-linux-armv7"
                    else -> "tailwindcss-linux-arm64"
                }

                else -> throw IllegalStateException("Unsupported architecture: $arch")
            }

            else -> throw IllegalStateException("Unsupported OS or architecture: $osName $arch")
        }
        val tailwindUrl = "$tailwindBaseUrl/$tailwindFileName"

        dest.parent.createDirectories()
        URL(tailwindUrl).openStream().use { input ->
            Files.copy(input, dest)
        }
        if (!osName.contains("win")) {
            dest.toFile().setExecutable(true)
        }
    }
}

abstract class GenerateTailwindCssTask : DefaultTask() {
    @get:InputFile
    abstract val input: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:InputDirectory
    abstract val watch: DirectoryProperty

    // 2. Inject ExecOperations using an abstract getter
    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun run() {
        execOperations.exec {
            it.commandLine(
                buildList {
                    add(dest.pathString)
                    val input = input.asFile.get().toPath().pathString
                    if (input != null) addAll(listOf("-i", input))
                    addAll(
                        listOf(
                            "-o",
                            output.asFile.get().toPath().pathString,
                            "--minify"
                        )
                    )
                }
            )
        }
    }
}