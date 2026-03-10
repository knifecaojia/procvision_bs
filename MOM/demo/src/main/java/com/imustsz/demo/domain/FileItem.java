package com.imustsz.demo.domain;

public class FileItem {
    private String fileName;
    private String fileData; // 假设这里是 Base64 字符串

    // Getter & Setter
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileData() { return fileData; }
    public void setFileData(String fileData) { this.fileData = fileData; }
}