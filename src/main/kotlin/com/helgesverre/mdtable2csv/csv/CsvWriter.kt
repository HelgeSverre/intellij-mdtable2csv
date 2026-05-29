package com.helgesverre.mdtable2csv.csv

/**
 * Serializes a table model — rows of already-extracted string fields — to CSV
 * text following RFC 4180.
 *
 * A field is wrapped in double quotes only when it contains the active
 * delimiter, a double quote, a carriage return, or a line feed; any embedded
 * double quote is doubled (`"` -> `""`). Rows are joined with a line feed
 * (`\n`) and there is no trailing newline, so the clipboard, in-editor tab, and
 * saved-file sinks all produce identical bytes.
 *
 * This object is pure and free of any IntelliJ Platform dependency so the
 * quoting and joining rules are fully unit-testable without a running IDE.
 */
object CsvWriter {

    private const val ROW_SEPARATOR = "\n"

    fun write(rows: List<List<String>>, delimiter: Delimiter): String =
        rows.joinToString(ROW_SEPARATOR) { row ->
            row.joinToString(delimiter.char.toString()) { field -> encodeField(field, delimiter) }
        }

    private fun encodeField(field: String, delimiter: Delimiter): String {
        val needsQuoting = field.any { ch ->
            ch == delimiter.char || ch == '"' || ch == '\r' || ch == '\n'
        }
        if (!needsQuoting) return field
        return "\"" + field.replace("\"", "\"\"") + "\""
    }
}
