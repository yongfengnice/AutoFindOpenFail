package com.suyf.openfail.filter

import com.suyf.ndkdev.utils.TextUtils
import com.suyf.openfail.excel.ExcelConstants
import com.suyf.openfail.utils.FileUtils
import java.io.*

/**
 * @author Created by suyongfeng on 2022/3/16
 */
object KeywordFilter {

    /**
     * 分析：csffwb exp into...  java.io.FileNotFoundException: open failed: ENOENT (No such file or directory)
     *      csffwb exp into...  java.io.FileNotFoundException: open failed: EACCES (Permission denied)
     *      csffwb exp into...  java.io.FileNotFoundException: No content provider
     *      java.lang.SecurityException: Permission Denial: opening provider
     *      csffwb exp into...  java.io.IOException: read failed: ENOENT (No such file or directory)
     *      java.io.IOException: write failed: ENOSPC (No space left on device)
     */

    private val fileNotFoundList1 = listOf<String>(
        "csffwb exp into...  java.io.FileNotFoundException: open failed: ENOENT (No such file or directory)",
        "csffwb exp into...  java.io.FileNotFoundException: open failed: EACCES (Permission denied)",
        "csffwb exp into...  java.io.FileNotFoundException: No content provider"
    )

    private val permissionDenialList2 =
        listOf<String>("java.lang.SecurityException: Permission Denial: opening provider")

    private val ioExceptionList3 = listOf<String>(
        "csffwb exp into...  java.io.IOException: read failed: ENOENT (No such file or directory)",
        "csffwb exp into...  java.io.IOException: read failed: EIO (I/O error)",
        "csffwb exp into...  java.io.IOException: No such file or directory"
    )

    private val noSpaceLeftList4 = listOf<String>(
        "java.io.IOException: write failed: ENOSPC (No space left on device)"
    )


    private fun findAllIncludeFiles(mutableFiles: MutableList<File>, filePath: String) {
        val fileDir = File(filePath)
        val listFiles = fileDir.listFiles()
        for (file in listFiles) {
            if (file.isFile && file.absolutePath.endsWith(ExcelConstants.INCLUDE_SUFFIX)) {
                mutableFiles.add(file)
            } else if (file.isDirectory) {
                findAllIncludeFiles(mutableFiles, file.absolutePath)
            }
        }
    }

    fun findFileByKeyword(filePath: String) {
        val keywordList = noSpaceLeftList4
        val mutableFiles: MutableList<File> = mutableListOf()
        findAllIncludeFiles(mutableFiles, filePath)
        if (mutableFiles.isNullOrEmpty()) {
            println("findIncludeFileKeyword---文件为空")
            return
        }
        val numberList = mutableListOf<String>()
        val deleteFileList = mutableListOf<String>()
        for (file in mutableFiles) {
            val br = BufferedReader(InputStreamReader(FileInputStream(file), "UTF-8"))
            var line: String? = null
            var number = ""
            while (br.readLine().also { line = it } != null) {
                for (keyword in keywordList) {
                    if (line?.contains(keyword) == true) {
                        val numPath = file.absolutePath.substring(0, file.absolutePath.indexOf("#"))
                        number = numPath.substring(numPath.lastIndexOf("\\") + 1)
                    }
                }
                if (!TextUtils.isEmpty(number)) {
                    numberList.add(number)
                    deleteFileList.add(file.parent)
                    break
                }
            }
            FileUtils.closeQuietly(br)
        }
        numberList.sortWith { o1, o2 -> if (o1.toInt() > o2.toInt()) 1 else -1 }
        print("包含编号: ")
        numberList.forEach {
            print("$it, ")
        }
        println("")
        deleteFileList.forEach {
            FileUtils.deleteAllFile(it, ExcelConstants.INCLUDE_SUFFIX)
        }

    }
}