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
import org.apache.maven.shared.dependency.graph.DependencyNode
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor

/**
 * Collects the dependencies of a dependency tree into a list. The list is ordered so dependencies
 * have a smaller index than their dependents.
 *
 * @author Hauke Jaeger, hauke.jaeger@yegair.io
 */
class JsDependencyCollector : DependencyNodeVisitor, Iterable<Artifact> {

    /**
     * All artifacts that have already been visited.
     */
    private val visited = mutableSetOf<Artifact>()

    /**
     * The artifacts that have been collected.
     */
    private val collected = mutableListOf<Artifact>()

    /**
     * The artifacts that have been collected.
     */
    val dependencies get() = collected.toList()

    /**
     * Starts visiting a node
     */
    override fun visit(node: DependencyNode?): Boolean = true

    /**
     * Finishes visiting a node. Adds the artifact to the list of collected artifacts if it has
     * not already been collected from a visit to a different node.
     */
    override fun endVisit(node: DependencyNode?): Boolean {

        val artifact = node?.artifact

        if (artifact is Artifact) {

            if (!alreadyVisited(artifact)) {
                collect(artifact)
            }

            markVisited(artifact)
        }

        return true
    }

    private fun alreadyVisited(artifact: Artifact): Boolean {
        return visited.contains(artifact)
    }

    private fun collect(artifact: Artifact) {
        collected.add(artifact)
    }

    private fun markVisited(artifact: Artifact) {
        visited.add(artifact)
    }

    override fun iterator(): Iterator<Artifact> = dependencies.iterator()
}