package com.tishina.wimcConsole.utils;


import com.tishina.wimcConsole.obj.ProjectConst;

public class MetricUtils {

    public static long getInBytes(String s) {
        String _s = s.trim().toLowerCase();
        String _zz = s.replaceAll("[a-zA-Z]+", "");
        long _z = Long.parseLong(_zz);

        if (_s.matches("^\\d+[k|kb]*$"))
            return _z * 1024;
        else if (_s.matches("^\\d+[m|mb]*$"))
            return _z * 1048576;
        else if (_s.matches("^\\d+[g|gb]*$"))
            return _z * 1073741824;
        else
            return _z;
    }

    public static String getAsString(long z, ProjectConst.SIZE_IN size_in) {
        switch (size_in) {
            case KB: return new String(z / 1024 + "kb");
            case MB: return new String(z / 1048576 + "mb");
            case GB: return new String(z / 1073741824 + "gb");
            default: return String.valueOf(z);
        }
    }

}
