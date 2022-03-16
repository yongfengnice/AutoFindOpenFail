package com.suyf.openfail

import com.suyf.openfail.utils.UnzipUtils
import com.suyf.openfail.excel.ExcelConstants
import com.suyf.openfail.excel.ExcelReader
import com.suyf.openfail.filter.IncludeFilter
import com.suyf.openfail.filter.KeywordFilter
import com.suyf.openfail.utils.FileUtils
import com.suyf.openfail.utils.HttpUtils
import java.io.File

/**
 * @author Created by suyongfeng on 2022/3/16
 */
object MainEntry {

    @JvmStatic
    fun main2(args: Array<String>) {
        FileUtils.deleteAllFile("F:\\openfail\\download")
    }

    @JvmStatic
    fun main(args: Array<String>) {
          //0.删除所有文件
//        FileUtils.deleteAllFile(ExcelConstants.EXCEL_DOWNLOAD_DIR)
//        //1.分析
//        val excelDataList = ExcelReader.readExcel(ExcelConstants.EXCEL_FILE_PATH)
//        if (excelDataList == null) {
//            println("ExcelReader.readExcel为空。。。。。")
//            return
//        }
//        println("readExcel:" + excelDataList.size)
//        //2.下载
//        println("downExcelFiles--start")
//        HttpUtils.downExcelFiles(excelDataList)
//        println("downExcelFiles--end")
//        //3.解压
//        println("unzipFile--start")
//        UnzipUtils.unAllDownZipFiles(ExcelConstants.EXCEL_DOWNLOAD_DIR)
//        println("unzipFile--end")
//        //4.过滤出有关键字的文件
//        println("filterFileContent--start")
//        IncludeFilter.filterAllDownFiles(ExcelConstants.EXCEL_DOWNLOAD_DIR)
//        println("filterFileContent--end")
          //5.统计出现指定关键字的文件个数，每一步需要手动操作
        println("KeywordFilter--start")
        KeywordFilter.findFileByKeyword(ExcelConstants.EXCEL_DOWNLOAD_DIR)
        println("KeywordFilter--end")
    }

}