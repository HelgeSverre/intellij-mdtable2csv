package com.helgesverre.mdtable2csv.actions

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.awt.datatransfer.DataFlavor

/**
 * Verifies the clipboard and open-in-tab sinks end to end against a fixture
 * table. The native save dialog is exercised manually via runIde; its
 * filename derivation is unit-tested in [CsvFileNamesTest].
 */
class SinkActionsTest : BasePlatformTestCase() {

    private val table = """
        | A | B |
        |---|---|
        | 1 | <caret>2 |
    """.trimIndent()

    private fun perform(action: ConvertTableActionBase, fileName: String) {
        myFixture.configureByText(fileName, table)
        val context = SimpleDataContext.builder()
            .add(CommonDataKeys.EDITOR, myFixture.editor)
            .add(CommonDataKeys.PSI_FILE, myFixture.file)
            .add(CommonDataKeys.VIRTUAL_FILE, myFixture.file.virtualFile)
            .add(CommonDataKeys.PROJECT, project)
            .build()
        action.actionPerformed(TestActionEvent.createTestEvent(action, context))
    }

    fun testCopyPlacesCsvOnClipboard() {
        perform(CopyTableAsCsvAction(), "t.md")
        val text = CopyPasteManager.getInstance().contents
            ?.getTransferData(DataFlavor.stringFlavor) as? String
        assertEquals("A,B\n1,2", text)
    }

    fun testOpenCreatesPlainTextCsvTabWithExpectedText() {
        perform(OpenTableAsCsvAction(), "readme.md")
        val csvFile = FileEditorManager.getInstance(project).openFiles
            .firstOrNull { it.name == "readme.csv" }
        assertNotNull("expected a readme.csv tab to open", csvFile)
        val text = FileDocumentManager.getInstance().getDocument(csvFile!!)?.text
        assertEquals("A,B\n1,2", text)
    }
}
