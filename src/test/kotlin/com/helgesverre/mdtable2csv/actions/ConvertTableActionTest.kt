package com.helgesverre.mdtable2csv.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Fixture-backed tests for the action base: visibility gating across file types
 * and the end-to-end CSV build for a caret inside a table.
 */
class ConvertTableActionTest : BasePlatformTestCase() {

    private val action = object : ConvertTableActionBase() {
        var captured: String? = null
        public override fun consume(e: AnActionEvent, csv: String) {
            captured = csv
        }
    }

    private fun eventFor(markdown: String, fileName: String): AnActionEvent {
        myFixture.configureByText(fileName, markdown)
        val context = SimpleDataContext.builder()
            .add(CommonDataKeys.EDITOR, myFixture.editor)
            .add(CommonDataKeys.PSI_FILE, myFixture.file)
            .build()
        return TestActionEvent.createTestEvent(action, context)
    }

    private fun isVisibleFor(markdown: String, fileName: String = "t.md"): Boolean {
        val event = eventFor(markdown, fileName)
        action.update(event)
        return event.presentation.isEnabledAndVisible
    }

    private val tableWithCaret = """
        | A | B |
        |---|---|
        | 1 | <caret>2 |
    """.trimIndent()

    fun testEnabledInsideTableInMdFile() {
        assertTrue(isVisibleFor(tableWithCaret))
    }

    // R2 — activation also works in .markdown files.
    fun testEnabledInsideTableInMarkdownFile() {
        assertTrue(isVisibleFor(tableWithCaret, "t.markdown"))
    }

    fun testHiddenInProseOutsideTable() {
        assertFalse(isVisibleFor("Just some <caret>prose, no table here."))
    }

    fun testHiddenInNonMarkdownFile() {
        assertFalse(isVisibleFor("plain <caret>text", "notes.txt"))
    }

    fun testBuildsCsvForTableAtCaret() {
        val event = eventFor(
            """
            | Name | Age |
            |------|-----|
            | Ada  | <caret>36 |
            """.trimIndent(),
            "t.md",
        )
        action.actionPerformed(event)
        assertEquals("Name,Age\nAda,36", action.captured)
    }
}
