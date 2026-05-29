package com.helgesverre.mdtable2csv.settings

import com.helgesverre.mdtable2csv.csv.Delimiter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * Application-wide persisted settings. Currently just the CSV [Delimiter]
 * preference (default [Delimiter.COMMA]), shared by all three conversion
 * actions. Application scope (not per-project) matches the set-and-forget
 * intent: pick a delimiter once and every conversion uses it.
 */
@Service(Service.Level.APP)
@State(name = "MdTable2CsvSettings", storages = [Storage("mdtable2csv.xml")])
class MdTable2CsvSettings : PersistentStateComponent<MdTable2CsvSettings.State> {

    /** Serialized form. Enums persist by name, so reordering [Delimiter] is safe. */
    class State {
        var delimiter: Delimiter = Delimiter.COMMA
    }

    private var state = State()

    var delimiter: Delimiter
        get() = state.delimiter
        set(value) {
            state.delimiter = value
        }

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): MdTable2CsvSettings =
            ApplicationManager.getApplication().getService(MdTable2CsvSettings::class.java)
    }
}
