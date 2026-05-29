package com.helgesverre.mdtable2csv.csv

/**
 * Field delimiter used when serializing a table to CSV.
 *
 * Modeled as an enum (rather than a raw [Char]) so the serializer and the
 * settings UI share one source of truth and the persisted preference stays
 * type-safe.
 */
enum class Delimiter(val char: Char, val displayName: String) {
    COMMA(',', "Comma  ( , )"),
    SEMICOLON(';', "Semicolon  ( ; )"),
    TAB('\t', "Tab")
}
