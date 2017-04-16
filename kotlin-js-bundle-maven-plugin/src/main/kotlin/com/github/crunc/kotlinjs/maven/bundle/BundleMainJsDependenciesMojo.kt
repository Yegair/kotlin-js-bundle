package com.github.crunc.kotlinjs.maven.bundle

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File
import java.nio.file.Paths

@Mojo(
        name = BundleMainJsDependenciesMojo.Name,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
class BundleMainJsDependenciesMojo : BundleJsDependenciesMojo() {

    companion object {
        const val Name = "bundle-js-dependencies"
    }

    @Parameter(defaultValue = "\${project.build.directory}/kotlinjs-bundle/dependencies")
    override lateinit var extractDirectory: File

    @Parameter(defaultValue = "\${project.build.directory}/js")
    override lateinit var outputDirectory: File

    @Parameter(defaultValue = "\${project.artifactId}.bundle.js")
    override lateinit var outputFilename: String

    override fun projectJsFiles(): Sequence<File> {

        val dir = Paths.get(project.build.outputDirectory).toFile()

        val files = when {
            dir.exists() && dir.isDirectory -> dir.walk().asSequence()
            else -> emptySequence()
        }

        return files
    }
}
