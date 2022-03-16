package com.suyf.openfail.utils

import java.io.*
import java.nio.channels.FileChannel

/**
 * @author Created by suyongfeng on 2021/9/18
 */
object FileUtils {

    fun copyFile(src: File?, destPath: String?): Boolean {
        if (src == null || !src.exists() || destPath == null) {
            return false
        }
        val dest = File(destPath)
        if (dest.exists()) {
            val isSuccess = dest.delete()
            if (!isSuccess) {
                return false
            }
        }
        try {
            val isSuccess = dest.createNewFile()
            if (!isSuccess) {
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return copyFileByChannel(src, dest)
    }

    private fun copyFileByChannel(srcFile: File, destFile: File): Boolean {
        var result = false
        var srcChannel: FileChannel? = null
        var dstChannel: FileChannel? = null
        try {
            srcChannel = FileInputStream(srcFile).channel
            dstChannel = FileOutputStream(destFile).channel
            srcChannel.transferTo(0, srcChannel.size(), dstChannel)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeQuietly(srcChannel)
            closeQuietly(dstChannel)
        }
        return result
    }

    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteAllFile(filePath: String) {
        val rootFile = File(filePath)
        if (!rootFile.exists()) {
            println("deleteAllFile---file不存在。。。")
            return
        }
        val listFiles = rootFile.listFiles()
        if (listFiles.isNullOrEmpty()) {
            println("deleteAllFile---listFiles为空。。。")
            return
        }
        for (file in listFiles) {
            file.deleteRecursively()
        }
    }

    fun deleteAllFile(filePath: String, fileExt: String) {
        val rootFile = File(filePath)
        if (!rootFile.exists()) {
            println("deleteAllFile---file不存在。。。")
            return
        }
        val listFiles = rootFile.listFiles(FileFilter { it.absolutePath.endsWith(fileExt) })
        if (listFiles.isNullOrEmpty()) {
            println("deleteAllFile---listFiles为空。。。")
            return
        }
        for (file in listFiles) {
            file.delete()
        }
    }
}