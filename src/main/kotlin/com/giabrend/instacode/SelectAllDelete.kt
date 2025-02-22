// File: SelectAllDelete.kt
package com.giabrend.instacode

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.util.Alarm
import com.intellij.openapi.diagnostic.Logger

class SelectAllDeleteAction : AnAction() {

    private val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD)
    private val LOG = Logger.getInstance(SelectAllDeleteAction::class.java)

    init {
        templatePresentation.icon = InstaCodeIcons.DELETE_ICON
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = (e.getData(CommonDataKeys.EDITOR) != null)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        CommandProcessor.getInstance().executeCommand(project, {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.setText("")
            }
        }, "Delete All", null)

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
