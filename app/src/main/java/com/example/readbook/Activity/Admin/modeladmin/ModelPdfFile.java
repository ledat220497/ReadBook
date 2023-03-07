package com.example.readbook.Activity.Admin.modeladmin;

public class ModelPdfFile {
    //variables
    String uid,id,title,description,categoryId,url;
    long timestamp;
    long viewCount;
    long downloadCount;
    boolean favorite;
    boolean read;
    //Constractor empty
    public ModelPdfFile() {
    }
    //Constructor

    public ModelPdfFile(String uid, String id, String title, String description, String categoryId, String url, long timestamp, long viewCount, long downloadCount) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description= description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
    }
    public ModelPdfFile(String uid, String id, String title, String description, String categoryId, String url, long timestamp,  boolean read,long viewCount, long downloadCount) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.read=read;
    }
    //cobtructor params

    public ModelPdfFile(String uid, String id, String title, String description, String categoryId, String url, long timestamp, long viewCount, long downloadCount, boolean favorite) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.favorite = favorite;


    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }




    //Geter and setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(long downloadCount) {
        this.downloadCount = downloadCount;
    }
}
