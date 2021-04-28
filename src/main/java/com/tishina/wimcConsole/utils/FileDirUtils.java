package com.tishina.wimcConsole.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> getMd5Path(File f, String md5RootPath) {
        Map<String, String> out = new HashMap<>();
        out.put("md5Root", md5RootPath);

        String md5 = getMd5(f);

        out.put("md5", md5);
        out.put("md5Dir", md5RootPath + "/" + md5 + "/" + f.getName());
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


    public static String getMd5(File file) {
        return getMd5(file, 0);
    }

    public static String getMd5(File file, long br) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);

            long _br = (br > 0 && file.length() > br) ? br : file.length();

            byte[] byteArray = new byte[(int) _br];
            int bytesCount = 0;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            };

            fis.close();

            byte[] bytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean mkDirIfNotExist(String path) {
        return new File(path).mkdirs();
    }

}
