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

import com.google.idea.perf.tracer.TraceTarget
import com.google.idea.perf.tracer.ui.TracerConfigModel.Column
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import javax.swing.ListSelectionModel
import javax.swing.table.JTableHeader

// TODO(baskakov): update description
/** Displays a list of tracepoints alongside their call counts and timing measurements. */
class TracerUIConfig(private val model: TracerConfigModel) : JBTable(model) {

    init {
        configureTracerTableOrTree(this)
    }

    override fun createDefaultTableHeader(): JTableHeader {
        return object: JBTableHeader() {
            init {
                // Override the renderer that JBTableHeader sets.
                // The default, center-aligned renderer looks better.
                defaultRenderer = createDefaultRenderer()
            }
        }
    }

    fun setTracingConfig(newStats: List<TraceTarget.Method>) {
        model.setTracepointStats(newStats)
    }

    companion object {

        /** Configuration common to both [TracerUIConfig] and [TracerTree]. */
        fun configureTracerTableOrTree(table: JBTable) {
            table.font = EditorUtil.getEditorFont()
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            table.setShowGrid(false)

            // Ensure the row heights for TracerTable and TracerTree match each other.
            table.rowHeight = JBUI.scale(22)

            // Column rendering.
            val tableColumns = table.columnModel.columns.toList()
            for (tableColumn in tableColumns) {
                val col = Column.valueOf(tableColumn.modelIndex)

                // Column widths.
                tableColumn.minWidth = 100
                tableColumn.preferredWidth = when (col) {
                    Column.OPTION -> 3
                    Column.CLASSES -> Integer.MAX_VALUE
                    Column.METHODS -> 100
                }
            }
        }
    }
}
