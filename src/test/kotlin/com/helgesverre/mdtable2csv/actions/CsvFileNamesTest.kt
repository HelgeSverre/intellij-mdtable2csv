package com.helgesverre.mdtable2csv.actions

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvFileNamesTest {

    @Test
    fun derivesFromSourceName() {
        assertEquals("README.csv", CsvFileNames.defaultFor("README.md"))
    }

    @Test
    fun fallsBackToTableWhenNull() {
        assertEquals("table.csv", CsvFileNames.defaultFor(null))
    }

    @Test
    fun fallsBackToTableWhenBlank() {
        assertEquals("table.csv", CsvFileNames.defaultFor("   "))
    }

    @Test
    fun handlesNameWithoutExtension() {
        assertEquals("notes.csv", CsvFileNames.defaultFor("notes"))
    }
}
