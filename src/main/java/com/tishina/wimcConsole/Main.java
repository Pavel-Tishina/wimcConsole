package com.tishina.wimcConsole;


import com.tishina.wimcConsole.obj.Container;
import com.tishina.wimcConsole.utils.FileDirUtils;
import com.tishina.wimcConsole.utils.TextUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] arg) {

        // List<String> list = FileDirUtils.getFilesPathList("C:/Program Files (x86)");

        // list.forEach(e -> {System.out.println(e);});

        // System.out.println(list.size());

        Map<String, Object> argMap = getArgMap(arg);

        String tt = "Игра!";

        String txt = "";
        txt += "UTF-8 : " + TextUtils.encodeFromTo("UTF-8", "UTF-8", tt);
        // txt += "\n" + "CP1251 : " + TextUtils.encodeFromTo3("UTF-8", "CP1251", tt);
        txt += "\n" + "CP1251 : " + 
        		TextUtils.encodeFromTo3("CP1251", "UTF-8", 
        				TextUtils.encodeFromTo3("UTF-8", "CP1251", tt));
        
        FileDirUtils.saveTextToFile("/home/mama/workspace_mars/text.txt", txt);

    }
    
    public static void modeSelector(Map<String, Object> argMap) {
    	String mode = (argMap.containsKey("mode")) 
    			? String.valueOf(argMap.get("mode"))
    			: "'mode' arg is not found!";
    			
    
    	if (mode.equalsIgnoreCase("generate")) {
    		generateScenario(argMap);
    	}
    	else if (mode.equalsIgnoreCase("move")) {
    		
    	}
    	else
    		System.out.print(mode);
    }
    
    public static void generateScenario(Map<String, Object> argMap) {
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
