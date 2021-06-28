package com.tishina.wimcConsole.utils;

import com.tishina.wimcConsole.obj.ProjectConst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MD5Utils {
    public final static TextUtils tx = new TextUtils();

    public static Map<String, String> getMd5Path(File f, String md5RootPath, long rb) {
        Map<String, String> out = new HashMap<>();
        out.put("md5Root", md5RootPath);

        String md5 = getMd5(f, rb);

        out.put("md5", md5);
        out.put("md5Dir", md5RootPath + "/" + md5 + "/" + f.getName());
        return out;
    }

    public static Map<String, String> getMd5Path(File f, String md5RootPath, String _md5) {
        Map<String, String> out = new HashMap<>();
        out.put("md5Root", md5RootPath);

        out.put("md5", _md5);
        out.put("md5Dir", md5RootPath + "/" + _md5 + "/" + f.getName());
        return out;
    }

    public static String getMd5(File file) {
        return getMd5(file, 0);
    }

    public static String getMd5(File file, long rb) {
        MessageDigest digest = null;
        if (file.exists() && file.canRead() && file.length() > 0) {
            try {
                digest = MessageDigest.getInstance("MD5");
                FileInputStream fis = new FileInputStream(file);

                long _br = (rb > 0 && file.length() > rb) ? rb : file.length();

                byte[] byteArray = new byte[(int) _br];
                int bytesCount = 0;

                boolean needProcessShow = ProcessUtils.needShowMd5Process(file.length(), rb);
                if (needProcessShow)
                    System.out.println("\n\t work with " + tx.encodeFileName4Console(FileDirUtils.dirSlashConvert(file.getAbsolutePath())));

                long process = 0;
                while ((bytesCount = fis.read(byteArray)) != -1) {
                    digest.update(byteArray, 0, bytesCount);

                    if (needProcessShow) {
                        process += bytesCount;

                        if (((process / file.length()) * 100) % ProjectConst.FSIZE_PROGRESS_PERC == 0) {
                            //System.out.println(ProcessUtils.getMd5ProcessInfo(file.length(), process, ProjectConst.SIZE_IN.MB));
                            ProcessUtils.printProgress(file.length(), process);
                        }
                    }
                };

                fis.close();

                byte[] bytes = digest.digest();

                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++)
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));

                if (needProcessShow) {
                    System.out.println();
                    if (file.length() / ProjectConst.FSIZE_PROGRESS > 20) {
                        System.gc();
                    }
                }

                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }


}
