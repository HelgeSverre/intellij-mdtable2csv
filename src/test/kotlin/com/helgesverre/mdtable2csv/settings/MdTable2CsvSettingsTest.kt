package com.helgesverre.mdtable2csv.settings

import com.helgesverre.mdtable2csv.csv.Delimiter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure persistence-logic tests for [MdTable2CsvSettings]. The settings object
 * is constructed directly (no application service lookup needed) to verify the
 * default and the getState/loadState round-trip.
 */
class MdTable2CsvSettingsTest {

    @Test
    fun defaultDelimiterIsComma() {
        assertEquals(Delimiter.COMMA, MdTable2CsvSettings().delimiter)
    }

    @Test
    fun stateRoundTripPreservesDelimiter() {
        val original = MdTable2CsvSettings().apply { delimiter = Delimiter.SEMICOLON }

        val restored = MdTable2CsvSettings()
        restored.loadState(original.state)

        assertEquals(Delimiter.SEMICOLON, restored.delimiter)
    }
}
