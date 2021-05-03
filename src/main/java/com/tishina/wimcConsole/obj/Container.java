package com.tishina.wimcConsole.obj;


import com.tishina.wimcConsole.utils.FileDirUtils;
import com.tishina.wimcConsole.utils.MetricUtils;
import com.tishina.wimcConsole.utils.ProcessUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Container {
    public static ProjectConst.MODE _work = ProjectConst.MODE.UNDEF;
    public static boolean _md5Check = false;
    public static long _totalSize = 0;
    public static long _totalFiles = 0;
    public static String _dirRoot = "";
    public static String _md5Root = "";
    public static String _listFile = "";

    public static Map<String, Set<FileRecord>> _recordSet = new HashMap<>();

    public Container(String mode, String listFile, String dirRoot, String md5Root) {
        _dirRoot = dirRoot;
        _md5Root = md5Root;
        _listFile = listFile;

        if (mode.toLowerCase().matches(".*md5.*") )
            _work = ProjectConst.MODE.MD5;
        else if (mode.toLowerCase().matches(".*generate.*"))
            _work = ProjectConst.MODE.LIST;
        else
            _work = ProjectConst.MODE.UNDEF;
    }

    public void runMakeList(Map<String, Object> argMap) {
        System.out.println("\t== Start Scenario 'Make List' ==");
        List<File> files = null;

        System.out.print("\t\t start make file list...");
        if (FileDirUtils.isDir(_dirRoot))
            files = FileDirUtils.getFilesList(_dirRoot);
        System.out.println(" ... done");

        long _rb = MetricUtils.getInBytes(String.valueOf(argMap.get("rb")));

        AtomicLong calculated = new AtomicLong();
        if (files != null && !files.isEmpty()) {
            System.out.println("\t\t start md5 calculating");
            _totalFiles = files.size();

            files.forEach(cf -> _totalSize += cf.length());

            AtomicLong j = new AtomicLong();
            files.forEach(curFile -> {
                calculated.addAndGet(curFile.length());
                //System.out.println("\t work with " + curFile.getAbsolutePath());
                FileRecord fr = new FileRecord(curFile, _md5Root, _rb);

                if (_recordSet.containsKey(fr.md5())) {
                    Set<FileRecord> set = _recordSet.get(fr.md5());
                    set.add(fr);
                    _recordSet.put(fr.md5(), set);
                }
                else {
                    fr.setMain();
                    _recordSet.put(fr.md5(), new HashSet<>(Arrays.asList(fr)));
                }


                j.getAndIncrement();

                if (j.get() % ProjectConst.EVERY_FILE_CHK == 0) {
                    //System.out.println(ProcessUtils.getProcessInfo(_totalFiles, j.get()));
                    System.out.println(ProcessUtils.getProcessInfo(_totalSize, calculated.get(), ProjectConst.SIZE_IN.MB));

                    if (j.get() % (ProjectConst.EVERY_FILE_CHK * 4) == 0)
                        System.gc();
                }
            });

            _md5Check = true;

            JSONObject jsonContainer = getAsJSON();

            System.out.println("\t== Start Scenario 'Save to file' ==");
            FileDirUtils.saveJsonToFile(_listFile, jsonContainer);
            System.out.println("\t== End Scenario 'Save to file' ==");
        }

        System.out.println("\t== End Scenario 'Make List' ==");

    }

    public static JSONObject getAsJSON() {
        System.out.println("\t== Start Scenario 'Convert to JSON' ==");

        JSONObject out = new JSONObject();
        out.put("totalSize", _totalSize);
        out.put("totalFiles", _totalFiles);
        out.put("md5Check", _md5Check);
        out.put("mode", _work.toString());
        out.put("md5Root", _md5Root);
        out.put("dirRoot", _dirRoot);

        long i = 0;
        for (Map.Entry<String, Set<FileRecord>> entry : _recordSet.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (FileRecord rec : entry.getValue())
                // jsonArray.put(rec.getAsJSON());
                jsonArray.put(rec.getAsJSON());

            out.put(entry.getKey(), jsonArray);

            i++;
            if (i % ProjectConst.EVERY_FILE_CHK == 0) {
                System.out.println(ProcessUtils.getProcessInfo(_totalFiles, i));
            }

        }

        System.out.println("\t== END Scenario 'Convert to JSON' ==");
        return out;
    }


}
