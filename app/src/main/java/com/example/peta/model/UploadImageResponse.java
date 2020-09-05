package com.example.peta.model;

public class UploadImageResponse {
    private String status;
    private UploadData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UploadData getData() {
        return data;
    }

    public void setData(UploadData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UploadImageResponse{" +
                "status='" + status + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
