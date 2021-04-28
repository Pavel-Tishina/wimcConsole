package com.tishina.wimcConsole.obj;


import java.io.File;
import java.util.Map;

import com.tishina.wimcConsole.utils.FileDirUtils;
import org.json.JSONObject;


public class FileRecord {
    public static String name;
    public static String path;
    public static String md5Path;
    public static String md5;
    public static long size;
    public static boolean main;
    public static boolean hold;
    public static boolean move;
    public static boolean del;

    public FileRecord(File f, String md5RootDir) {
        if (f.exists() && f.isFile()) {
            name = f.getName();
            path = f.getAbsolutePath();

            Map<String, String> md5Map = FileDirUtils.getMd5Path(f, md5RootDir);
            md5Path = md5Map.get("md5Dir");
            size = f.length();
            md5 = md5Map.get("md5");

            main = false;
            hold = false;
            move = true;
            del = false;
        }
        else {
            name = "";
            path = "";
            md5Path = "";
            size = 0;
            md5 = "";

            main = false;
            hold = false;
            move = false;
            del = false;
        }
    }

    public void setMain() {
        main = true;
        move = false;
        del = false;
        hold = false;
    }

    public JSONObject getAsJSON() {
        JSONObject out = new JSONObject();

        out.put("name", name);
        out.put("path", path);
        out.put("md5Path", md5Path);
        out.put("md5", md5);
        out.put("size", size);
        out.put("main", main);
        out.put("move", move);
        out.put("del", del);
        out.put("hold", hold);

        return out;
    }

}
