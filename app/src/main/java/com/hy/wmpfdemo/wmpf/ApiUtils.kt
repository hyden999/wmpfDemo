package com.hy.wmpfdemo.wmpf

import android.annotation.SuppressLint
import android.content.Context
import com.alibaba.fastjson.JSON
import com.hy.wmpfdemo.bean.WxFacePayBean
import com.hy.wmpfdemo.utils.DLog
import com.vise.xsnow.http.ViseHttp
import com.vise.xsnow.http.callback.ACallback

/**
 * @author hy
 * @date 2020-07-07
 */
@SuppressLint("CheckResult")
object ApiUtils {

    val BASE_URL = "https://api.weixin.qq.com/"

    /**
     * 获取接口调用凭据access_token
     */
    val URL_GET_ACCESS_TOKEN = "cgi-bin/token?grant_type=client_credential&appid=${DeviceInfo.HOST_APP_ID}&secret=${DeviceInfo.APP_SECRET}"
    /**
     * 小程序框架注册接口
     */
    var URL_REGISTER_DEVICE = "wxa/business/runtime/adddevice?access_token="

    var open_id = ""
    var nickname = ""

    val store_id = ""

    fun getWxParameters(listener: WmHttpListener<String>) {
        //TODO 从公司服务端获取的微信刷脸参数
        ViseHttp.POST("")
                .request<String>(object : ACallback<String>() {
                    override fun onSuccess(data: String) {
                        listener.onSuccess(data)
                    }

                    override fun onFail(errCode: Int, errMsg: String) {
                        listener.onFail(errMsg)
                    }
                })
    }

    fun getAuthInfo(rawdata: String, listener: WmHttpListener<String>) {
        //TODO 从公司服务端获取的AuthInfo
        ViseHttp.POST("")
                .addParam("rawdata", rawdata)
                .request<String>(object : ACallback<String>() {
                    override fun onSuccess(data: String) {
                        listener.onSuccess(data)
                    }

                    override fun onFail(errCode: Int, errMsg: String) {
                        listener.onFail(errMsg)
                    }
                })
    }

    /**
     * 获取access_token
     */
    fun getAccessToken(listener: WmHttpListener<AccessTokenResponse>) {
        ViseHttp.GET(URL_GET_ACCESS_TOKEN)
                .baseUrl(BASE_URL)
                .request<String>(object : ACallback<String?>() {
                    override fun onSuccess(data: String?) {
                        DLog.e("getAccessToken wmpf success: $data")
                        val result = JSON.parseObject(data, AccessTokenResponse::class.java)
                        if (result.errcode == 0) {
                            listener.onSuccess(result)
                        } else {
                            listener.onFail(result?.errmsg
                                    ?: "getAccessToken fail code: ${result.errcode}")
                        }
                    }

                    override fun onFail(errCode: Int, errMsg: String?) {
                        listener.onFail("getAccessToken fail: $errMsg")
                    }
                })
    }

    /**
     * 注册设备
     */
    fun registerDevice(access_token: String, listener: WmHttpListener<String>) {
        val params = HashMap<String, Any>()
        params["product_id"] = DeviceInfo.PRODUCT_ID
        params["device_id_list"] = arrayListOf<String>(SnUtil.getSn())
        params["model_name"] = DeviceInfo.MODEL_NAME
        DLog.e("request params: ${JSON.toJSONString(params)}")

        ViseHttp.POST(URL_REGISTER_DEVICE + access_token)
                .baseUrl(BASE_URL)
                .setJson(JSON.toJSONString(params))
                .request<String>(object : ACallback<String?>() {
                    override fun onSuccess(data: String?) {
                        DLog.e("registerDevice wmpf success: $data")
                        val obj = JSON.parseObject(data)
                        val errcode = obj.getIntValue("errcode")
                        if (errcode == 0) {
                            listener.onSuccess(data!!)
                        } else {
                            listener.onFail(obj.getString("errmsg"))
                        }
                    }

                    override fun onFail(errCode: Int, errMsg: String?) {
                        DLog.e("registerDevice onFailed: $errMsg")
                        listener.onFail(errMsg!!)
                    }
                })
    }

    /**
     * 激活设备
     */
    fun activateDeviceByIoT(context: Context, listener: WmHttpListener<String>) {
        Api.activateDeviceByIoT(DeviceInfo.HOST_APP_ID)
                .subscribe({
                    DLog.e("activateDeviceByIoT wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    DLog.e("activateDeviceByIoT wmpf success: ${it.invokeToken} ")
                    InvokeTokenHelper.initInvokeToken(context, it.invokeToken)
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess(it.invokeToken)
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("activateDeviceByIoT wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 获取设备激活状态
     */
    fun activeStatus(listener: WmHttpListener<Boolean>) {
        Api.activeStatus()
                .subscribe({
                    DLog.e("activeStatus wmpf isActive = ${it.isActive}")
                    DLog.e("activeStatus wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    DLog.e("activeStatus wmpf success: ${it.isActive} ")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess(it.isActive)
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("activeStatus wmpf error: $it")
                    DLog.e("activeStatus wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 注入人脸登录信息
     */
    fun initWxPayInfoAuthInfo(context: Context, appid: String, mch_id: String, authinfo: String,
                              listener: WmHttpListener<String>) {
        Api.initWxPayInfoAuthInfo(mapOf(
                "face_authtype" to "FACEPAY" as Any,
                "appid" to appid as Any, //商户号绑定的公众号/小程序
                "mch_id" to mch_id as Any, //商户号
                "store_id" to store_id as Any, //门店编号
//                "out_trade_no" to "商户订单号" as Object,//须与调用支付接口时字段一致，该字段在在face_code_type为"1"时可不填，为"0"时必填
//                "total_fee" to "订单金额(数字)" as Object, // 单位分. 该字段在在face_code_type为"1"时可不填，为"0"时必填
                "authinfo" to authinfo as Any, //调用凭证
                //获取方式参见: get_wxpayface_authinfo[https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/sdk-android.html#%E8%8E%B7%E5%8F%96%E8%B0%83%E7%94%A8%E5%87%AD%E8%AF%81-get-wxpayface-authinfo]
                "ignore_update_pay_result" to "1" as Any //不需要商户App更新支付结果

        ))
                .subscribe({
                    DLog.e("initWxPayInfoAuthInfo wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess("wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("initWxPayInfoAuthInfo wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 人脸登录
     */
    fun authorizeFaceLogin(listener: WmHttpListener<String>) {
        Api.authorizeFaceLogin()
                .subscribe({
                    DLog.e("authorizeFaceLogin wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ${it.resultJson}")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess(it.resultJson)
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("authorizeFaceLogin wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 授权状态获取
     */
    fun authorizeStatus(listener: WmHttpListener<Array<Any>>) {
        Api.authorizeStatus()
                .subscribe({
                    DLog.e("authorizeStatus wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    DLog.e("authorizeStatus wmpf success: ${it.isAuthorize} openId: ${it.openId} ")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess(arrayOf(it.isAuthorize, it.openId))
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 注销登录状态
     */
    fun deauthorize(listener: WmHttpListener<String>) {
        Api.deauthorize()
                .subscribe({
                    DLog.e("deauthorize wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg}")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess("deauthorize wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg}")
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("deauthorize wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 启动小程序
     */
    fun launchWxaApp(listener: WmHttpListener<String>) {
        Api.launchWxaApp(DeviceInfo.WMP_APP_ID, "")
                .subscribe({
                    DLog.e("launchWxaApp wmpf success: $it")
                    DLog.e("launchWxaApp wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess("launchWxaApp wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("launchWxaApp wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 关闭小程序
     */
    fun closeWxaApp() {
        Api.closeWxaApp(DeviceInfo.WMP_APP_ID)
                .subscribe({
                    DLog.e("closeWxaApp wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                }, {
                    DLog.e("closeWxaApp wmpf error: $it")
                })
    }

    /**
     * 退出小程序
     */
    fun exitWxaApp() {
        closeWxaApp()
        deauthorize(object : WmHttpListener<String> {
            override fun onSuccess(data: String) {
            }

            override fun onFail(msg: String) {
            }
        })
    }

    /**
     * 预载小程序环境
     */
    fun preloadRuntime(listener: WmHttpListener<String>) {
        Api.preloadRuntime()
                .subscribe({
                    DLog.e("preloadRuntime wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    if (it.baseResponse.ret == 0) {
                        listener.onSuccess("preloadRuntime wmpf success: ${it.baseResponse.ret} ${it.baseResponse.errMsg} ")
                    } else {
                        listener.onFail(it.baseResponse.errMsg)
                    }
                }, {
                    DLog.e("preloadRuntime wmpf error: $it")
                    listener.onFail(it.message!!)
                })
    }

    /**
     * 激活设备之后再执行操作
     */
    fun afterActivate(context: Context, listener: WmHttpListener<String>) {
        if (ActiveStatusManager.wmpfIsDeviceActivate) {
            listener.onSuccess("")
        } else {
            initDevice(context, object : WmHttpListener<String> {
                override fun onSuccess(data: String) {
                    listener.onSuccess("")
                }

                override fun onFail(msg: String) {
                    listener.onFail(msg)
                }
            })
        }
    }

    /**
     * 初始化设备
     */
    fun initDevice(context: Context, listener: WmHttpListener<String>) {
        getAccessToken(object : WmHttpListener<AccessTokenResponse> {
            override fun onSuccess(data: AccessTokenResponse) {
                registerDevice(AccessTokenResponse.access_token
                        ?: "", object : WmHttpListener<String> {
                    override fun onSuccess(data: String) {
                        activateDeviceByIoT(context, object : WmHttpListener<String> {
                            override fun onSuccess(data: String) {
                                ActiveStatusManager.wmpfIsDeviceActivate = true
                                listener.onSuccess(data)
                            }

                            override fun onFail(msg: String) {
                                ActiveStatusManager.wmpfIsDeviceActivate = false
                                listener.onFail(msg)
                            }
                        })
                    }

                    override fun onFail(msg: String) {
                        listener.onFail(msg)
                    }
                })
            }

            override fun onFail(msg: String) {
                listener.onFail(msg)
            }
        })
    }

    /**
     * 调起小程序刷脸
     */
    fun authorizeFaceLogin(context: Context, wxFacePayBean: WxFacePayBean, authInfo: String, listener: WmHttpListener<String>) {
        initWxPayInfoAuthInfo(context,
                DeviceInfo.WMP_APP_ID, //商户号绑定的公众号/小程序
                wxFacePayBean.wxFaceParam.mch_id,
                authInfo, object : WmHttpListener<String> {
            override fun onSuccess(data: String) {
                authorizeFaceLogin(object : WmHttpListener<String> {
                    override fun onSuccess(data: String) {
                        listener.onSuccess(data)
                    }

                    override fun onFail(msg: String) {
                        listener.onFail(msg)
                    }
                })
            }

            override fun onFail(msg: String) {
                listener.onFail(msg)
            }
        })
    }
}

public class ActiveStatusManager {
    companion object {
        @JvmField
        var wmpfIsDeviceActivate = false

        fun resetActiveStatus() {
            wmpfIsDeviceActivate = false
        }
    }
}

object AccessTokenResponse {
    var access_token: String? = ""
    var expires_in: Int = 0
    var errcode: Int = 0
    var errmsg: String? = ""
}

interface WmHttpListener<T> {
    fun onSuccess(data: T)
    fun onFail(msg: String)
}