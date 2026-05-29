package com.helgesverre.mdtable2csv.settings

import com.helgesverre.mdtable2csv.MdTable2CsvBundle
import com.helgesverre.mdtable2csv.csv.Delimiter
import com.intellij.openapi.options.Configurable
import com.intellij.util.ui.JBUI
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton

/**
 * Settings page at Settings/Preferences -> Tools -> Markdown Table to CSV.
 *
 * Exposes the delimiter preference as a radio-button group (three short,
 * mutually-exclusive options — radio buttons per JetBrains UI guidelines).
 * Implemented as a plain [Configurable] with Swing components to keep the
 * binding explicit and version-stable.
 */
class MdTable2CsvConfigurable : Configurable {

    private val settings get() = MdTable2CsvSettings.getInstance()
    private val buttons = LinkedHashMap<Delimiter, JRadioButton>()

    override fun getDisplayName(): String = MdTable2CsvBundle.message("settings.displayName")

    override fun createComponent(): JComponent {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
            add(JLabel(MdTable2CsvBundle.message("settings.delimiter.label")))
        }
        val group = ButtonGroup()
        Delimiter.entries.forEach { delimiter ->
            val button = JRadioButton(delimiter.displayName)
            buttons[delimiter] = button
            group.add(button)
            panel.add(button)
        }
        reset()
        return panel
    }

    private fun selectedDelimiter(): Delimiter =
        buttons.entries.firstOrNull { it.value.isSelected }?.key ?: Delimiter.COMMA

    override fun isModified(): Boolean = selectedDelimiter() != settings.delimiter

    override fun apply() {
        settings.delimiter = selectedDelimiter()
    }

    override fun reset() {
        val current = settings.delimiter
        buttons.forEach { (delimiter, button) -> button.isSelected = delimiter == current }
    }
}
