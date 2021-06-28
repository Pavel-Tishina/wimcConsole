package com.tishina.wimcConsole;


import com.tishina.wimcConsole.obj.Container;
import com.tishina.wimcConsole.obj.ProjectConst;
import com.tishina.wimcConsole.utils.MetricUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] arg) {

        // List<String> list = FileDirUtils.getFilesPathList("C:/Program Files (x86)");

        // list.forEach(e -> {System.out.println(e);});

        // System.out.println(list.size());

        Map<String, Object> argMap = getArgMap(arg);

        //Container c = new Container("generate", "C:/Users/Pavel/Documents/shareftp/list.json", "C:/Users/Pavel/Documents/shareftp", "C:/Users/Pavel/Documents/shareftp/_md5");
        // Container c = new Container("generate", "H:/list.json", "H:/", "H:/_md5");
        Container c = new Container("generate", "F:/test/list.json", "F:/test/1;F:/test/2", "F:/test/_md5");
        c.runMakeList(argMap);
        // Container c = new Container("C:/Users/Pavel/Documents/shareftp/list.json");

        // Container c = new Container("F:/test/list.json");
        // c.moveDuplicates();
        // c.deleteDuplicates(true);

        //System.out.println("Total files: " + c._totalFiles);

        // Container c = new Container("H:/list.json");
        long s = c.getDuplicatesSize();
        System.out.println("Duplicates: " + c.getDuplicatesTotalFiles() + " files\n"
            + MetricUtils.getAsString(s, ProjectConst.SIZE_IN.GB) + "\n"
            + MetricUtils.getAsString(s, ProjectConst.SIZE_IN.MB)
        );


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
