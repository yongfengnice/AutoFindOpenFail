package com.suyf.openfail.excel

import com.suyf.ndkdev.utils.TextUtils

/**
 * @author Created by suyongfeng on 2022/3/10
 */
data class ExcelData(
    var indexNum: String = "",
    var url: String = "",
    var md5: String = "",
    var appVer: String = "",
    var channel: String = "",
    var regUser: String = "",
    var phoneVer: String = "",
    var phoneName: String = ""
) {
    fun isValidData(): Boolean {
        return !TextUtils.isEmpty(url)
                || !TextUtils.isEmpty(appVer)
                || !TextUtils.isEmpty(channel)
                || !TextUtils.isEmpty(regUser)
                || !TextUtils.isEmpty(phoneVer)
                || !TextUtils.isEmpty(phoneName)
    }
}