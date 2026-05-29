package com.helgesverre.mdtable2csv.table

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable

/**
 * Fixture-backed tests for [MarkdownTableExtractor]. Each case parses real
 * Markdown via the bundled Markdown plugin and asserts the extracted model.
 * Scenarios map to the plan's acceptance examples.
 */
class MarkdownTableExtractorTest : BasePlatformTestCase() {

    private fun extractFirstTable(markdown: String, fileName: String = "test.md"): List<List<String>> {
        val file = myFixture.configureByText(fileName, markdown)
        val table = PsiTreeUtil.findChildOfType(file, MarkdownTable::class.java)
            ?: error("No Markdown table found in fixture")
        return MarkdownTableExtractor.extract(table)
    }

    fun testHeaderAndBodyRowsWithSeparatorExcluded() {
        val rows = extractFirstTable(
            """
            | Name | Age |
            |------|-----|
            | Ada  | 36  |
            | Bob  | 40  |
            """.trimIndent()
        )
        assertEquals(
            listOf(
                listOf("Name", "Age"),
                listOf("Ada", "36"),
                listOf("Bob", "40"),
            ),
            rows,
        )
    }

    // AE2 — escaped pipe becomes a literal pipe.
    fun testEscapedPipeBecomesLiteralPipe() {
        val rows = extractFirstTable(
            """
            | Expr   |
            |--------|
            | A \| B |
            """.trimIndent()
        )
        assertEquals(listOf(listOf("Expr"), listOf("A | B")), rows)
    }

    // AE3 — inline formatting and <br> kept verbatim, no newline introduced.
    fun testInlineFormattingAndBrKeptVerbatim() {
        val rows = extractFirstTable(
            """
            | Note               |
            |--------------------|
            | **bold** <br> more |
            """.trimIndent()
        )
        assertEquals(listOf(listOf("Note"), listOf("**bold** <br> more")), rows)
    }

    // AE4 — ragged rows: pad short to header width, keep extra cells.
    fun testRaggedRowsPadShortAndKeepExtra() {
        val rows = extractFirstTable(
            """
            | A | B | C |
            |---|---|---|
            | 1 | 2 |
            | 1 | 2 | 3 | 4 |
            """.trimIndent()
        )
        assertEquals(
            listOf(
                listOf("A", "B", "C"),
                listOf("1", "2", ""),
                listOf("1", "2", "3", "4"),
            ),
            rows,
        )
    }

    fun testEmptyCellsBecomeEmptyStrings() {
        val rows = extractFirstTable(
            """
            | A   | B |
            |-----|---|
            |     | x |
            """.trimIndent()
        )
        assertEquals(listOf(listOf("A", "B"), listOf("", "x")), rows)
    }
}
