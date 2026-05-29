package com.helgesverre.mdtable2csv.settings

import com.helgesverre.mdtable2csv.MdTable2CsvBundle
import com.helgesverre.mdtable2csv.csv.Delimiter
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel

/**
 * Settings page at Settings/Preferences -> Tools -> Markdown Table to CSV.
 *
 * Exposes the delimiter preference as a radio-button group (three short,
 * mutually-exclusive options — radio buttons per JetBrains UI guidelines).
 */
class MdTable2CsvConfigurable : BoundConfigurable(
    MdTable2CsvBundle.message("settings.displayName"),
) {

    private val settings get() = MdTable2CsvSettings.getInstance()

    override fun createPanel(): DialogPanel = panel {
        buttonsGroup(MdTable2CsvBundle.message("settings.delimiter.label")) {
            Delimiter.entries.forEach { delimiter ->
                row {
                    radioButton(delimiter.displayName, delimiter)
                }
            }
        }.bind(settings::delimiter)
    }
}
