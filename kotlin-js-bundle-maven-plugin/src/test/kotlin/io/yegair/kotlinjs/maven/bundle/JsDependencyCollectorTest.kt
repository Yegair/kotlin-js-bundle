package io.yegair.kotlinjs.maven.bundle

import org.apache.maven.plugin.logging.Log
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

/**
 * Unit test for [JsDependencyCollector]
 *
 * @author Hauke Jaeger, hauke.jaeger@yegair.io
 */
internal class JsDependencyCollectorTest {

    @Test
    fun collectsRoot() {

        // given
        val log = mockLog()
        val collector = JsDependencyCollector(log)
        val graph = root("org.foo", "bar", "1.0")

        // when
        graph.accept(collector)

        // then
        assertIterableEquals(listOf(
                artifact("org.foo", "bar", "1.0")
        ), collector)
    }

    @Test
    fun collectsChildren() {

        // given
        val collector = JsDependencyCollector(mockLog())
        val graph = root("org.foo", "bar", "1.1") {
            child("org.foo", "baz", "1.0") {
                child("com.acme", "lib", "1.0") {
                    child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
                }
                child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
            }
            child("io.foobar", "foo", "0.0.1") {
                child("org.foo", "baz", "1.0") {
                    child("com.acme", "lib", "1.0") {
                        child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
                    }
                    child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
                }
                child("com.acme", "dep", "2.0") {
                    child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
                }
                child("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2")
            }
        }

        // when
        graph.accept(collector)

        // then
        assertIterableEquals(listOf(
                artifact("org.jetbrains.kotlin", "kotlin-stdlib-js", "1.1.2"),
                artifact("com.acme", "lib", "1.0"),
                artifact("org.foo", "baz", "1.0"),
                artifact("com.acme", "dep", "2.0"),
                artifact("io.foobar", "foo", "0.0.1"),
                artifact("org.foo", "bar", "1.1")
        ), collector)
    }

    private fun mockLog(): Log {
        return Mockito.mock(Log::class.java)
    }
}