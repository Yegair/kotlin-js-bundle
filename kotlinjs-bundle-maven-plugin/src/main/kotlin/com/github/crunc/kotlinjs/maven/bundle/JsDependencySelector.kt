package com.github.crunc.kotlinjs.maven.bundle

import org.codehaus.plexus.components.io.fileselectors.FileInfo
import org.codehaus.plexus.components.io.fileselectors.FileSelector

class JsDependencySelector(private val filter: JsDependencyFilter) : FileSelector {

    override fun isSelected(fileInfo: FileInfo): Boolean = filter.test(fileInfo.name)
}