package io.yegair.kotlinjs.maven.bundle

import org.codehaus.plexus.components.io.fileselectors.FileInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.InputStream

/**
 * Unit test for [JsDependencySelector]
 *
 * @author Hauke Jaeger, hauke.jaeger@yegair.io
 */
internal class JsDependencySelectorTest {

    @Nested
    inner class DefaultConfig {

        @Test
        fun fileNameMatch() {

            // given
            val fileInfo = TestFileInfo("some/file.js")
            val filter = JsDependencyFilter()
            val selector = JsDependencySelector(filter)

            // when
            val result = selector.isSelected(fileInfo)

            // then
            assertEquals(true, result)
        }

        @Test
        fun fileNameMismatch() {

            // given
            val fileInfo = TestFileInfo("some/file.min.js")
            val filter = JsDependencyFilter()
            val selector = JsDependencySelector(filter)

            // when
            val result = selector.isSelected(fileInfo)

            // then
            assertEquals(false, result)
        }
    }

    internal class TestFileInfo(
            private val name: String,
            private val symbolicLink: Boolean = false,
            private val directory: Boolean = false,
            private val contents: String = "") : FileInfo {

        override fun isDirectory(): Boolean = directory

        override fun isSymbolicLink(): Boolean = symbolicLink

        override fun getName(): String = name

        override fun getContents(): InputStream = contents.byteInputStream()

        override fun isFile(): Boolean = !directory
    }
}