package ru.itis.saetova.wizard.ui.search

import javax.swing.table.AbstractTableModel

class SearchResultTableModel : AbstractTableModel() {

    private val columns = arrayOf("Result")

    private var tableData = mutableListOf<String>()

    override fun getRowCount(): Int = tableData.size

    override fun getColumnCount(): Int = columns.size

    override fun getColumnName(column: Int): String = columns.getOrNull(column) ?: "-"

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return tableData.getOrNull(rowIndex) ?: "-"
    }

    fun addData(data: String) {
        tableData.add(data)
        fireTableDataChanged()
    }

    fun setData(data: MutableList<String>) {
        tableData = data
        fireTableDataChanged()
    }

    fun clearData() {
        tableData = mutableListOf()
        fireTableDataChanged()
    }
}