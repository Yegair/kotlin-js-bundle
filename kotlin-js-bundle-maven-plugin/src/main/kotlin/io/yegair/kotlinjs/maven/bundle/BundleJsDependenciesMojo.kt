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

import org.apache.maven.artifact.Artifact
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder
import org.apache.maven.shared.dependency.graph.DependencyNode
import org.codehaus.plexus.archiver.UnArchiver
import org.codehaus.plexus.archiver.manager.ArchiverManager
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

/**
 * Base for Mojos that produce JS bundles.
 *
 * @author Hauke Jaeger, hauke.jaeger@yegiar.io
 */
abstract class BundleJsDependenciesMojo : AbstractMojo() {

    @Component(hint = "default")
    private lateinit var dependencyGraphBuilder: DependencyGraphBuilder

    @Component
    private lateinit var archiverManager: ArchiverManager

    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    protected lateinit var project: MavenProject

    @Parameter(defaultValue = "\${session}", readonly = true, required = true)
    protected lateinit var session: MavenSession

    /**
     * Indicates whether the execution of this Mojo should be skipped
     */
    @Parameter(defaultValue = "false")
    private var skip: Boolean = false

    /**
     * Indicates whether `<editor-fold>` comments should be included in the bundle file.
     * They make it possible to collapse regions of the bundle file in certain editors.
     */
    @Parameter(defaultValue = "true")
    private var editorFold: Boolean = true

    /**
     * The directory where the dependencies will be extracted during the execution of this Mojo.
     */
    protected abstract val extractDirectory: File

    /**
     * The directory where the bundle file be be created, e.g. `${project.build.directory}/js`.
     */
    protected abstract val outputDirectory: File

    /**
     * The name of the bundle file, e.g. `foo.bundle.js`
     */
    protected abstract val outputFilename: String

    /**
     * Location of the bundle file (combination of [outputDirectory] and [outputFilename]).
     */
    private val outputFile: Path get() = outputDirectory.toPath().resolve(outputFilename)

    override fun execute() {

        when {

            project.packaging == "pom" ->
                log.debug("[kotlin-js-bundle-maven-plugin] goal could not be applied to pom project")

            skip ->
                log.debug("skipping [kotlin-js-bundle-maven-plugin] as per configuration")

            else ->
                bundleJsDependencies()
        }
    }

    /**
     * Main execution of this Mojo.
     */
    private fun bundleJsDependencies() {

        val graph = buildDependencyGraph()
        val collector = JsDependencyCollector(log)
        val filter = JsDependencyFilter()

        graph.accept(collector)

        extract(collector, filter)
        bundle(collector, filter)
    }

    /**
     * Builds the dependency graph for the current project.
     */
    private fun buildDependencyGraph(): DependencyNode {

        val sessionRequest = session.projectBuildingRequest
        val request = DefaultProjectBuildingRequest(sessionRequest)
        request.project = project

        return dependencyGraphBuilder.buildDependencyGraph(request, ScopeArtifactFilter("compile"))
    }

    /**
     * Extracts the given `dependencies` into the [extractDirectory]. Each artifact is extracted
     * into it's own sub-directory which is determined by [BundleJsDependenciesMojo.extractDir].
     * Only the files matching the given `filter` are extracted.
     */
    private fun extract(dependencies: Iterable<Artifact>, filter: JsDependencyFilter) =
            dependencies.forEach { artifact ->

                if (artifact.file?.isFile ?: false) {

                    val unarchiver: UnArchiver = archiverManager.getUnArchiver(artifact.type)
                    val targetDir = artifact.extractDir().toFile()

                    unarchiver.sourceFile = artifact.file
                    unarchiver.fileSelectors = arrayOf(JsDependencySelector(filter))
                    unarchiver.destDirectory = targetDir

                    targetDir.mkdirs()
                    unarchiver.extract()
                }
            }


    /**
     * Combines the JS files of the given dependencies into a single bundle.
     */
    private fun bundle(dependencies: Iterable<Artifact>, filter: JsDependencyFilter) {

        bundleWriter().use { writer ->

            dependencies.forEach { artifact ->

                editorFold(writer, "${artifact.groupId} : ${artifact.artifactId} : ${artifact.version}") {

                    artifact.jsFiles(filter).forEach { file ->

                        log.info("adding file to bundle: $file")

                        editorFold(writer, file.name) {

                            file.bufferedReader().use { reader ->
                                reader.copyTo(writer)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new [PrintWriter] for writing the bundle file.
     */
    private fun bundleWriter(): PrintWriter {
        val file = outputFile.toFile()
        file.parentFile.mkdirs()
        return file.printWriter()
    }

    /**
     * Wraps everything that is written to the given [PrintWriter] within the given `block` into
     * `<editor-fold>` comments with the given description.
     */
    private fun editorFold(writer:PrintWriter, description: String, block: () -> Unit) {
        startEditorFold(writer, description)
        block()
        endEditorFold(writer)
    }

    /**
     * Writes an opening `<editor-fold>` comment.
     */
    private fun startEditorFold(writer: PrintWriter, description: String) {
        if (editorFold) {
            writer.println("// <editor-fold description=\"$description\">\n")
        }
    }

    /**
     * Writes a closing `<editor-fold>` comment.
     */
    private fun endEditorFold(writer:PrintWriter) {
        if (editorFold) {
            writer.println("// </editor-fold>\n")
        }
    }

    /**
     * Resolves the directory the [Artifact] should be extracted to.
     */
    private fun Artifact.extractDir(): Path =
            extractDirectory.toPath()
                    .resolve(groupId)
                    .resolve(artifactId)
                    .resolve(version)

    /**
     * Resolves the JS files for the [Artifact]. If the [Artifact] is a dependency this method
     * delegates to [artifactJsFiles]. If the [Artifact] is the current porject this method
     * delegates to [projectJsFiles].
     */
    private fun Artifact.jsFiles(filter: JsDependencyFilter): Sequence<File> {

        val files = when {
            project.artifact == this -> projectJsFiles()
            else -> artifactJsFiles(this)
        }

        return files
                .filter { it.isFile }
                .filter { filter.test(it.name) }
                .sortedBy {
                    when {
                        it.isMainFileOf(this) -> Integer.MAX_VALUE - 1
                        it.isTestFileOf(this) -> Integer.MAX_VALUE
                        else -> 0
                    }
                }
    }

    /**
     * Resolves the extracted JS files for the [Artifact].
     */
    private fun artifactJsFiles(artifact: Artifact): Sequence<File> {

        val dir = artifact.extractDir().toFile()

        if (dir.exists() && dir.isDirectory) {
            return dir.walk().asSequence()
        } else {
            return emptySequence()
        }
    }

    /**
     * Resolves the JS files for the current project that should be included in the bundle.
     */
    abstract protected fun projectJsFiles(): Sequence<File>

    /**
     * Determines whether the [File] is the main JS file of the given [Artifact].
     */
    private fun File.isMainFileOf(artifact: Artifact): Boolean =
            nameWithoutExtension == artifact.artifactId

    /**
     * Determines whether the [File] is the test JS file of the given [Artifact].
     */
    private fun File.isTestFileOf(artifact: Artifact): Boolean =
            nameWithoutExtension == "${artifact.artifactId}-tests" ||
                    nameWithoutExtension == "${artifact.artifactId}-test"
}