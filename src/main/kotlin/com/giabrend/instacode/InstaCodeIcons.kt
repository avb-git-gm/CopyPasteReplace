// File: InstaCodeIcons.kt
package com.giabrend.instacode

import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.diagnostic.Logger
import javax.swing.Icon

object InstaCodeIcons {
    private val LOG = Logger.getInstance(InstaCodeIcons::class.java)

    val COPY_ICON: Icon = loadIcon("/icons/pluginCopyAllIcon.svg")
    val PASTE_ICON: Icon = loadIcon("/icons/pluginPasteReplaceIcon.svg")
    val DELETE_ICON: Icon = loadIcon("/icons/pluginDeleteIcon.svg")
    val CHECK_ICON: Icon = loadIcon("/icons/pluginCheckIcon.svg")

    private fun loadIcon(path: String): Icon {
        return try {
            IconLoader.getIcon(path, InstaCodeIcons::class.java)
        } catch (e: Exception) {
            LOG.error("Error loading icon: $path", e)
            IconLoader.getIcon("/icons/defaultIcon.svg", InstaCodeIcons::class.java) // Fallback icon
        }
    }
}
