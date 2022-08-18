package ru.itis.saetova.wizard.ui.search

import com.intellij.ui.table.JBTable
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableModel

class SearchResultTable(
    tableModel: TableModel,
    onResultClicked: (String) -> Unit
) : JBTable(tableModel) {

    init {
        setCellSelectionEnabled(true)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                val selectedResult = tableModel.getValueAt(selectedRow, 0).toString()
                onResultClicked(selectedResult)
            }
        })
    }

    override fun getCellRenderer(row: Int, column: Int): TableCellRenderer {
        return DetailsCellRenderer()
    }

    private class DetailsCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            require(value is String)
            val name = value.split(" ")[0]
            var fqName = value.split(" ")[1]
            if (fqName.length > 50) {
                fqName = "...${fqName.split(".").takeLast(2).joinToString(".")}"
            }
            val location = "   in $fqName"

            if (fqName.isEmpty()) {
                text = name
            } else {
                if (isSelected) {
                    background = table?.selectionBackground
                    foreground = table?.selectionForeground
                    text = "<html><font color=\"#FFFFFF\">${name}  </font><font color=\"#FFFFFF\">${location}</font></html>"
                }
                else{
                    background = table?.background
                    foreground = table?.foreground
                    text = "<html><font color=\"#FFFFFF\">${name}  </font><font color=\"#787878\">${location}</font></html>"
                }
            }
            return this
        }
    }
}