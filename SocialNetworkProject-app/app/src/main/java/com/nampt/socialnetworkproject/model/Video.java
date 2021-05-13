package com.nampt.socialnetworkproject.model;

public class Video {
    private String path;
    private String thumb;
    private long size;
    private long timeInMs;

    public Video(String path, String thumb, long size, long timeInMs) {
        this.path = path;
        this.thumb = thumb;
        this.size = size;
        this.timeInMs = timeInMs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTimeInMs() {
        return timeInMs;
    }

    public void setTimeInMs(long timeInMs) {
        this.timeInMs = timeInMs;
    }

    @Override
    public String toString() {
        return "Video{" +
                "path='" + path + '\'' +
                ", thumb='" + thumb + '\'' +
                ", size=" + size +
                '}';
    }
}
