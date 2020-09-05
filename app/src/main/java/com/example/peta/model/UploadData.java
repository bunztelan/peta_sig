package com.example.peta.model;

public class UploadData {
    private String filelocation;
    private String size;

    public String getFilelocation() {
        return filelocation;
    }

    public void setFilelocation(String filelocation) {
        this.filelocation = filelocation;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "UploadData{" +
                "file_location='" + filelocation + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
