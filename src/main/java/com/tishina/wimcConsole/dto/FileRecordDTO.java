package com.tishina.wimcConsole.dto;

public class FileRecordDTO {
    public static String name;
    public static String path;
    public static String md5Path;
    public static String md5;
    public static long size;
    public static boolean main;
    public static boolean hold;
    public static boolean move;
    public static boolean del;

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getPath() {return this.path;}
    public void setPath(String path) {this.path = path;}

    public String getMd5Path() {return this.md5Path;}
    public void setMd5Path(String md5Path) {this.md5Path = md5Path;}

    public String getMd5() {return this.md5;}
    public void setMd5(String md5) {this.md5 = md5;}

    public long getSize() {return this.size;}
    public void setSize(long size) {this.md5 = md5;}

    public boolean getMain() {return this.main;}
    public void setMain(boolean size) {this.md5 = md5;}

    public boolean getHold() {return this.hold;}
    public void setHold(boolean hold) {this.hold = hold;}

    public boolean getDel() {return this.del;}
    public void setDel(boolean move) {this.del = del;}
}