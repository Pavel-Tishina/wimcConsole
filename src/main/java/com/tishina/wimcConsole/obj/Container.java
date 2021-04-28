package com.tishina.wimcConsole.obj;


import java.util.HashSet;
import java.util.Set;

public class Container {
    public static ProjectConst.MODE _work = ProjectConst.MODE.UNDEF;
    public static boolean _md5Check = false;
    public static long _totalSize = 0;
    public static long _totalFiles = 0;
    public static String _dirRoot = "";
    public static String _md5Root = "";

    public static Set<FileRecord> recordSet = new HashSet<>();

    public Container(String mode, String dirRoot, String md5Root) {
        _dirRoot = dirRoot;
        _md5Root = md5Root;

        if (mode.toLowerCase().matches(".*md5.*") )
            _work = ProjectConst.MODE.MD5;
        else if (mode.toLowerCase().matches(".*generate.*"))
            _work = ProjectConst.MODE.LIST;
        else
            _work = ProjectConst.MODE.UNDEF;
    }

    public void runMakeList() {

    }


}
