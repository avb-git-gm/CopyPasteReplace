// File: CopyAllCodeAction.kt
package com.giabrend.instacode

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.Alarm
import com.intellij.openapi.diagnostic.Logger
import java.awt.datatransfer.StringSelection

class CopyAllCodeAction : AnAction() {

    private val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD)
    private val LOG = Logger.getInstance(CopyAllCodeAction::class.java)

    init {
        templatePresentation.icon = InstaCodeIcons.COPY_ICON
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = (e.getData(CommonDataKeys.EDITOR) != null)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document

        if (document.textLength > 1_000_000) { // Check if file is larger than 1MB
            LOG.warn("Copying a very large file may slow down the IDE")
        }

        val textToCopy = document.text
        CopyPasteManager.getInstance().setContents(StringSelection(textToCopy))

        val presentation = event.presentation
        val oldIcon = presentation.icon
        presentation.icon = InstaCodeIcons.CHECK_ICON

        alarm.addRequest({
            presentation.icon = oldIcon
        }, 1000)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
