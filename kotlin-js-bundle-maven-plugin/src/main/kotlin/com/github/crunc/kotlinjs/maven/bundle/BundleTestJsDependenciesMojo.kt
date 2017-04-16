package com.github.crunc.kotlinjs.maven.bundle

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File
import java.nio.file.Paths


@Mojo(
        name = BundleTestJsDependenciesMojo.Name,
        defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
class BundleTestJsDependenciesMojo : BundleJsDependenciesMojo() {

    companion object {
        const val Name = "bundle-js-test-dependencies"
    }

    @Parameter(defaultValue = "\${project.build.directory}/kotlinjs-bundle/test-dependencies")
    override lateinit var extractDirectory: File

    @Parameter(defaultValue = "\${project.build.directory}/test-js")
    override lateinit var outputDirectory: File

    @Parameter(defaultValue = "\${project.artifactId}-test.bundle.js")
    override lateinit var outputFilename: String

    override fun projectJsFiles(): Sequence<File> {

        val mainDir = Paths.get(project.build.outputDirectory).toFile()
        val testDir = Paths.get(project.build.testOutputDirectory).toFile()

        val mainFiles = when {
            mainDir.exists() && mainDir.isDirectory -> mainDir.walk().asSequence()
            else -> emptySequence()
        }

        val testFiles = when {
            testDir.exists() && testDir.isDirectory -> testDir.walk().asSequence()
            else -> emptySequence()
        }

        return mainFiles + testFiles
    }
}
