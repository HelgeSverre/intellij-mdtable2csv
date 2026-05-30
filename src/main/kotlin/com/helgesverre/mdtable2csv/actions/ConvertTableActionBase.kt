package com.helgesverre.mdtable2csv.actions

import com.helgesverre.mdtable2csv.MdTable2CsvBundle
import com.helgesverre.mdtable2csv.csv.CsvWriter
import com.helgesverre.mdtable2csv.settings.MdTable2CsvSettings
import com.helgesverre.mdtable2csv.table.MarkdownTableExtractor
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runReadAction
import com.intellij.psi.util.PsiTreeUtil
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable

/**
 * Base for the three "convert the Markdown table under the caret to CSV"
 * actions. Centralizes visibility gating (shown only when the caret is inside a
 * [MarkdownTable]) and building the CSV string from that table using the
 * current delimiter preference. Subclasses implement only [consume] — the sink
 * (clipboard / new tab / file).
 *
 * Threading: update() runs on a background thread (BGT) and resolves the table
 * inside a read action via [markdownTableAtCaret].
 */
abstract class ConvertTableActionBase : AnAction() {

    final override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    final override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = markdownTableAtCaret(e) != null
    }

    final override fun actionPerformed(e: AnActionEvent) {
        val csv = buildCsv(e)
        if (csv == null) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP_ID)
                .createNotification(MdTable2CsvBundle.message("notification.noContent"), NotificationType.WARNING)
                .notify(e.project)
            return
        }
        consume(e, csv)
    }

    /** The sink implemented by each concrete action (clipboard, editor tab, file). */
    protected abstract fun consume(e: AnActionEvent, csv: String)

    /**
     * Builds the CSV for the table at the caret using the current delimiter, or
     * null when there is no table or it produced no rows.
     */
    protected fun buildCsv(e: AnActionEvent): String? {
        val rows = runReadAction {
            val table = markdownTableAtCaret(e) ?: return@runReadAction emptyList<List<String>>()
            MarkdownTableExtractor.extract(table)
        }
        if (rows.isEmpty()) return null
        return CsvWriter.write(rows, MdTable2CsvSettings.getInstance().delimiter)
    }
}

/**
 * Resolves the innermost [MarkdownTable] enclosing the caret, or null. Shared by
 * [ConvertTableActionBase] and the popup [TableToCsvActionGroup]. The caret
 * offset and PSI lookups happen inside a read action so this is safe to call
 * from the background action-update thread.
 */
internal fun markdownTableAtCaret(e: AnActionEvent): MarkdownTable? {
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
    val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return null
    return runReadAction {
        val offset = editor.caretModel.offset
        val element = psiFile.findElementAt(offset) ?: return@runReadAction null
        PsiTreeUtil.getParentOfType(element, MarkdownTable::class.java)
    }
}

/** Must match the notificationGroup id registered in plugin.xml. */
private const val NOTIFICATION_GROUP_ID = "Markdown Table to CSV"
