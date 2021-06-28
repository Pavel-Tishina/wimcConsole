package com.tishina.wimcConsole.utils;

import com.tishina.wimcConsole.obj.ProjectConst;

import java.util.Collections;


public class ProcessUtils {

    // ******** MD5 ********* //

    public static String getMd5ProcessInfo(long size, long process, ProjectConst.SIZE_IN size_in) {
        return String.format("%.2f", ((float) (process*100) / size)) + "% md5 reading of " + MetricUtils.getAsString(size, size_in);
    }

    public static boolean needShowMd5Process(long size, long br) {
        return (size >= ProjectConst.FSIZE_PROGRESS) & (br > 0 & size > br);
    }

    public static long getMd5PercentValue(long size) {
        return size / 100 * ProjectConst.FSIZE_PROGRESS_PERC;
    }

    // thx for
    // https://coderoad.ru/1001290/%D0%9A%D0%BE%D0%BD%D1%81%D0%BE%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B5%D1%81%D1%81-%D0%B2-Java
    public static void printProgress(long total, long current) {

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies(current == 0 ? (int) (Math.log10(total)) : (int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                //.append(String.format(" %d/%d", MetricUtils.getAsString(current, ProjectConst.SIZE_IN.MB) , MetricUtils.getAsString(total, ProjectConst.SIZE_IN.MB)));
                .append(String.format(" %d/%d", current, total));

        System.out.print(string);
    }

    // ******** COPY ******** //

    public static boolean needShowCopyProcess(long size) {
        long br = MetricUtils.getInBytes("512k");
        return (size >= ProjectConst.FSIZE_PROGRESS) && (size > br);
    }

    // ******** COMMON ******** //

    public static long getProcent(long max, long cur) {
        // return max / 100 * cur;
        return (cur * 100) / max;
    }

    public static String getProcessInfo(long max, long cur) {
        return String.format("%.2f", ((float) getProcent(max, cur))) + "% done... Objects (" + cur + "/" + max + ")";
    }

    public static String getProcessInfo(long max, long cur, ProjectConst.SIZE_IN size_in) {
        return
                getProcent(max, cur)
                        + "% done... Objects ("
                        + MetricUtils.getAsString(cur, size_in)
                        + "/"
                        + MetricUtils.getAsString(max, size_in)
                        + ")";
    }

    /*
    public static String getMd5ProcessInfo(long size, byte i, ProjectConst.SIZE_IN size_in) {
        return i * ProjectConst.FSIZE_PROGRESS_PERC + "% md5 reading of " + MetricUtils.getAsString(size, size_in);
    }

     */




}
