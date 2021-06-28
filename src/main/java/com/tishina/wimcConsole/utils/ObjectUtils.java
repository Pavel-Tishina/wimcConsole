package com.tishina.wimcConsole.utils;

import com.tishina.wimcConsole.obj.ProjectConst;

public class ObjectUtils {

    public static long obj2Long(Object obj) {
        return (obj != null)
                ? Long.parseLong(obj2String(obj))
                : null;
    }

    public static int obj2Int(Object obj) {
        return (obj != null)
                ? Integer.parseInt(obj2String(obj))
                : null;
    }

    public static String obj2String(Object obj) {
        return (obj != null)
                ? String.valueOf(obj)
                : "";
    }

    public static boolean obj2Boolean(Object obj) {
        return (obj != null)
                ? Boolean.parseBoolean(obj2String(obj))
                : null;
    }

    public static ProjectConst.WORK obj2Work(Object obj) {
        String _v = obj2String(obj);
        if (!_v.isEmpty()) {
            if (_v.equalsIgnoreCase("READY"))
                return ProjectConst.WORK.READY;
            else if (_v.equalsIgnoreCase("FINISH"))
                return ProjectConst.WORK.FINISH;
            else if (_v.equalsIgnoreCase("SKIP"))
                return ProjectConst.WORK.SKIP;
            else if (_v.equalsIgnoreCase("NEED_WORK"))
                return ProjectConst.WORK.NEED_WORK;
            else if (_v.equalsIgnoreCase("DELETED"))
                return ProjectConst.WORK.NEED_WORK;
            else if (_v.equalsIgnoreCase("MOVED"))
                return ProjectConst.WORK.MOVED;
        }

        return ProjectConst.WORK.UNDONE;
    }

}
