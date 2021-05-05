package com.tishina.wimcConsole.obj;

public class ProjectConst {
    public static final long FSIZE_PROGRESS = 10485760;
    public static final byte FSIZE_PROGRESS_PERC = 20;
    public static final short EVERY_FILE_CHK = 256;

    public static enum SIZE_IN {
        BYTE,
        KB,
        MB,
        GB
    }

    public static enum MODE {
        LIST,
        MD5,
        UNDEF
    }

}
