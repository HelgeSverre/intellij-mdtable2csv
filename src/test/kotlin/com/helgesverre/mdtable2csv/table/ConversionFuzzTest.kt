package com.helgesverre.mdtable2csv.table

import com.helgesverre.mdtable2csv.csv.CsvTestReader
import com.helgesverre.mdtable2csv.csv.CsvWriter
import com.helgesverre.mdtable2csv.csv.Delimiter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable
import java.util.Random

/**
 * Randomized robustness test for the full conversion path. Generates many
 * malformed / ragged / unicode-laden Markdown documents and asserts:
 *  - resolving the enclosing table at every caret offset never throws;
 *  - every parsed table extracts without throwing;
 *  - the writer round-trips the extracted model for every delimiter (where the
 *    model is CSV-representable — every row has at least one field).
 *
 * Seeds are fixed so any failure is reproducible.
 */
class ConversionFuzzTest : BasePlatformTestCase() {

    // No bare '|' (would split cells), no newline (would split rows); astral
    // codepoints are exercised in the pure CsvWriter fuzz instead.
    private val cellChars = "ab12  ,;\"\\éあ中".toCharArray()

    private fun cell(rnd: Random): String = buildString {
        repeat(rnd.nextInt(6)) {
            if (rnd.nextInt(12) == 0) append("\\|") else append(cellChars[rnd.nextInt(cellChars.size)])
        }
    }

    private fun rowLine(cols: Int, rnd: Random): String =
        "| " + (0 until cols).joinToString(" | ") { cell(rnd) } + " |\n"

    private fun randomDoc(rnd: Random): String = buildString {
        repeat(rnd.nextInt(3)) {                                   // 0..2 tables
            val cols = 1 + rnd.nextInt(4)
            append(rowLine(cols, rnd))                             // header
            append("|").append((0 until cols).joinToString("|") { " --- " }).append("|\n")
            repeat(rnd.nextInt(4)) {
                val ragged = (cols + rnd.nextInt(3) - 1).coerceAtLeast(1)
                append(rowLine(ragged, rnd))                       // ragged body rows
            }
            append("\nsome ").append(cell(rnd)).append(" prose\n\n")
        }
        if (rnd.nextBoolean()) append("| dangling | pipes | without separator |\n")
    }

    fun testConversionNeverThrowsAndWriterRoundTripsExtractedModel() {
        for (seed in 1..80) {
            val rnd = Random(seed.toLong())
            val doc = randomDoc(rnd)
            val file = myFixture.configureByText("fuzz-$seed.md", doc)

            // Caret resolution must be exception-free at every offset.
            var o = 0
            while (o < doc.length) {
                PsiTreeUtil.getParentOfType(file.findElementAt(o), MarkdownTable::class.java)
                o += 5
            }

            // Every parsed table extracts cleanly and the writer round-trips it.
            for (table in PsiTreeUtil.findChildrenOfType(file, MarkdownTable::class.java)) {
                val model = MarkdownTableExtractor.extract(table)
                for (delimiter in Delimiter.entries) {
                    val csv = CsvWriter.write(model, delimiter) // must not throw
                    if (model.isNotEmpty() && model.all { it.isNotEmpty() }) {
                        assertEquals(
                            "writer round-trip mismatch (seed=$seed, delimiter=$delimiter)",
                            model,
                            CsvTestReader.parse(csv, delimiter.char),
                        )
                    }
                }
            }
        }
    }
}
