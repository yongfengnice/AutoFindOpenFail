package com.suyf.openfail.utils

import com.suyf.ndkdev.utils.TextUtils
import com.suyf.openfail.excel.ExcelConstants
import com.suyf.openfail.excel.ExcelData
import kotlinx.coroutines.*
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author Created by suyongfeng on 2022/3/10
 */
object HttpUtils {
    private const val TIME_OUT = 5000L
    private const val connectionPoolSize = 1000
    private var httpClient: OkHttpClient? = null

    private fun getOkHttpClient(): OkHttpClient {
        if (httpClient == null) {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            builder.readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            builder.writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            builder.connectionPool(ConnectionPool(connectionPoolSize, 30, TimeUnit.MINUTES))
            httpClient = builder.build()
        }
        return httpClient as OkHttpClient
    }

    fun downLoadFile(indexNum: String, filePath: String?, fileUrl: String) {
        println("正在下载第${indexNum}个文件，路径为：" + filePath + ",fileUrl:" + fileUrl)
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        val request: Request = Request.Builder().url(fileUrl)
            .header(
                "Cookie",
                "uzone=CN-HN; ulocale=zh-CN; __utmz=143991283.1622708724.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_bf5ecb3e49c79396056e2fb62093655e=1623242813,1625811722; Hm_lvt_6bb63e416c91510a666853fe6e7935df=1629797550,1631160101; __utma=143991283.1055497077.1622708724.1622708724.1631160115.2; UM_distinctid=17d46ca290d95f-0ac4469cdc8ef2-57b1a33-232800-17d46ca290e9a2; twiceverify_flag=ncrIEhQOq7VFF9k7dgeb8_hzFMlQV2u2GkEeF466S7Hn5MqXuPGuBQsmo68AXA10; wps_sid=V02Sveuq2sabUGFZplP2dJn5lc0riR000a53f674004559c779; admin-get_mfa_ssid=6756efd80b23835c31718aa152cd3387cf5fb8de1b08cd620cc46bf28f56d5eec49f486469461307211cb7bee473f266ccb7e29304752ad9d4ecfaee2bdbf29d"
            )
            .build()
        val response: Response = getOkHttpClient().newCall(request).execute()
        var byteStream: InputStream? = null
        var fos: FileOutputStream? = null
        var len = 0
        val buf = ByteArray(2048)
        try {
            val total: Long = response.body?.contentLength() ?: 0
            var current: Long = 0
            byteStream = response.body?.byteStream() ?: return
            fos = FileOutputStream(file)
            while (byteStream.read(buf).also { len = it } != -1) {
                current += len.toLong()
                fos.write(buf, 0, len)
            }
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            FileUtils.closeQuietly(byteStream)
            FileUtils.closeQuietly(fos)
        }
    }

    fun downExcelFiles(excelDataList: List<ExcelData>) = runBlocking {
        val asyncList = mutableListOf<Deferred<Unit>>();
        for (item in excelDataList) {
            if (TextUtils.isEmpty(item.url)) {
                continue
            }
            val fileDirPath = ExcelConstants.EXCEL_DOWNLOAD_DIR + item.phoneName
            val fileDir = File(fileDirPath)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val filePath = fileDir.absolutePath + "\\" + item.indexNum + "#" + item.md5 + ".zip"
            val async = GlobalScope.async { downLoadFile(item.indexNum, filePath, item.url) }
            asyncList.add(async)
        }
        asyncList.forEach {
            it.await()
        }
        println("downExcelFiles----execute---finish")
    }
}