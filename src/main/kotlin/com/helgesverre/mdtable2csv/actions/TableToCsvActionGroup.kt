package com.helgesverre.mdtable2csv.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

/**
 * The editor right-click submenu hosting the three conversion actions. It is
 * hidden unless the caret is inside a Markdown table, so the group never shows
 * an empty popup elsewhere in the editor.
 */
class TableToCsvActionGroup : DefaultActionGroup() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = markdownTableAtCaret(e) != null
    }
}
