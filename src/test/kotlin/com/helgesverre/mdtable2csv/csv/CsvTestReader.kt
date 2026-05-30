package com.helgesverre.mdtable2csv.csv

/**
 * Minimal quote-aware RFC 4180 reader that mirrors [CsvWriter]'s output rules
 * (quoted fields, doubled embedded quotes, `\n` row separator). Test-only — used
 * by the fuzz tests to prove the writer round-trips arbitrary field content.
 *
 * Note: an empty input parses to `[[""]]` (one empty field), so round-trip
 * assertions only hold for non-empty models where every row has at least one
 * field — CSV cannot unambiguously represent a zero-field row.
 */
internal object CsvTestReader {

    fun parse(text: String, delimiter: Char): List<List<String>> {
        val rows = mutableListOf<MutableList<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (inQuotes) {
                when {
                    c == '"' && i + 1 < text.length && text[i + 1] == '"' -> { field.append('"'); i += 2 }
                    c == '"' -> { inQuotes = false; i++ }
                    else -> { field.append(c); i++ }
                }
            } else {
                when (c) {
                    '"' -> { inQuotes = true; i++ }
                    delimiter -> { row.add(field.toString()); field.setLength(0); i++ }
                    '\n' -> { row.add(field.toString()); field.setLength(0); rows.add(row); row = mutableListOf(); i++ }
                    else -> { field.append(c); i++ }
                }
            }
        }
        row.add(field.toString())
        rows.add(row)
        return rows.map { it.toList() }
    }
}
