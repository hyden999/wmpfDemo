package com.hy.wmpfdemo.bean;

public class WxFacePayBean {
    private int code;
    private WxFaceParamBean wxFaceParam;
    private String order_sn;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public WxFaceParamBean getWxFaceParam() {
        return wxFaceParam;
    }

    public void setWxFaceParam(WxFaceParamBean wxFaceParam) {
        this.wxFaceParam = wxFaceParam;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class WxFaceParamBean {
        private String appid;
        private String mch_id;
        private String sub_appid;
        private String sub_mch_id;
        private String store_id;
        private String store_name;
        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getSub_appid() {
            return sub_appid;
        }

        public void setSub_appid(String sub_appid) {
            this.sub_appid = sub_appid;
        }

        public String getSub_mch_id() {
            return sub_mch_id;
        }

        public void setSub_mch_id(String sub_mch_id) {
            this.sub_mch_id = sub_mch_id;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }
    }
}
