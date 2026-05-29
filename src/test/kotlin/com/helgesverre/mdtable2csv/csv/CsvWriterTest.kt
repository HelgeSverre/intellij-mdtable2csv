package com.helgesverre.mdtable2csv.csv

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure unit tests for [CsvWriter]. No IntelliJ fixture is needed — the writer
 * has no platform dependency. Scenarios map to the plan's acceptance examples.
 */
class CsvWriterTest {

    private fun csv(rows: List<List<String>>, delimiter: Delimiter = Delimiter.COMMA) =
        CsvWriter.write(rows, delimiter)

    // AE1 — special-character quoting with the comma delimiter.
    @Test
    fun quotesFieldsContainingTheDelimiter() {
        assertEquals("\"a, b\"", csv(listOf(listOf("a, b"))))
    }

    @Test
    fun doublesEmbeddedQuotes() {
        assertEquals("\"say \"\"hi\"\"\"", csv(listOf(listOf("say \"hi\""))))
    }

    @Test
    fun leavesPlainFieldsUnquoted() {
        assertEquals("plain", csv(listOf(listOf("plain"))))
    }

    // AE5 — delimiter-relative quoting: only the active delimiter forces quoting.
    @Test
    fun semicolonDelimiterQuotesSemicolonButNotComma() {
        assertEquals("\"a;b\"", csv(listOf(listOf("a;b")), Delimiter.SEMICOLON))
        assertEquals("a,b", csv(listOf(listOf("a,b")), Delimiter.SEMICOLON))
    }

    @Test
    fun quotesFieldsContainingNewlines() {
        assertEquals("\"line1\nline2\"", csv(listOf(listOf("line1\nline2"))))
    }

    @Test
    fun emptyFieldsAndAllEmptyRow() {
        assertEquals("", csv(listOf(listOf(""))))
        assertEquals(",,", csv(listOf(listOf("", "", ""))))
    }

    @Test
    fun tabDelimiterQuotesFieldsContainingTab() {
        assertEquals("\"a\tb\"", csv(listOf(listOf("a\tb")), Delimiter.TAB))
    }

    @Test
    fun joinsRowsWithLineFeedAndNoTrailingNewline() {
        assertEquals("h1,h2\na,b", csv(listOf(listOf("h1", "h2"), listOf("a", "b"))))
    }
}
