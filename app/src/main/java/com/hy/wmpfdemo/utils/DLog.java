package com.hy.wmpfdemo.utils;

import android.util.Log;

import com.hy.wmpfdemo.BuildConfig;


public class DLog {

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog(String methodName, String log) {
        StringBuilder b = new StringBuilder();
        b.append("[");
        b.append(methodName);
        b.append("]");
        b.append(log);
        return b.toString();
    }

    private static String[] getLogInfo(StackTraceElement[] sElements) {
        String[] infoArr = new String[2];
        StackTraceElement sElement = sElements[1];
        infoArr[0] = sElement.getFileName();
        infoArr[1] = sElement.getMethodName();
        return infoArr;
    }

    public static void e(String message) {
        if (!isDebuggable()) return;
        // Throwable instance must be created before any methods
        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.e(logInfo[0], createLog(logInfo[1], message));
    }

    public static void i(String message) {
        if (!isDebuggable()) return;

        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.i(logInfo[0], createLog(logInfo[1], message));
    }

    public static void d(String message) {
        if (!isDebuggable()) return;

        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.d(logInfo[0], createLog(logInfo[1], message));
    }

    public static void v(String message) {
        if (!isDebuggable()) return;

        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.v(logInfo[0], createLog(logInfo[1], message));
    }

    public static void w(String message) {
        if (!isDebuggable()) return;

        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.w(logInfo[0], createLog(logInfo[1], message));
    }

    public static void wtf(String message) {
        if (!isDebuggable()) return;

        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.wtf(logInfo[0], createLog(logInfo[1], message));
    }

    public static void e(String message, Throwable e) {
        if (!isDebuggable()) return;
        // Throwable instance must be created before any methods
        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.e(logInfo[0], createLog(logInfo[1], message + Log.getStackTraceString(e)));
    }

    public static void e(Throwable e) {
        if (!isDebuggable()) return;
        // Throwable instance must be created before any methods
        String[] logInfo = getLogInfo(new Throwable().getStackTrace());
        Log.e(logInfo[0], createLog(logInfo[1], Log.getStackTraceString(e)));
    }
}
