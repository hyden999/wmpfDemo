package com.hy.wmpfdemo.wmpf;

import android.content.Context;
import android.os.RemoteException;

import com.hy.wmpfdemo.utils.DLog;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.util.HashMap;
import java.util.Map;

public class WxFaceUtils {

    /**
     * 刷脸初始化
     *
     * @param context
     * @param listener
     */
    public static void initWechatPay(Context context, final WechatPayResponseListener listener) {
        DLog.e("-----initWechatPay-----");
        Map<String, String> m1 = new HashMap<>();
//                m1.put("ip", "192.168.1.1"); //若没有代理,则不需要此行
//                m1.put("port", "8888");//若没有代理,则不需要此行
        WxPayFace.getInstance().initWxpayface(context, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!isSuccessInfo(info)) {
                    if (listener != null) {
                        listener.onFailed();
                    }
                    DLog.e("-----initWechatPay----- 微信刷脸支付初始化失败");
                    return;
                }
                if (listener != null) {
                    listener.onSucceed();
                }
                DLog.e("-----initWechatPay----- 微信刷脸支付初始化完成");
            }
        });
    }

    public static boolean isSuccessInfo(Map info) {
        if (info == null) {
            DLog.e("isSuccessInfo: 调用返回为空, 请查看日志");
            DLog.e("isSuccessInfo: ");
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        String code = (String) info.get("return_code");
        String msg = (String) info.get("return_msg");
        DLog.e("response | getWxpayfaceRawdata " + code + " | " + msg);

        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            DLog.e("调用返回非成功信息, 请查看日志");
            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        DLog.e("调用返回成功");
        return true;
    }

    public interface WechatPayResponseListener {
        void onSucceed();

        void onFailed();
    }

}
