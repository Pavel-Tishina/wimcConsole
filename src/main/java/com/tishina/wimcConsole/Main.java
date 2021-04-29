package com.tishina.wimcConsole;


import com.tishina.wimcConsole.obj.Container;
import com.tishina.wimcConsole.utils.FileDirUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] arg) {

        List<String> list = FileDirUtils.getFilesPathList("C:/Program Files (x86)");

        list.forEach(e -> {System.out.println(e);});

        System.out.println(list.size());

        Map<String, Object> argMap = getArgMap(arg);

        //Map<String, String> map = FileDirUtils.getMd5Path(new File("C:/Program Files (x86)/Guitar Pro 5/GP5.exe"), "/_md5");

        Container c = new Container(
                String.valueOf(argMap.get("mode")),
                String.valueOf(argMap.get("out")),
                String.valueOf(argMap.get("in")),
                String.valueOf(argMap.get("md5dir"))
        );

        c.runMakeList(argMap);


    }

    public static Map<String, Object> getArgMap(String[] arg) {
        Map<String, Object> out = new HashMap<>();

        //Pattern pt = Pattern.compile("-(\\w+)='([a-zA-Z\\d\\/\\: ]+)'");
        Pattern pt = Pattern.compile("-(\\w+)='(.*?)'");

        for (String a : arg) {
            Matcher mt = mt = pt.matcher(a);

            if (mt.find())
                out.put(mt.group(1).toLowerCase(), mt.group(2));
        }

        Set<String> error = new HashSet<>();

        if (!out.containsKey("mode"))
            error.add("not found '-mode' argument");

        if (!out.containsKey("in"))
            error.add("not found '-in' argument");

        if (error.isEmpty()) {
            if (!out.containsKey("rb"))
                out.put("rb", "4m");

            if (!out.containsKey("out"))
                out.put("out", out.get("in") + "/md5.json");

            if (!out.containsKey("md5dir"))
                out.put("md5dir", out.get("in") + "/_md5");

            if (!out.containsKey("log"))
                out.put("log", "process");

            if (!out.containsKey("logfile"))
                out.put("logfile", out.get("md5dir") + "/work.log");
        }
        else {
            out.put("error", error);
        }

        return out;
    }

}
