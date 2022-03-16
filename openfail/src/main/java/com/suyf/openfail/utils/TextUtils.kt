package com.suyf.ndkdev.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row

/**
 * @author Created by suyongfeng on 2022/3/10
 */
object TextUtils {

    fun stringCellValue(row: Row, colNum: Int): String {
        val cell = row.getCell(colNum)
        return when (cell.cellType) {
            Cell.CELL_TYPE_STRING -> cell.stringCellValue
            Cell.CELL_TYPE_NUMERIC -> cell.numericCellValue.toString()
            Cell.CELL_TYPE_BOOLEAN -> cell.booleanCellValue.toString()
            Cell.CELL_TYPE_FORMULA -> "formula"
            Cell.CELL_TYPE_ERROR -> "error"
            Cell.CELL_TYPE_BLANK -> ""
            else -> ""
        }
    }


    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.isEmpty()
    }
}