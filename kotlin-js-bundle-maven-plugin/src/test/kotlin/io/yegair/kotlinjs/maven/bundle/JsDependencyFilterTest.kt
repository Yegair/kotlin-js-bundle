package io.yegair.kotlinjs.maven.bundle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit test for [JsDependencyFilter]
 */
internal class JsDependencyFilterTest {

    /**
     * Tests the filters default config
     */
    @Nested
    inner class DefaultConfig {

        @Test
        fun includesJsFile() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test("/some/javascript/file.js")

            // then
            assertEquals(true, match)
        }

        @Test
        fun excludesMetaJsFile() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test("/some/javascript/file.meta.js")

            // then
            assertEquals(false, match)
        }

        @Test
        fun excludesMinJsFile() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test("/some/javascript/file.min.js")

            // then
            assertEquals(false, match)
        }

        @Test
        fun excludesBundleJsFile() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test("/some/javascript/file.bundle.js")

            // then
            assertEquals(false, match)
        }

        @Test
        fun excludesTrash() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test(".Hello123!#")

            // then
            assertEquals(false, match)
        }

        @Test
        fun excludesEmpty() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test("")

            // then
            assertEquals(false, match)
        }

        @Test
        fun excludesWhitespaces() {

            // given
            val filter = JsDependencyFilter()

            // when
            val match = filter.test(" \t ")

            // then
            assertEquals(false, match)
        }
    }
}