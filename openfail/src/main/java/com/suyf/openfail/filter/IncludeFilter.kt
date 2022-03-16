package com.suyf.openfail.filter

import com.suyf.openfail.excel.ExcelConstants
import com.suyf.openfail.utils.FileUtils
import java.io.*


/**
 * @author Created by suyongfeng on 2022/3/11
 * 找出包括includeTagList关键字，但是不包含excludeTagList关键字的文件生成新的.include文件
 */
object IncludeFilter {

    private val includeTagList = mutableListOf("file_open_tag", "Exception")
    private val excludeTagList = mutableListOf(
        "SecurityException: getDeviceId",
        "[KApp]   java.lang.reflect.InvocationTargetException",
        "Caused by: java.lang.SecurityException",
        "android.os.Parcel.createException",
        "android.os.Parcel.readException",
        "exception: YunResultException",
        "exception.YunHttpIOException",
        "java.lang.IllegalStateException: 无锅",
        "exitDSC:  java.lang.IllegalArgumentException",
        "java.lang.SecurityException: getUniqueDeviceId",
        "[KDSC_TAG.WsChannel] onFailure:",
        "[KDSC_TAG]   android.os.DeadObjectException",
        "[KApp]   java.lang.SecurityException"
    )

    private fun isExclude(line: String?): Boolean {
        for (tag in excludeTagList) {
            if (line?.contains(tag) == true) {
                return true
            }
        }
        return false
    }

    private fun filterFileContent(inFile: File) {
        if (!inFile.exists()) {
            println("文件不存在。。。。")
        }
        val outFile = File("${inFile.absolutePath}.include")
        val br = BufferedReader(InputStreamReader(FileInputStream(inFile), "UTF-8"))
        var bo: BufferedWriter? = null
        var line: String? = null
        var isHit = false
        while (br.readLine().also { line = it } != null) {
            println("IncludeFilter--->$line")
            for (key in includeTagList) {
                if (line?.contains(key) == true && !isExclude(line)) {
                    isHit = true
                    if (bo == null) {
                        bo = BufferedWriter(OutputStreamWriter(FileOutputStream(outFile)))
                    }
                    bo?.appendLine(line)
                } else { //不包含tag的情况
                    val isNewLine = line?.contains("[I]") == true
                            || line?.contains("[E]") == true
                            || line?.contains("[W]") == true
                    if (isNewLine) { //其他tag
                        isHit = false
                    } else { //没有tag
                        if (isHit) { //上一次命中继续认为同一个tag内容
                            if (bo == null) {
                                bo = BufferedWriter(OutputStreamWriter(FileOutputStream(outFile)))
                            }
                            bo?.appendLine(line)
                        }
                    }

                }
            }
        }
        bo?.flush()
        FileUtils.closeQuietly(br)
        FileUtils.closeQuietly(bo)
    }

    private fun filterAllFileContent(allkguoLogFiles: MutableList<File>) {
        for (file in allkguoLogFiles) {
            if (file.absolutePath == null || !file.absolutePath.endsWith(ExcelConstants.KGUO_SUFFIX)) {
                continue
            }
            filterFileContent(file)
        }
    }

    private fun listAllKguoLogFiles(mutableFiles: MutableList<File>, filePath: String) {
        val fileDir = File(filePath)
        val listFiles = fileDir.listFiles()
        for (file in listFiles) {
            if (file.isFile && file.absolutePath.endsWith(ExcelConstants.KGUO_SUFFIX)) {
                mutableFiles.add(file)
            } else if (file.isDirectory) {
                listAllKguoLogFiles(mutableFiles, file.absolutePath)
            }
        }
    }

    fun filterAllDownFiles(filePath: String) {
        val hadFilterFiles = mutableListOf<File>()
        while (true) {
            val allkguoLogFiles = mutableListOf<File>()
            listAllKguoLogFiles(allkguoLogFiles, filePath)
            allkguoLogFiles.removeAll(hadFilterFiles)

            if (allkguoLogFiles.isNullOrEmpty()) {
                break
            } else {
                filterAllFileContent(allkguoLogFiles)
                hadFilterFiles.addAll(allkguoLogFiles)
            }
        }
    }
}