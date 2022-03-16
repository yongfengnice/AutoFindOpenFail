package com.suyf.openfail.utils

import com.suyf.openfail.excel.ExcelConstants
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object UnzipUtils {

    fun unAllDownZipFiles(filePath: String) {
        val hadUnzipFiles = mutableListOf<File>()
        while (true) {
            val listAllFiles = mutableListOf<File>()
            listAllZipFiles(listAllFiles, filePath)
            listAllFiles.removeAll(hadUnzipFiles)

            if (listAllFiles.isNullOrEmpty()) {
                break
            } else {
                unAllZipFiles(listAllFiles)
                hadUnzipFiles.addAll(listAllFiles)
            }
        }
    }


    private fun listAllZipFiles(mutableFiles: MutableList<File>, filePath: String) {
        val fileDir = File(filePath)
        val listFiles = fileDir.listFiles()
        for (file in listFiles) {
            if (file.isFile && file.absolutePath.endsWith(ExcelConstants.ZIP_SUFFIX)) {
                mutableFiles.add(file)
            } else if (file.isDirectory) {
                listAllZipFiles(mutableFiles, file.absolutePath)
            }
        }
    }

    private fun createDirectory(outputDir: String, subDir: String?) {
        var file = File(outputDir)
        if (!(subDir == null || subDir.trim { it <= ' ' } == "")) { //子目录不为空
            file = File("$outputDir/$subDir")
        }
        if (!file.exists()) {
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            file.mkdirs()
        }
    }

    private fun unAllZipFiles(fileList: MutableList<File>) {
        for (file in fileList) {
            if (file.absolutePath == null || !file.absolutePath.endsWith(ExcelConstants.ZIP_SUFFIX)) {
                continue
            }
            unzipFile(file)
        }
    }

    private fun unzipFile(file: File) {
        println("unzipFile:" + file.absolutePath)
        val charset = Charset.forName("CP866") //specifying alternative (non UTF-8) charset
        val zipFile = ZipFile(file, charset)
        val enums: Enumeration<*> = zipFile.entries()

        while (enums.hasMoreElements()) {
            val entry = enums.nextElement() as ZipEntry
            println("解压entry:" + entry.name)

            // 文件名不能含有“：”，否则会生成失败
            val fileName = entry.name.replace(":".toRegex(), " ")
            val subDir = file.absolutePath.substring(file.absolutePath.lastIndexOf("\\") + 1)
                .replace(ExcelConstants.ZIP_SUFFIX, "")

            createDirectory(file.parent + "/", subDir) //创建输出目录
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                inputStream = zipFile.getInputStream(entry)
                outputStream = FileOutputStream(File("${file.parent}/$subDir/$fileName"))
                var length = 0
                val b = ByteArray(2048)
                while (inputStream.read(b).also { length = it } != -1) {
                    outputStream.write(b, 0, length)
                }
            } catch (ex: Exception) {
                throw ex
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

}