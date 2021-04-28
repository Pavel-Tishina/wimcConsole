package com.tishina.wimcConsole.utils;

import com.tishina.wimcConsole.obj.ProjectConst;


public class ProcessUtils {

    public static int getProcent(int max, int cur) {
        return max / 100 * cur;
    }

    public static String getProcessInfo(int max, int cur) {
        return getProcent(max, cur) + "% done... Objects (" + cur + "/" + max + ")";
    }

    public static String getMd5ProcessInfo(long size, byte i, ProjectConst.SIZE_IN size_in) {
        return i * ProjectConst.FSIZE_PROGRESS_PERC + "% md5 reading of " + MetricUtils.getAsString(size, size_in);
    }

    public static boolean needShowMd5Process(long size, long br) {
        return (size >= ProjectConst.FSIZE_PROGRESS) & (br > 0 & size > br);
    }

    public static long getMd5ProcentValue(long size) {
        return size / 100 * ProjectConst.FSIZE_PROGRESS_PERC;
    }
}
