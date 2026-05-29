package com.helgesverre.mdtable2csv.table

import com.intellij.psi.util.PsiTreeUtil
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTableCell
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTableRow

/**
 * Converts a Markdown [MarkdownTable] PSI element into a row/cell model: the
 * header row first, then each body row, each row a list of verbatim cell
 * strings.
 *
 * The header and body rows are [MarkdownTableRow] children of the table; the
 * alignment/separator row (`|---|`) is a distinct leaf element (not a
 * [MarkdownTableRow]), so iterating row children excludes it without any
 * special handling.
 *
 * Cell text is preserved as authored, with one normalization: surrounding
 * cell-padding whitespace is trimmed and an escaped pipe (`\|`) becomes a
 * literal `|`. Inline formatting, `<br>`, and inline HTML are left untouched.
 * Rows shorter than the header are padded with empty trailing fields; rows
 * with extra cells keep them — no cell content is dropped.
 */
object MarkdownTableExtractor {

    fun extract(table: MarkdownTable): List<List<String>> {
        val rows = PsiTreeUtil.getChildrenOfTypeAsList(table, MarkdownTableRow::class.java)
        if (rows.isEmpty()) return emptyList()

        val headerWidth = cellsOf(rows.first()).size
        return rows.map { row -> padToWidth(cellsOf(row), headerWidth) }
    }

    private fun cellsOf(row: MarkdownTableRow): List<String> =
        PsiTreeUtil.getChildrenOfTypeAsList(row, MarkdownTableCell::class.java)
            .map { normalize(it.text) }

    private fun padToWidth(cells: List<String>, width: Int): List<String> =
        if (cells.size >= width) cells else cells + List(width - cells.size) { "" }

    private fun normalize(rawCellText: String): String =
        rawCellText.trim().replace("\\|", "|")
}
