package com.github.crunc.kotlinjs.maven.bundle

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
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

abstract class BundleJsDependenciesMojo : AbstractMojo() {

    @Component(hint = "default")
    private lateinit var dependencyGraphBuilder: DependencyGraphBuilder

    @Component
    private lateinit var archiverManager: ArchiverManager

    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    protected lateinit var project: MavenProject

    @Parameter(defaultValue = "\${session}", readonly = true, required = true)
    protected lateinit var session: MavenSession

    @Parameter(defaultValue = "false")
    private var skip: Boolean = false

    protected abstract val extractDirectory: File
    protected abstract val outputDirectory: File
    protected abstract val outputFilename: String

    private val outputFile: Path get() = outputDirectory.toPath().resolve(outputFilename)

    override fun execute() {

        when {

            project.packaging == "pom" ->
                log.debug("[kotlinjs-bundle-maven-plugin] goal could not be applied to pom project")

            skip ->
                log.debug("skipping [kotlinjs-bundle-maven-plugin] as per configuration")

            else ->
                bundleJsDependencies()
        }
    }

    private fun bundleJsDependencies() {

        val graph = buildDependencyGraph()
        val collector = JsDependencyCollector(log)
        val filter = JsDependencyFilter()

        graph.accept(collector)

        extract(collector, filter)
        bundle(collector, filter)
    }

    private fun buildDependencyGraph(): DependencyNode {

        val sessionRequest = session.projectBuildingRequest
        val request = DefaultProjectBuildingRequest(sessionRequest)
        request.project = project

        return dependencyGraphBuilder.buildDependencyGraph(request, ScopeArtifactFilter("compile"))
    }

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


    private fun bundle(dependencies: Iterable<Artifact>, filter: JsDependencyFilter) {

        outputWriter().use { writer ->

            dependencies.forEach { artifact ->

                writer.println("// <editor-fold description=\"${artifact.groupId} : ${artifact.artifactId} : ${artifact.version}\">\n")

                artifact.jsFiles(filter).forEach { file ->

                    log.info("adding file to bundle: $file")

                    writer.println("// <editor-fold description=\"${file.name}\">\n")

                    file.bufferedReader().use { reader ->
                        reader.copyTo(writer)
                    }

                    writer.println("// </editor-fold>\n")
                }

                writer.println("// </editor-fold>\n")
            }
        }
    }

    private fun outputWriter(): PrintWriter {
        val file = outputFile.toFile()
        file.parentFile.mkdirs()
        return file.printWriter()
    }

    private fun Artifact.extractDir(): Path =
            extractDirectory.toPath()
                    .resolve(groupId)
                    .resolve(artifactId)
                    .resolve(version)

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

    private fun artifactJsFiles(artifact: Artifact): Sequence<File> {

        val dir = artifact.extractDir().toFile()

        if (dir.exists() && dir.isDirectory) {
            return dir.walk().asSequence()
        } else {
            return emptySequence()
        }
    }

    abstract protected fun projectJsFiles(): Sequence<File>

    private fun File.isMainFileOf(artifact: Artifact): Boolean =
            nameWithoutExtension == artifact.artifactId

    private fun File.isTestFileOf(artifact: Artifact): Boolean =
            nameWithoutExtension == "${artifact.artifactId}-tests"
}