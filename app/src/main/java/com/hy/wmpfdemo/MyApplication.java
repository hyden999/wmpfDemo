package com.hy.wmpfdemo;

import com.hy.wmpfdemo.wmpf.ActiveStatusManager;
import com.hy.wmpfdemo.wmpf.ApiUtils;
import com.hy.wmpfdemo.wmpf.InvokeTokenHelper;
import com.hy.wmpfdemo.wmpf.WmHttpListener;
import com.tencent.mmkv.MMKV;
import com.tencent.wmpf.app.WMPFApplication;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.interceptor.HttpLogInterceptor;

import org.jetbrains.annotations.NotNull;

public class MyApplication extends WMPFApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        InvokeTokenHelper.INSTANCE.initInvokeToken(this);
        String rootDir = MMKV.initialize(this);

        initLog();
        initNet();

        //重置激活状态
        ActiveStatusManager.Companion.resetActiveStatus();
        //注册激活设备
        ApiUtils.INSTANCE.initDevice(this, new WmHttpListener<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(@NotNull String msg) {
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MMKV.onExit();
    }

    private void initLog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(false);//是否排版显示
        ViseLog.plant(new LogcatTree());//添加打印日志信息到Logcat的树
    }

    private void initNet() {
        ViseHttp.init(this);
        ViseHttp.CONFIG()
                //TODO 配置请求主机地址
                .baseUrl("https://test/")
                .setCookie(true)
                //配置日志拦截器
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY));
    }
}
