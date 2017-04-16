package com.github.crunc.kotlinjs.maven.bundle

import org.apache.maven.artifact.Artifact
import org.apache.maven.plugin.logging.Log
import org.apache.maven.shared.dependency.graph.DependencyNode
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor

class JsDependencyCollector(val log: Log) : DependencyNodeVisitor, Iterable<Artifact> {

    private val visited = mutableSetOf<Artifact>()
    private val collected = mutableListOf<Artifact>()

    val dependencies get() = collected.toList()

    override fun visit(node: DependencyNode?): Boolean = true

    override fun endVisit(node: DependencyNode?): Boolean {

        val artifact = node?.artifact

        if (artifact is Artifact && !visited.contains(artifact)) {

            if (artifact.type == "jar") {
                collected.add(artifact)
            }

            visited.add(artifact)
        }

        log.info("${artifact?.groupId} : ${artifact?.artifactId} : ${artifact?.version}")
        return true
    }

    override fun iterator(): Iterator<Artifact> = dependencies.iterator()
}