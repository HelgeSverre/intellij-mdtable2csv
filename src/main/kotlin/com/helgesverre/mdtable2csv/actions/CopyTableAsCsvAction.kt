package com.helgesverre.mdtable2csv.actions

import com.helgesverre.mdtable2csv.MdTable2CsvBundle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.wm.WindowManager
import java.awt.datatransfer.StringSelection

/** Copies the Markdown table at the caret to the system clipboard as CSV (R9). */
class CopyTableAsCsvAction : ConvertTableActionBase() {

    override fun consume(e: AnActionEvent, csv: String) {
        CopyPasteManager.getInstance().setContents(StringSelection(csv))
        val project = e.project ?: return
        WindowManager.getInstance().getStatusBar(project)?.info =
            MdTable2CsvBundle.message("action.copy.success")
    }
}
