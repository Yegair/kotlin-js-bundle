package io.yegair.kotlinjs.maven.bundle

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.versioning.VersionRange
import org.apache.maven.shared.dependency.graph.DependencyNode
import org.apache.maven.shared.dependency.graph.internal.DefaultDependencyNode

internal class DependencyGraphBuilder(
        parent: DependencyNode?,
        groupId: String,
        artifactId: String,
        version: String,
        scope: String?,
        type: String,
        classifier: String?,
        optional: Boolean,
        definitions: DependencyGraphBuilder.() -> Unit) {

    private val node = DefaultDependencyNode(
            parent,
            artifact(
                    groupId,
                    artifactId,
                    version,
                    scope,
                    type,
                    classifier,
                    optional
            ),
            null,
            null,
            null
    )

    private val children = mutableListOf<DependencyNode>()

    init {
        definitions.invoke(this)
    }

    fun child(
            groupId: String,
            artifactId: String,
            version: String,
            scope: String? = null,
            type: String = "jar",
            classifier: String? = null,
            optional: Boolean = false,
            definitions: DependencyGraphBuilder.() -> Unit = {}) {

        val node = DependencyGraphBuilder(
                parent = node,
                groupId = groupId,
                artifactId = artifactId,
                version = version,
                scope = scope,
                type = type,
                classifier = classifier,
                optional = optional,
                definitions = definitions
        ).build()

        children.add(node)
    }

    fun build(): DependencyNode {
        node.children = children
        return node
    }

}

internal fun root(
        groupId: String,
        artifactId: String,
        version: String,
        scope: String? = null,
        type: String = "jar",
        classifier: String? = null,
        optional: Boolean = false,
        definitions: DependencyGraphBuilder.() -> Unit = {}): DependencyNode {

    return DependencyGraphBuilder(
            parent = null,
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            scope = scope,
            type = type,
            classifier = classifier,
            optional = optional,
            definitions = definitions
    ).build()
}



internal fun artifact(
        groupId: String,
        artifactId: String,
        version: String,
        scope: String? = null,
        type: String = "jar",
        classifier: String? = null,
        optional: Boolean = false): Artifact {

    return DefaultArtifact(
            groupId,
            artifactId,
            VersionRange.createFromVersion(version),
            scope,
            type,
            classifier,
            DefaultArtifactHandler(),
            optional
    )
}