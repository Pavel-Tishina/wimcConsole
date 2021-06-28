package com.tishina.wimcConsole.obj;

import com.tishina.wimcConsole.utils.FileDirUtils;
import com.tishina.wimcConsole.utils.MD5Utils;
import com.tishina.wimcConsole.utils.ObjectUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;


public class FileRecord {
    public String name;
    public String path;
    public String md5Path;
    public String md5;
    public long size;
    public boolean main;
    public boolean hold;
    public boolean move;
    public boolean del;

    public ProjectConst.WORK work;

    public FileRecord(File f, String md5RootDir, long rb) {
        if (f.exists() && f.isFile()) {
            this.name = f.getName();
            this.path = FileDirUtils.dirSlashConvert(f.getAbsolutePath());

            Map<String, String> md5Map = MD5Utils.getMd5Path(f, md5RootDir, rb);
            this.md5Path = md5Map.get("md5Dir");
            this.size = f.length();
            this.md5 = (md5Map.get("md5").isEmpty()) ? "zero" : md5Map.get("md5");

            this.main = false;
            this.hold = true;
            this.move = true;
            this.del = false;
        }
        else {
            this.name = "";
            this.path = "";
            this.md5Path = "";
            this.size = 0;
            this.md5 = "";

            this.main = false;
            this.hold = false;
            this.move = false;
            this.del = false;
        }

        this.work = ProjectConst.WORK.UNDONE;
    }

    public FileRecord(File f, String md5RootDir, String MD5) {
        if (f.exists() && f.isFile()) {
            this.name = f.getName();
            this.path = FileDirUtils.dirSlashConvert(f.getAbsolutePath());

            this.md5 = MD5.isEmpty() ? "zero" : MD5;
            Map<String, String> md5Map = MD5Utils.getMd5Path(f, md5RootDir, md5);
            this.md5Path = md5Map.get("md5Dir");
            this.size = f.length();


            this.main = false;
            this.hold = true;
            this.move = true;
            this.del = false;
        }
        else {
            this.name = "";
            this.path = "";
            this.md5Path = "";
            this.size = 0;
            this.md5 = "";

            this.main = false;
            this.hold = false;
            this.move = false;
            this.del = false;
        }

        this.work = ProjectConst.WORK.UNDONE;
    }

    public FileRecord(Map<String, Object> map) {
        this.md5Path = (map.containsKey("md5Path"))
                ? ObjectUtils.obj2String(map.get("md5Path"))
                : null;

        this.md5 = (map.containsKey("md5"))
                ? ObjectUtils.obj2String(map.get("md5"))
                : null;

        this.name = (map.containsKey("name"))
                ? ObjectUtils.obj2String(map.get("name"))
                : null;

        this.path = (map.containsKey("path"))
                ? ObjectUtils.obj2String(map.get("path"))
                : null;

        this.size = (map.containsKey("size"))
                ? ObjectUtils.obj2Long(map.get("size"))
                : 0;

        this.main = (map.containsKey("main"))
                ? ObjectUtils.obj2Boolean(map.get("main"))
                : null;

        this.del = (map.containsKey("del"))
                ? ObjectUtils.obj2Boolean(map.get("del"))
                : null;

        this.hold = (map.containsKey("hold"))
                ? ObjectUtils.obj2Boolean(map.get("hold"))
                : null;

        this.move = (map.containsKey("move"))
                ? ObjectUtils.obj2Boolean(map.get("move"))
                : null;

        this.work = (map.containsKey("work"))
                ? ObjectUtils.obj2Work(map.get("work"))
                : ProjectConst.WORK.UNDONE;
    }

    public void setMain() {
        this.main = true;
        this.move = false;
        this.del = false;
        this.hold = true;
    }

    public JSONObject getAsJSON() {
        JSONObject out = new JSONObject();

        out.put("name", this.name);
        out.put("path", this.path);
        out.put("md5Path", this.md5Path);
        out.put("md5", this.md5);
        out.put("size", this.size);
        out.put("main", this.main);
        out.put("move", this.move);
        out.put("del", this.del);
        out.put("hold", this.hold);
        out.put("work", this.work);

        return out;
    }

    public String md5() {
        return this.md5;
    }

    public String getFullPath() {
        //return this.path.concat("/").concat(this.name);
        return this.path;
    }

    public String getFullMD5Path() {
        //return this.md5Path.concat("/").concat(this.name);
        return this.md5Path;
    }

    public boolean isCorrectRecord() {
        return Objects.nonNull(this.del)
                && Objects.nonNull(this.hold)
                && Objects.nonNull(this.main)
                && Objects.nonNull(this.move)
                && Objects.nonNull(this.md5)
                && Objects.nonNull(this.md5Path)
                && Objects.nonNull(this.name)
                && Objects.nonNull(this.size)
                && Objects.nonNull(this.path);
    }

    public void setFinish() {
        this.work = ProjectConst.WORK.FINISH;
    }

    public void setSkip() {
        this.work = ProjectConst.WORK.SKIP;
    }

    public void setUndone() {
        this.work = ProjectConst.WORK.UNDONE;
    }

    public void setNeedWork() {
        this.work = ProjectConst.WORK.NEED_WORK;
    }

    public void setReady() {
        this.work = ProjectConst.WORK.READY;
    }

    public void setDeleted() {
        this.work = ProjectConst.WORK.DELETED;
    }

    public void setMoved() {
        this.work = ProjectConst.WORK.MOVED;
    }

    public void setWork(ProjectConst.WORK work) {this.work = work;}

    public boolean isMoved() {
        return this.work == ProjectConst.WORK.MOVED;
    }

    public boolean isFinish() {
        return this.work == ProjectConst.WORK.FINISH;
    }

    public boolean isUndone() {
        return this.work != ProjectConst.WORK.FINISH;
    }

    public boolean isNeedMD5() {
        return this.work == ProjectConst.WORK.NEED_WORK;
    }

    public boolean isDelayed() {
        return this.work == ProjectConst.WORK.SKIP;
    }

    public boolean isDeleted() {
        return this.work == ProjectConst.WORK.DELETED;
    }

    public boolean canCopyOrMove() {
        return !this.main && this.move && this.work == ProjectConst.WORK.READY;
    }

    public boolean isFileDeleted() {
        return !FileDirUtils.isExist(this.path);
    }

    public boolean isMovedDeleted() {
        return !FileDirUtils.isExist(this.md5Path);
    }

    public ProjectConst.WORK getWork() {return this.work;}

    public boolean compareWork(ProjectConst.WORK work) {return this.work == work;}
}
