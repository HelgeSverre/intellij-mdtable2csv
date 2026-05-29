package com.helgesverre.mdtable2csv.actions

/** Derives the default CSV filename from the source document's name. */
object CsvFileNames {

    fun defaultFor(sourceFileName: String?): String {
        if (sourceFileName.isNullOrBlank()) return "table.csv"
        val base = sourceFileName.substringBeforeLast('.').ifBlank { sourceFileName }
        return "$base.csv"
    }
}
