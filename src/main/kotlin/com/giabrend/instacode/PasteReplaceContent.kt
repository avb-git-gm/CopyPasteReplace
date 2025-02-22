// File: PasteReplaceContent.kt
package com.giabrend.instacode

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.Alarm
import com.intellij.openapi.diagnostic.Logger
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

class PasteReplaceContent : AnAction() {

    private val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD)
    private val LOG = Logger.getInstance(PasteReplaceContent::class.java)

    init {
        templatePresentation.icon = InstaCodeIcons.PASTE_ICON
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = (e.getData(CommonDataKeys.EDITOR) != null)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        if (!editor.document.isWritable) {
            LOG.warn("Attempted to modify a read-only document")
            return
        }

        val clipboardContents: Transferable = CopyPasteManager.getInstance().contents ?: return
        if (!clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) return

        val clipboardText = try {
            clipboardContents.getTransferData(DataFlavor.stringFlavor) as? String
        } catch (ex: Exception) {
            LOG.error("Clipboard read error", ex)
            return
        } ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.setText(clipboardText)
        }

        val presentation = e.presentation
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
