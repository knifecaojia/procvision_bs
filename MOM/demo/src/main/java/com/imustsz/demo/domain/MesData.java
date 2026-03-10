package com.imustsz.demo.domain;

import java.util.List;

public class MesData {
    private String oriSysName;
    private String oriSysNum;
    private String uniqueFlag;
    private String timestamp;
    private List<FileItem> fileData;


    public String getOriSysName() { return oriSysName; }
    public void setOriSysName(String oriSysName) { this.oriSysName = oriSysName; }

    public String getOriSysNum() { return oriSysNum; }
    public void setOriSysNum(String oriSysNum) { this.oriSysNum = oriSysNum; }

    public String getUniqueFlag() { return uniqueFlag; }
    public void setUniqueFlag(String uniqueFlag) { this.uniqueFlag = uniqueFlag; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<FileItem> getFileData() { return fileData; }
    public void setFileData(List<FileItem> fileData) { this.fileData = fileData; }
}