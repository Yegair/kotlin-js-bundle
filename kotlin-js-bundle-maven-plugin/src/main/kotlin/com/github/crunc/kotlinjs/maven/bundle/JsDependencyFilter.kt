package com.github.crunc.kotlinjs.maven.bundle

import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.nio.file.Paths

class JsDependencyFilter(
        pattern: String = "glob:**.js",
        private val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher(pattern),
        private val excludes: List<String> = listOf(".meta.js", ".min.js", ".bundle.js")
) {
    fun test(name: String): Boolean =
            matcher.matches(Paths.get(name)) && !excludes.any { name.endsWith(it) }
}