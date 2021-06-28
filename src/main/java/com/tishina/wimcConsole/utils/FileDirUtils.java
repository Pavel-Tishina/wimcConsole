package com.tishina.wimcConsole.utils;

import com.tishina.wimcConsole.obj.ProjectConst;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class FileDirUtils {

    public static List<File> getFilesList(String dir) {
        List<File> out = new ArrayList<>();

        File current = new File(dir);
        try {
            if (current.exists() && current.canRead() && (current.isDirectory() & current.listFiles().length > 0))
                for (File f : current.listFiles()) {
                    if (f != null && f.exists()) {
                        if (f.isDirectory())
                            out.addAll(getFilesList(f.getAbsolutePath()));
                        else
                            out.add(f);
                    }
                }
        }
        catch (Exception e) {
            System.out.println("[ERROR] (FileUtils::getFilesPathList) problem with '" + current.getAbsolutePath() + "' details: " + e.getLocalizedMessage());
        }

        return out;
    }

    public static List<String> getFilesPathList(String dir) {
        List<String> out = new ArrayList<>();

        File current = new File(dir);

        try {
            if (current.exists() && current.canRead() && (current.isDirectory() & current.listFiles().length > 0))
                for (File f : current.listFiles()) {
                    if (f != null && f.exists()) {
                        if (f.isDirectory())
                            out.addAll(getFilesPathList(f.getAbsolutePath()));
                        else
                            out.add(f.getAbsolutePath());
                    }

                }
        }
        catch (Exception e) {
            System.out.println("[ERROR] (FileUtils::getFilesPathList) problem with '" + current.getAbsolutePath() + "' details: " + e.getLocalizedMessage());
        }

        return out;
    }

    public static boolean isExist(String file) {
        return new File(file).exists();
    }

    public static boolean isFile(String file) {
        return new File(file).isFile();
    }

    public static boolean isDir(String file) {
        return new File(file).isDirectory();
    }

    public static void saveJsonToFile(String file, JSONObject json) {
        File f = new File(file);
        try {
            FileOutputStream os = new FileOutputStream(f);

            os.write(json.toString().getBytes());
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveTextToFile(String file, String txt) {
        File f = new File(file);
        try {
            FileOutputStream os = new FileOutputStream(f);

            os.write(txt.getBytes());
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    public static boolean mkDirIfNotExist(String path) {
        return new File(path).mkdirs();
    }

    public static Properties loadConfigProperties() {
        Properties p = new Properties();
        try (InputStream input = new FileInputStream("/resources/config.properties")) {
            p.load(input);
        } catch (Exception e) {
            System.out.println("[ERROR] (FileDirUtils::loadConfigProperties) error of load config file. details:\n" + e.getLocalizedMessage());
        } finally {
            return p;
        }
    }

    public static boolean deleteFile(String file) {
        File f = new File(file);
        // return f.isFile() && f.exists() && f.delete();
        return f.isFile() && f.delete();
    }

    public static boolean deleteDir(String dir) {
        File f = new File(dir);
        return f.isDirectory() && f.exists() && f.delete();
    }

    public static Map<String, Object> moveOrCopyFile(String source, String destPath, boolean isMove, boolean rewrite) {
        File sourceFile = new File(source);
        /*
        File destFile = (new File(destPath).isFile())
            ? new File(destPath)
            : new File(destPath + "/" + sourceFile.getName());
         */
        File destFile = new File(destPath);

        Map<String, Object> out = new HashMap<>();

        if (destFile.exists() && rewrite)
            out.put("rewrite", destFile.delete());
        // else

        /*
        File pathMake = (new File(destPath).isDirectory())
                ? new File(destPath)
                : new File(new File(destPath).getPath());
         */
        //String fName = sourceFile.getName()
        File pathMake = new File(destPath.substring(0, destPath.lastIndexOf(sourceFile.getName())));

        out.put("make dirs", pathMake.mkdirs());


        try {
            // FileReader fis = new FileReader(sourceFile);
            // FileWriter fos = new FileWriter(destFile);
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(destFile);

            if (sourceFile.getFreeSpace() <= sourceFile.length()) {
                out.put("move", false);
                out.put("error", "not enough free space");
                out.put("errorType", "FreeSpace");
                out.put("result", getCopyOrMoveResultOperation(out));
                return out;
            }

            boolean needProcessShow = ProcessUtils.needShowCopyProcess(sourceFile.length());
            long progressPart = (needProcessShow)
                    ? ProcessUtils.getMd5PercentValue(sourceFile.length())
                    : 0;

            byte[] b = new byte[(int) MetricUtils.getInBytes("512k")];
            // char[] b = new char[(int) MetricUtils.getInBytes("512k")];
            int length;
            int curRead = 0;

            String action = (isMove) ? "Move" : "Copy";

            if (needProcessShow)
                System.out.println(
                        "\n\t" + action + " file \"" + sourceFile.getName() + "\" (" + MetricUtils.getAsString(sourceFile.length(), ProjectConst.SIZE_IN.MB) + ")"
                        + "\n\t\t from: " + sourceFile.getPath()
                        + "\n\t\t to: " + destFile.getPath());

            while ((length = fis.read(b)) > 0 ) {
                fos.write(b, 0, length);
                //fos.write(b, curRead, length);

                curRead += length;
                if (needProcessShow) {

                    if (curRead % progressPart == 0)
                        ProcessUtils.printProgress(sourceFile.length(), curRead);
                }
            }

            fis.close();
            fos.flush();
            fos.close();

            if (isMove)
                out.put("del", sourceFile.delete()) ;

            out.put("move", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            out.put("error", e.getLocalizedMessage());
            out.put("errorType", e.getClass().getSimpleName());
            out.put("move", false);
        } catch (IOException e) {
            e.printStackTrace();
            out.put("error", e.getLocalizedMessage());
            out.put("errorType", e.getClass().getSimpleName());
            out.put("move", false);
        }

        out.put("result", getCopyOrMoveResultOperation(out));
        return out;
    }

    public static JSONObject loadFileList(String source) {
        JSONObject out = new JSONObject();

        File f = new File(source);
        try {
            FileInputStream fi = new FileInputStream(f);
            JSONTokener tk = new JSONTokener(fi);

            out = new JSONObject(tk);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static String dirSlashConvert(String path) {
        return path.replaceAll("\\\\+", "/");
    }

    public static ProjectConst.WORK getCopyOrMoveResultOperation(Map<String, Object> map) {
        Boolean deleteSource = (map.containsKey("del"))
                ? ObjectUtils.obj2Boolean(map.get("del"))
                : false;

        Boolean move = (map.containsKey("move"))
                ? ObjectUtils.obj2Boolean(map.get("move"))
                : false;

        Boolean error = (map.containsKey("error"))
                ? ObjectUtils.obj2Boolean(map.get("error"))
                : false;

        if (!error) {
            if (move) {
                if (deleteSource)
                    return ProjectConst.WORK.MOVED;
                else
                    return ProjectConst.WORK.FINISH;
            }
            else
                return ProjectConst.WORK.SKIP;
        }
        else {
            String errorType = map.get("errorType").toString();
            if (errorType.equalsIgnoreCase("Freespace"))
                return ProjectConst.WORK.NEED_WORK;
            else
                return ProjectConst.WORK.SKIP;
        }

    }
}
