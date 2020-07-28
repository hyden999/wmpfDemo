@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection")

package com.hy.wmpfdemo.wmpf

import android.util.Log
import com.tencent.mmkv.MMKV

object DeviceInfo {
    /**
     * NOTE:
     * WARNING: You should never have your app secret stored in client.
     * The following code is a MISTAKE.
     * temp here!
     */

    //TODO 替换成微信开放平台申请的app_id、app_secret
    const val HOST_APP_ID = "" // com.tencent.luggage.demo
    const val APP_SECRET = ""
    //TODO 替换成已上线的小程序app_id
    const val WMP_APP_ID = ""

    //TODO 替换成微信终端合作平台审核通过的设备product_id、model_name
    const val PRODUCT_ID = 12345
    const val MODEL_NAME = ""

    private const val DEFAULT_EXPIRED_TIME_MS = -1L

    private const val TAG = "Constants"
    private val kv = MMKV.mmkvWithID(TAG, MMKV.SINGLE_PROCESS_MODE)

    const val KEY_TEST_PRODUCT_ID = "product_id"

    const val KEY_TEST_DEVICE_ID = "device_id"

    const val KEY_TEST_SIGNATURE = "signature"

    const val KEY_TEST_KEY_VERSION = "key_version"

    const val KEY_EXPIRED_TIME_MS = "expiredTimeMs"

    /**
     * NOTICE HERE!!!
     * set to ture if you want to user your own device info
     */
    const val isInProductionEnv = true

    var expiredTimeMs = DEFAULT_EXPIRED_TIME_MS
        get() = kv.getLong(KEY_EXPIRED_TIME_MS, DEFAULT_EXPIRED_TIME_MS)
        set(value) {
            kv.putLong(KEY_EXPIRED_TIME_MS, value)
            field = value
        }

    var productId: Int = 0
        get() {
            return if (isExpired() || isInProductionEnv) {
                0 // REPLACE YOUR OWN DEVICE INFO
            } else {
                kv.getInt(KEY_TEST_PRODUCT_ID, 0)
            }
        }
        set(value) {
            kv.putInt(KEY_TEST_PRODUCT_ID, value)
            field = value
        }

    var keyVersion: Int = 0
        get() {
            return if (isExpired() || isInProductionEnv) {
                0 // REPLACE YOUR OWN DEVICE INFO
            } else {
                kv.getInt(KEY_TEST_KEY_VERSION, 0)
            }
        }
        set(value) {
            kv.putInt(KEY_TEST_KEY_VERSION, value)
            field = value
        }

    var deviceId: String = ""
        get() {
            return if (isExpired() || isInProductionEnv) {
                "" // REPLACE YOUR OWN DEVICE INFO
            } else {
                kv.getString(KEY_TEST_DEVICE_ID, "")!!
            }
        }
        set(value) {
            kv.putString(KEY_TEST_DEVICE_ID, value)
            field = value
        }

    var signature: String = ""
        get() {
            return if (isExpired() || isInProductionEnv) {
                "" // REPLACE YOUR OWN DEVICE INFO
            } else {
                kv.getString(KEY_TEST_SIGNATURE, "")!!
            }
        }
        set(value) {
            kv.putString(KEY_TEST_SIGNATURE, value)
            field = value
        }

    fun isInited(): Boolean {
        return expiredTimeMs != DEFAULT_EXPIRED_TIME_MS
    }

    fun isExpired(): Boolean {
        val ret = expiredTimeMs > System.currentTimeMillis()
        if (ret) Log.e(TAG, "isExpired: deviceInfo is isExpired or not init")
        return ret
    }

    fun reset() {
        expiredTimeMs = -1
        productId = 0
        deviceId = ""
        signature = ""
        keyVersion = 0
    }
}