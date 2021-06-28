package com.tishina.wimcConsole.obj;


import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.tishina.wimcConsole.utils.FileDirUtils;
import com.tishina.wimcConsole.utils.MetricUtils;
import com.tishina.wimcConsole.utils.ObjectUtils;
import com.tishina.wimcConsole.utils.ProcessUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    public Container(String fileSource) {
        loadContainer(fileSource);
    }

    public void runMakeList(Map<String, Object> argMap) {
        System.out.println("\t== Start Scenario 'Make List' ==");
        List<File> files = new ArrayList<>();

        System.out.print("\t\t start make file list...");
        String[] _dirs = _dirRoot.split(";");

        for (String d : _dirs)
            if (FileDirUtils.isDir(d.trim()))
                // files = FileDirUtils.getFilesList(d.trim());
                files.addAll(FileDirUtils.getFilesList(d.trim()));
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
            System.out.println("\t save to " + _listFile);
            saveListFile();
            System.out.println("\t== End Scenario 'Save to file' ==");
        }

        System.out.println("\t== End Scenario 'Make List' ==");
    }

    public static void saveListFile() {
        JSONObject jsonContainer = getAsJSON();
        FileDirUtils.saveJsonToFile(_listFile, jsonContainer);
    }

    public static void moveDuplicates() {

        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            if (e.getValue().size() > 1)
                for (FileRecord f : e.getValue())
                    if (f.canCopyOrMove()) {
                        Map<String, Object> result = FileDirUtils.moveOrCopyFile(f.getFullPath(), f.getFullMD5Path(), !f.hold, true);
                        f.setWork(ObjectUtils.obj2Work(result.get("result")));

                            /*
                        f.del = FileDirUtils.moveOrCopyFile(f.getFullPath(), f.getFullMD5Path(), !f.hold, true);
                        boolean isFileDel = f.isFileDeleted();

                        if (f.del & isFileDel)
                            f.setDeleted();
                        else if (f.del & !isFileDel)
                            f.setMoved();
                        else if (!f.del & isFileDel)
                            f.setDeleted();
                        else
                            f.setSkip();
                        */
                    }

        saveListFile();
    }

    public static void moveDuplicates(boolean force) {
        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            if (e.getValue().size() > 1)
                for (FileRecord f : e.getValue())
                    if (force || f.canCopyOrMove()) {
                        Map<String, Object> result = FileDirUtils.moveOrCopyFile(f.getFullPath(), f.getFullMD5Path(), !f.hold, true);
                        f.setWork(ObjectUtils.obj2Work(result.get("result")));
                            /*
                        f.del = FileDirUtils.moveOrCopyFile(f.getFullPath(), f.getFullMD5Path(), !f.hold, true);
                        boolean isFileDel = f.isFileDeleted();

                        if (f.del & isFileDel)
                            f.setDeleted();
                        else if (f.del & !isFileDel)
                            f.setMoved();
                        else if (!f.del & isFileDel)
                            f.setDeleted();
                        else
                            f.setSkip();

                         */
                    }

        saveListFile();
    }

    public static void deleteDuplicates() {
        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            if (e.getValue().size() > 1)
                for (FileRecord f : e.getValue())
                    if (f.del && !f.main && !f.isFileDeleted()) {
                        if (FileDirUtils.deleteFile(f.getFullMD5Path()))
                            f.setDeleted();
                        else
                            f.setSkip();
                    }

        saveListFile();
    }

    public static void deleteDuplicates(boolean force) {
        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            if (e.getValue().size() > 1)
                for (FileRecord f : e.getValue())
                    if (!f.main && (force || !f.isFileDeleted())) {
                        if (FileDirUtils.deleteFile(f.getFullMD5Path()))
                            f.setDeleted();
                        else
                            f.setSkip();
                    }

        saveListFile();
    }

    public static void comeBackDuplicates() {
        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            for (FileRecord f : e.getValue())
                if (f.del && !f.main && f.isMoved()) {
                    Map<String, Object> result = FileDirUtils.moveOrCopyFile(f.getFullMD5Path(), f.getFullPath(), false, true);
                    f.setWork(ObjectUtils.obj2Work(result.get("result")));
                }

        for (Map.Entry<String, Set<FileRecord>> e : _recordSet.entrySet())
            for (FileRecord f : e.getValue())
                if ((!f.main && f.isFinish())
                    && (f.isMovedDeleted() || FileDirUtils.deleteFile(f.getFullMD5Path())))
                        f.setDeleted();
                    else
                        f.setNeedWork();

        saveListFile();
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
        JSONObject jsonMD5part = new JSONObject();
        for (Map.Entry<String, Set<FileRecord>> entry : _recordSet.entrySet()) {
            JSONArray recordsArray = new JSONArray();

            for (FileRecord rec : entry.getValue())
                recordsArray.put(rec.getAsJSON());

            String duplicatesKey = String.valueOf(entry.getValue().size());
            JSONObject pt = (jsonMD5part.has(duplicatesKey))
                    ? jsonMD5part.optJSONObject(duplicatesKey)
                    : new JSONObject();

            pt.put(entry.getKey(), recordsArray);

            jsonMD5part.put(duplicatesKey, pt);


            i++;
            if (i % ProjectConst.EVERY_FILE_CHK == 0) {
                System.out.println(ProcessUtils.getProcessInfo(_totalFiles, i));
            }
        }

        out.put("MD5s", jsonMD5part);
        System.out.println("\t== END Scenario 'Convert to JSON' ==");
        return out;
    }

    public static boolean loadContainer(String file) {
        JSONObject content = FileDirUtils.loadFileList(file);

        if (content.has("MD5s")) {
            _recordSet = new HashMap<>();
            _totalFiles = 0;

            JSONObject jsonMD5s = content.getJSONObject("MD5s");
            for (String k : jsonMD5s.keySet()) {
                JSONObject rec = jsonMD5s.optJSONObject(k);
                Map records = getRecordFromJSON(rec);

                _totalFiles += howManyFiles(records);
                _recordSet.putAll(getRecordFromJSON(rec));

            }

            _totalSize = content.optLong("totalSize");
            _totalFiles = content.optLong("totalFiles");
            _md5Check = content.optBoolean("md5Check");
            _md5Root = content.optString("md5Root");
            _dirRoot = content.optString("dirRoot");
            _listFile = file;

            return true;
        }

        return false;
    }

    public static long howManyFiles(Map<String, Set<Object>> records) {
        return records.values().parallelStream().mapToLong(Set::size).sum();
    };

    public static Map<String, Set<FileRecord>> getRecordFromJSON(JSONObject j) {
        Map<String, Set<FileRecord>> out = new HashMap<>();
        for (Map.Entry<String, Object> e : j.toMap().entrySet()) {
            ArrayList<Map<String, Object>> val = (ArrayList<Map<String, Object>>) e.getValue();
            Set<FileRecord> val4Out = new HashSet<>();
            for (Map<String, Object> fileRec : val) {
                FileRecord fr = new FileRecord(fileRec);
                if (fr.isCorrectRecord())
                    val4Out.add(fr);
            }
            out.put(e.getKey(), val4Out);
        }

        return out;
    }

    public static long getDuplicatesSize() {
        long s = 0;

        for (Set<FileRecord> r : _recordSet.values()) {
            long n = r.size();
            if (n > 1) {
                long size = r.toArray(new FileRecord[r.size()])[0].size;
                s += size * (n - 1);
            }
        }

        return s;
    }

    public static long getDuplicatesTotalFiles() {
        long s = 0;
        for (Set<FileRecord> r : _recordSet.values())
            if (r.size() > 1)
                s += r.size() - 1;

        return s;
    }

    /*
    public static Map<String, Set<FileRecord>> getFilteredSetByWork(ProjectConst.WORK work, boolean addUnique) {
        Map<String, Set<FileRecord>> out = new HashMap<>();
        for (Map.Entry<String, Set<FileRecord>> entry : _recordSet.entrySet())
            if ((addUnique || (entry.getValue().size() > 1)))
                out.put(entry.getKey(),
                        entry.getValue()
                                .parallelStream()
                                .filter(e -> e.compareWork(work))
                                .collect(Collectors.toSet())
                );

        return out;
    }

     */


}
