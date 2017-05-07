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

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

/**
 * Tests a file name whether it is a Javascript file that should be included into the bundle.
 * By default the filter matches all files with a `.js` extension, except files ending in
 * `.meta.js`, `.min.js`, `.bundle.js`
 *
 * @author Hauke Jaeger, hauke.jaeger@yegair.io
 *
 * @param pattern The pattern for files that should be included (see [java.nio.file.FileSystem.getPathMatcher]).
 * @property excludeSuffixes zero or more file suffixes that should be excluded
 */
class JsDependencyFilter(
        pattern: String = "glob:**.js",
        private val excludeSuffixes: List<String> = listOf(".meta.js", ".min.js", ".bundle.js")
) {

    /**
     * Checks whether a file/path should be included
     */
    private val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher(pattern)

    /**
     * Tests whether the given path is a file that should be included in the bundle.
     */
    fun test(path: String): Boolean = test(Paths.get(path))

    /**
     * Tests whether the given path should be included in the bundle.
     */
    fun test(path: Path): Boolean = matcher.matches(path) && !isExcluded(path)

    /**
     * Tests whether the given path is excluded according to [excludeSuffixes]
     */
    private fun isExcluded(path: Path): Boolean {

        return excludeSuffixes.any { exclude ->
            path.toString().endsWith(exclude)
        }
    }
}