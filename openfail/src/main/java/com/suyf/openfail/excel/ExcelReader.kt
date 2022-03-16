package com.suyf.openfail.excel

import com.suyf.ndkdev.utils.TextUtils
import com.suyf.openfail.utils.FileUtils
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.ArrayList

/**
 * @author Created by suyongfeng on 2022/3/10
 */
object ExcelReader {

    fun readExcel(filePath: String): List<ExcelData>? {
        var workbook: Workbook? = null
        try {
            // 获取Excel文件
            val excelFile = File(filePath)
            if (!excelFile.exists()) {
                println("指定的Excel文件不存在！")
                return null
            }
            // 获取Excel工作簿
            workbook = getWorkbookByFile(excelFile)
            return if (workbook == null) {
                println("获取工作簿失败workbook==null！")
                null
            } else {
                // 读取excel中的数据
                parseExcelLastSheet(workbook)
            }
        } catch (e: Exception) {
            println("解析Excel失败，文件名：" + filePath + " 错误信息：" + e.message)
            return null
        } finally {
            FileUtils.closeQuietly(workbook)
        }
        return null
    }

    private fun parseExcelLastSheet(workbook: Workbook): List<ExcelData> {
        val resultDataList: MutableList<ExcelData> = ArrayList()
        // 解析sheet
        if (workbook.numberOfSheets <= 0) {
            println("工作簿不存在表单。。。")
            return resultDataList
        }
        val sheet = workbook.getSheetAt(ExcelConstants.EXCEL_SHEET_NUMBER)
        // 校验sheet是否合法
        if (sheet == null) {
            println("工作簿第${ExcelConstants.EXCEL_SHEET_NUMBER}个表单为空。。。")
            return resultDataList
        }
        println("sheet.firstRowNum:" + sheet.firstRowNum)
        println("sheet.physicalNumberOfRows:" + sheet.physicalNumberOfRows)
        val rowStart = sheet.firstRowNum + 1
        val rowEnd = sheet.physicalNumberOfRows
        for (rowNum in rowStart until rowEnd) {
            val row = sheet.getRow(rowNum) ?: continue
            val resultData: ExcelData = convertRowToData(row, rowNum)
            if (resultData.isValidData()) {
                resultDataList.add(resultData)
            } else {
                break
            }
        }
        return resultDataList
    }


    private fun convertRowToData(row: Row, rowNum: Int): ExcelData {
        val excelData = ExcelData()
        excelData.indexNum = (rowNum+1).toString()
        excelData.url = TextUtils.stringCellValue(row, 1) //从0开始，第1列是url
        if (!TextUtils.isEmpty(excelData.url)) {
            if (excelData.url.contains("md5=", true)){
                excelData.md5 = excelData.url.substring(excelData.url.indexOf("md5=") + 4)
            }
            if (TextUtils.isEmpty(excelData.md5) && excelData.url.contains("/")) {
                excelData.md5 = excelData.url.substring(excelData.url.lastIndexOf("/") + 1)
            }
            if (TextUtils.isEmpty(excelData.md5)){
                excelData.md5 = excelData.url
            }
        }
        excelData.appVer = TextUtils.stringCellValue(row, 6)
        excelData.channel = TextUtils.stringCellValue(row, 8)
        excelData.regUser = TextUtils.stringCellValue(row, 13)
        excelData.phoneVer = TextUtils.stringCellValue(row, 15)
        excelData.phoneName = TextUtils.stringCellValue(row, 17)
            .replace("/", "-").replace(":", "-")
        if (TextUtils.isEmpty(excelData.phoneName)) {
            excelData.phoneName = "other"
        }
        return excelData
    }

    //获取工作簿
    private fun getWorkbookByFile(excelFile: File): Workbook? {
        // 获取Excel后缀名
        val filePath = excelFile.absolutePath
        println("getWorkbookByFile:$filePath")
        val fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length)
        var workbook: Workbook? = null
        if (fileType.equals(ExcelConstants.XLS_SUFFIX, ignoreCase = true)) {
            workbook = HSSFWorkbook(FileInputStream(excelFile))
        } else if (fileType.equals(ExcelConstants.XLSX_SUFFIX, ignoreCase = true)) {
            workbook = XSSFWorkbook(FileInputStream(excelFile))
        }
        return workbook
    }
}