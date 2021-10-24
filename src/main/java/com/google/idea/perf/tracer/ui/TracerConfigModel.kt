/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.idea.perf.tracer.ui

import com.google.idea.perf.tracer.TraceOption
import com.google.idea.perf.tracer.TraceTarget
import javax.swing.table.AbstractTableModel
import kotlin.math.min

/** The table model for [TracerTable]. */
class TracerConfigModel : AbstractTableModel() {
    var data: List<TraceTarget.Method> = emptyList()

    /** Table columns. */
    enum class Column(val displayName: String, val type: Class<*>) {
        OPTION("is traced", String::class.java),
        CLASSES("class", String::class.java),
        METHODS("method", String::class.java);

        companion object {
            val values = values()
            val count = values.size
            fun valueOf(col: Int): Column = values[col]
        }
    }

    override fun getColumnCount(): Int = Column.count

    override fun getColumnName(col: Int): String = Column.valueOf(col).displayName

    override fun getColumnClass(col: Int): Class<*> = Column.valueOf(col).type

    override fun getRowCount(): Int = data.size

    override fun getValueAt(row: Int, col: Int): Any {
        val stats = data[row]
        return when (Column.valueOf(col)) {
            Column.OPTION -> when(stats.traceOption) {
                TraceOption.COUNT_AND_WALL_TIME -> "+"
                TraceOption.COUNT_ONLY -> "%"
                TraceOption.UNTRACE -> "-"
            }
            Column.CLASSES -> stats.className
            Column.METHODS -> stats.methodName ?: ""
        }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = false

    fun setTracepointStats(newStats: List<TraceTarget.Method>) {
        val oldStats = data
        if (oldStats.size != newStats.size) {
            data = newStats
            fireTableDataChanged()
            return
        }

//        // Generate table model events.
//        val oldRows = oldStats.size
//        val newRows = newStats.size
//        when {
//            newRows > oldRows -> fireTableRowsInserted(oldRows, newRows - 1)
//            newRows < oldRows -> fireTableRowsDeleted(newRows, oldRows - 1)
//        }
//        val modifiedRows = min(oldRows, newRows)
//        if (modifiedRows > 0) {
//            fireTableRowsUpdated(0, modifiedRows - 1)
//        }
    }
}
