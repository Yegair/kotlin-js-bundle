package io.yegair.kotlinjs.maven.bundle

/*
 * MIT License
 *
 * Copyright (c) 2017 Hauke Jaeger http://yegair.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    @Parameter(defaultValue = "\${project.build.directory}/kotlin-js-bundle/test-dependencies")
    override lateinit var extractDirectory: File

    @Parameter(defaultValue = "\${project.build.directory}/test-js")
    override lateinit var outputDirectory: File

    @Parameter(defaultValue = "\${project.artifactId}-tests.bundle.js")
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
