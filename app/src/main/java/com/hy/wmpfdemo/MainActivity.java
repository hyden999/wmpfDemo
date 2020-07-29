package com.hy.wmpfdemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hy.wmpfdemo.wmpf.event.WmpfEvent;
import com.hy.wmpfdemo.bean.WxFacePayBean;
import com.hy.wmpfdemo.utils.DLog;
import com.hy.wmpfdemo.wmpf.ApiUtils;
import com.hy.wmpfdemo.wmpf.WmHttpListener;
import com.hy.wmpfdemo.wmpf.WxFaceUtils;
import com.hy.wmpfdemo.wmpf.contentprovider.ThirdPartContentProvider;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.vise.xsnow.event.BusManager;
import com.vise.xsnow.event.Subscribe;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        ApiUtils.INSTANCE.preloadRuntime(new WmHttpListener<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(@NotNull String msg) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusManager.getBus().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BusManager.getBus().register(this);

        findViewById(R.id.btn_face).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFaceOnClick();
            }
        });
    }

    private void btnFaceOnClick() {
        WxFaceUtils.initWechatPay(this, new WxFaceUtils.WechatPayResponseListener() {
            @Override
            public void onSucceed() {
                ApiUtils.INSTANCE.getWxParameters(new WmHttpListener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        WxFacePayBean wxFacePayBean = JSON.parseObject(data, WxFacePayBean.class);
                        if (wxFacePayBean != null && wxFacePayBean.getCode() == 1
                                && wxFacePayBean.getWxFaceParam() != null
                                && wxFacePayBean.getWxFaceParam().getStatus() == 1) {
                            getRawData(wxFacePayBean);
                        }
                    }

                    @Override
                    public void onFail(@NotNull String msg) {
                        DLog.e("getWxParameters onFail：" + msg);
                    }
                });
            }

            @Override
            public void onFailed() {
                DLog.e("initWechatPay onFail");
            }
        });
    }

    private void getRawData(final WxFacePayBean wxFacePayBean) {
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!WxFaceUtils.isSuccessInfo(info)) {
                    return;
                }
                try {
                    String rawData = info.get("rawdata").toString();
                    ApiUtils.INSTANCE.getAuthInfo(rawData, new WmHttpListener<String>() {
                        @Override
                        public void onSuccess(String data) {
                            JSONObject result = JSON.parseObject(data);
                            int code = result.getInteger("code");
                            if (code == 1) {
                                String mAuthInfo = result.getString("authInfo");
                                //接入小程序框架，调用小程序刷脸
                                wmpfFace(wxFacePayBean, mAuthInfo);
                            }
                        }

                        @Override
                        public void onFail(@NotNull String msg) {
                            DLog.e("getAuthInfo onFail：" + msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void wmpfFace(final WxFacePayBean wxFacePayBean, final String authInfo) {
        //先清除旧的登录状态
        ApiUtils.INSTANCE.setOpen_id("");
        ApiUtils.INSTANCE.setNickname("");
        ApiUtils.INSTANCE.deauthorize(new WmHttpListener<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFail(@NotNull String msg) {
            }
        });

        ApiUtils.INSTANCE.afterActivate(this, new WmHttpListener<String>() {
            @Override
            public void onSuccess(String data) {
                authorizeFaceLogin(wxFacePayBean, authInfo);
            }

            @Override
            public void onFail(@NotNull String msg) {
                DLog.e("afterActivate onFail：" + msg);
            }
        });
    }

    private void authorizeFaceLogin(final WxFacePayBean wxFacePayBean, final String authInfo) {
        //TODO 替换店铺id
        String store_id = "";
        ApiUtils.INSTANCE.authorizeFaceLogin(this, wxFacePayBean,
                store_id,
                authInfo, new WmHttpListener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        DLog.e("wmpf authorizeFaceLogin: " + data);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject obj = JSON.parseObject(data);
                            String openid = obj.getString("openid");
                            String nickname = obj.getString("nickname");
                            ApiUtils.INSTANCE.setOpen_id(openid);
                            ApiUtils.INSTANCE.setNickname(nickname);

                            //启动小程序
                            ApiUtils.INSTANCE.launchWxaApp(new WmHttpListener<String>() {
                                @Override
                                public void onSuccess(String data) {
                                }

                                @Override
                                public void onFail(@NotNull String msg) {
                                }
                            });
                        } else {
                        }
                    }

                    @Override
                    public void onFail(@NotNull String msg) {
                        DLog.e("authorizeFaceLogin onFail：" + msg);
                    }
                });
    }

    /**
     * 接收ThirdPartContentProvider收到的小程序发来的事件
     *
     * @param event
     */
    @Subscribe
    public void onWmpfEvent(WmpfEvent event) {
        String open_id = ApiUtils.INSTANCE.getOpen_id();
        String nickname = ApiUtils.INSTANCE.getNickname();
        ApiUtils.INSTANCE.exitWxaApp();

        if (!TextUtils.isEmpty(event.getCommand())) {
            switch (event.getCommand()) {
                //获取手机号成功
                case ThirdPartContentProvider
                        .COMMAND_GET_PHONE_NUMBER_SUCCEED:
                    DLog.e("获取手机号成功 open_id：" + open_id + " nickname：" + nickname + " phone：" + event.getSourceData());
                    break;

                //获取手机号失败
                case ThirdPartContentProvider
                        .COMMAND_GET_PHONE_NUMBER_FAILED:
                    DLog.e("获取手机号失败 open_id：" + open_id + " nickname：" + nickname + " phone：" + event.getSourceData());
                    break;
            }
        }
    }
}
