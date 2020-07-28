package com.hy.wmpfdemo.wmpf;

import java.lang.reflect.Method;

/**
 * @author hy
 * @date 2020-07-08
 */
public class SnUtil {

    public static Method systemProperties_get = null;

    /**
     * 使用反射调用系统隐藏方法get(),获取系统相关属性配置
     *
     * @param key 属性名称
     * @return
     */
    public static String getAndroidOsSystemProperties(String key) {
        String ret;
        try {
            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
                return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    public static String getSn() {
        String property = "ro.serialno";
        return getAndroidOsSystemProperties(property);
    }
}
