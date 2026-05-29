package com.helgesverre.mdtable2csv.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.testFramework.LightVirtualFile

/**
 * Opens the Markdown table at the caret as CSV in a new in-memory editor tab
 * (R10). The buffer is plain text named `<base>.csv` — no file is written to
 * disk, and the plugin takes no CSV file-type dependency. Each invocation opens
 * a fresh tab.
 */
class OpenTableAsCsvAction : ConvertTableActionBase() {

    override fun consume(e: AnActionEvent, csv: String) {
        val project = e.project ?: return
        val name = CsvFileNames.defaultFor(e.getData(CommonDataKeys.VIRTUAL_FILE)?.name)
        val file = LightVirtualFile(name, PlainTextFileType.INSTANCE, csv)
        FileEditorManager.getInstance(project).openFile(file, true)
    }
}
