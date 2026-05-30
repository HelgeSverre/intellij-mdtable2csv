package com.helgesverre.mdtable2csv.table

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable

/**
 * Targeted edge-case coverage for [MarkdownTableExtractor] against real parsed
 * Markdown. Complements the randomized [ConversionFuzzTest].
 */
class MarkdownTableExtractorEdgeCasesTest : BasePlatformTestCase() {

    private fun extract(markdown: String): List<List<String>> {
        val file = myFixture.configureByText("t.md", markdown)
        val table = PsiTreeUtil.findChildOfType(file, MarkdownTable::class.java)
            ?: error("no Markdown table parsed from fixture")
        return MarkdownTableExtractor.extract(table)
    }

    fun testSingleColumn() {
        assertEquals(
            listOf(listOf("H"), listOf("a"), listOf("b")),
            extract("| H |\n|---|\n| a |\n| b |"),
        )
    }

    fun testHeaderOnlyNoBodyRows() {
        assertEquals(listOf(listOf("A", "B")), extract("| A | B |\n|---|---|"))
    }

    fun testAllEmptyCells() {
        assertEquals(
            listOf(listOf("A", "B"), listOf("", "")),
            extract("| A | B |\n|---|---|\n|   |   |"),
        )
    }

    fun testWhitespaceOnlyCellBecomesEmpty() {
        assertEquals(
            listOf(listOf("A"), listOf("")),
            extract("| A |\n|---|\n|       |"),
        )
    }

    fun testCellsWithDelimitersAndQuotesKeptVerbatim() {
        assertEquals(
            listOf(listOf("H"), listOf("a,b;c\"d")),
            extract("| H |\n|---|\n| a,b;c\"d |"),
        )
    }

    fun testUnicodeAndEmojiPreserved() {
        assertEquals(
            listOf(listOf("Name"), listOf("Åse 中 😀")),
            extract("| Name |\n|------|\n| Åse 中 😀 |"),
        )
    }

    fun testEscapedPipeAndLoneBackslashKeptDistinct() {
        assertEquals(
            listOf(listOf("Expr"), listOf("a | b"), listOf("c\\d")),
            extract("| Expr |\n|------|\n| a \\| b |\n| c\\d |"),
        )
    }

    fun testShortRowPaddedToHeaderWidth() {
        assertEquals(
            listOf(listOf("A", "B", "C", "D"), listOf("1", "", "", "")),
            extract("| A | B | C | D |\n|---|---|---|---|\n| 1 |"),
        )
    }

    fun testMultipleTablesEachExtractIndependently() {
        val file = myFixture.configureByText(
            "t.md",
            "| A |\n|---|\n| 1 |\n\nsome prose\n\n| B | C |\n|---|---|\n| 2 | 3 |",
        )
        val tables = PsiTreeUtil.findChildrenOfType(file, MarkdownTable::class.java).toList()
        assertEquals(2, tables.size)
        assertEquals(listOf(listOf("A"), listOf("1")), MarkdownTableExtractor.extract(tables[0]))
        assertEquals(listOf(listOf("B", "C"), listOf("2", "3")), MarkdownTableExtractor.extract(tables[1]))
    }
}
