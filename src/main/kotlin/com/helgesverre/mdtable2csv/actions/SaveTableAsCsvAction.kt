package com.helgesverre.mdtable2csv.actions

import com.helgesverre.mdtable2csv.MdTable2CsvBundle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.ui.Messages
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Saves the Markdown table at the caret to a chosen `.csv` file (R11, R14).
 * The write happens inside a write action via the VFS so the new file is
 * visible in the Project view; cancel and IO failures are handled rather than
 * swallowed.
 */
class SaveTableAsCsvAction : ConvertTableActionBase() {

    override fun consume(e: AnActionEvent, csv: String) {
        val project = e.project
        val sourceFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val descriptor = FileSaverDescriptor(
            MdTable2CsvBundle.message("action.save.dialog.title"),
            MdTable2CsvBundle.message("action.save.dialog.description"),
            "csv",
        )
        val wrapper = FileChooserFactory.getInstance()
            .createSaveFileDialog(descriptor, project)
            .save(sourceFile?.parent, CsvFileNames.defaultFor(sourceFile?.name))
            ?: return // user cancelled

        try {
            runWriteAction {
                val target = wrapper.getVirtualFile(true)
                    ?: throw IOException("Could not create the target file.")
                target.setBinaryContent(csv.toByteArray(StandardCharsets.UTF_8))
            }
        } catch (ex: IOException) {
            Messages.showErrorDialog(
                project,
                ex.message ?: "Failed to write the CSV file.",
                MdTable2CsvBundle.message("action.save.error.title"),
            )
        }
    }
}
